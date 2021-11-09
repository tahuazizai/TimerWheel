package com.leelen.cloud.utils;

import com.leelen.cloud.entity.Response;
import com.leelen.cloud.timerwheel.TimingWheel;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @version: 1.00.00
 * @description: 时间轮工具类
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-11-06 18:08
 */
public class TimingWheelUtil {

    private static TimingWheel timingWheel = SpringContextUtils.getBean("timingWheel", TimingWheel.class);
    /**
     * 注册订阅者
     *
     * @param object
     */
    public static void register(Object object) {
        timingWheel.getSubcriberRegister().register(object);
    }

    /**
     * 移除订阅者
     *
     * @param object
     */
    public static void unregister(Object object) {
        timingWheel.getSubcriberRegister().unregister(object);
    }

    /**
     * 开启时间轮
     */
    public static void start() {
        timingWheel.startThread();
    }

    /**
     * 停止时间轮
     */
    public static void stop() {
        timingWheel.stopThread();
    }

    /**
     * 添加任务
     *
     * @param t
     * @param delayTime
     * @param timeUnit
     * @param <T>
     * @return
     */
    public static <T> Response taskAdd(T t, long delayTime, TimeUnit timeUnit) {
        Response response = timingWheel.addTask(t, delayTime, timeUnit);
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
    public static <T> void taskRemove(T t, long executeTime, int tick) {
        timingWheel.removeTask(t, executeTime, tick);
    }

    /**
     * 查询任务
     *
     * @param executeTime
     * @param tick
     * @return
     */
    public static Set<?> taskQuery(long executeTime, int tick) {
        return timingWheel.queryTask(executeTime, tick);
    }
}
