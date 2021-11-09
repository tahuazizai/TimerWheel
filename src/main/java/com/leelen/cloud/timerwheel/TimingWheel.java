package com.leelen.cloud.timerwheel;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.leelen.cloud.entity.Response;
import com.leelen.cloud.entity.Slot;
import com.leelen.cloud.entity.Subcriber;
import com.leelen.cloud.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @version: 1.00.00
 * @description: 时间轮
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-10-27 13:11
 */
@Slf4j
@Component
public class TimingWheel {


    private static final ThreadPoolExecutor SEND_MSG_SERVICE = new ThreadPoolExecutor(10, 20, 180L, TimeUnit.SECONDS, new ArrayBlockingQueue(10000), new ThreadPoolExecutor.DiscardOldestPolicy());


    /**
     * 每次跳动持续时间
     */
    @Value("${timeWheel.tickDuration:1}")
    private Long tickDuration;

    /**
     * 分发服务名
     */
    @Value("${timeWheel.dispatchServiceName:orderDispatch}")
    private String dispatchServiceName;
    /**
     * 时间轮一轮的tick数
     */
    @Value("${timeWheel.ticksPerWheel:60}")
    private Integer ticksPerWheel;

    /**
     * 当前指针所在的tick的下标
     */
    private volatile int currentTickIndex = 0;

    /**
     * 每一路的时间
     */
    private long perWheelTime;

    /**
     * 当前跳跃的次数
     */
    private volatile long tick;


    /**
     * 时间轮开始时间
     */
    private volatile long startTime;

    /**
     * 线程是否执行
     */
    private boolean isRunning = true;
    /**
     * 时间轮集合
     */
    private List<Slot> timerWheelList = Lists.newArrayList();

    /**
     * 时间轮线程
     */
    private Thread timerWheelThread;
    /**
     * 注册订阅者
     */
    private SubcriberRegister subcriberRegister;
    /**
     * 分发任务
     */
    private Dispatch dispatch;

    /**
     * 读写锁
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public SubcriberRegister getSubcriberRegister() {
        return subcriberRegister;
    }

    /**
     * 开启时间轮
     */
    public synchronized void startThread() {
        if (!timerWheelThread.isAlive()) {
            isRunning = true;
            timerWheelThread.start();
        }
    }

    /**
     * 停止时间轮
     */
    public synchronized void stopThread() {
        try {
            isRunning = false;
            if (timerWheelThread.isAlive()) {
                timerWheelThread.interrupt();
                timerWheelThread.join();
            }
        } catch (InterruptedException e) {
            log.error("时间轮停止失败", e);
        }
    }

    /**
     * 初始化
     */
    public void init() {
        tickDuration = TimeUnit.MILLISECONDS.convert(tickDuration, TimeUnit.SECONDS);
        perWheelTime = (ticksPerWheel + 1) * tickDuration;
        timerWheelThread = new Thread(new timerWheelRunnabe(), "timer-wheel");
        for (int i = 0; i < ticksPerWheel + 1; i++) {
            timerWheelList.add(new Slot(i));
        }
        subcriberRegister = SubcriberRegister.getInstance();
        startTime = System.currentTimeMillis();
        dispatch = SpringContextUtils.getBean(dispatchServiceName, Dispatch.class);
        tick = 0;
        timerWheelThread.start();
    }

    /**
     * 添加任务
     *
     * @param t
     * @param <T>
     * @return
     */
    public <T> Response addTask(T t, long delayTime, TimeUnit timeUnit) {
        Preconditions.checkNotNull(t);
        Preconditions.checkNotNull(timeUnit);
        delayTime = TimeUnit.MILLISECONDS.convert(delayTime, timeUnit);
        lock.writeLock().lock();
        int tick1 = -1;
        long executeTime = -1L;
        try {
            tick1 = (int) computeExecuteTime(delayTime);
            executeTime = startTime + (tick - 1) * tickDuration + delayTime;
            log.info("tick={}, executeTime={}, startTime={}", tick1, executeTime, startTime);
            Slot slot = timerWheelList.get(tick1);
            slot.add(t, executeTime);
        } catch (Exception e) {
            log.error("添加任务异常", e);
        } finally {
            lock.writeLock().unlock();
        }
        Response response = Response.builder()
                .executeTime(executeTime)
                .tick(tick1)
                .build();
        return response;
    }

    /**
     * 移除任务
     *
     * @param t
     * @param executeTime
     * @param tick
     * @param <T>
     */
    public <T> void removeTask(T t, long executeTime, int tick) {
        Slot slot = timerWheelList.get(tick);
        Preconditions.checkNotNull(slot);
        lock.writeLock().lock();
        try {
            slot.remove(executeTime, t);
        } catch (Exception e) {
            log.error("移除任务异常", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 查询某个插槽,某个时间点的任务
     *
     * @param executeTime
     * @param tick
     * @return
     */
    public Set<?> queryTask(Long executeTime, int tick) {
        Slot slot = timerWheelList.get(tick);
        lock.readLock().lock();
        Set set = Sets.newCopyOnWriteArraySet();
        try {
            set = slot.get(executeTime);
        } catch (Exception e) {
            log.error("查询任务异常", e);
        } finally {
            lock.readLock().unlock();
        }
        return set;
    }

    /**
     * 计算任务插槽
     *
     * @param delayTime
     * @return
     */
    private long computeExecuteTime(long delayTime) {
        long tick1 = delayTime % perWheelTime == 0 ? currentTickIndex : (delayTime % perWheelTime) / tickDuration + currentTickIndex;
        return tick1 % (perWheelTime / tickDuration);
    }

    /**
     * 通知所有订阅者
     */
    private void notifySubscriber(int currentTickIndex, long executeTime) {
        Slot slot = timerWheelList.get(currentTickIndex);
        CopyOnWriteArraySet copyOnWriteArraySet = slot.get(executeTime);
        slot.remove(executeTime, null);
        SEND_MSG_SERVICE.execute(() -> {
            if (!CollectionUtils.isEmpty(copyOnWriteArraySet)) {
                copyOnWriteArraySet.forEach(event -> {
                    CopyOnWriteArraySet<Subcriber> subcribers = subcriberRegister.getSubcribers(event.getClass());
                    this.dispatch.dispatchTask(event, subcribers);
                });
            }
        });
    }

    /**
     * 等待时间
     *
     * @param startTime
     * @return
     */
    private void waitForTime(long startTime, long tick) throws InterruptedException {
        long restoreTime = tick * tickDuration - (System.currentTimeMillis() - startTime);
        if (restoreTime <= 0) {
            return;
        }
        Thread.sleep(restoreTime);
    }


    public class timerWheelRunnabe implements Runnable {

        @Override
        public void run() {
            try {
                while (isRunning) {
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
//                    lock.writeLock().lock();
                    //通知对应订阅者
                    notifySubscriber(currentTickIndex, startTime + tick * tickDuration);
                    //等待执行时间
                    tick++;
                    waitForTime(startTime, tick);
                    if (currentTickIndex == ticksPerWheel) {
                        currentTickIndex = 0;
                    } else {
                        currentTickIndex++;
                    }
//                    lock.writeLock().unlock();
                }
            } catch (InterruptedException e) {
                log.error("InterruptedException:时间轮停止");
            } catch (Exception e) {
                log.error("时间轮运行异常", e);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }


}
