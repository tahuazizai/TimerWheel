package com.leelen.cloud.entity;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @version: 1.00.00
 * @description: 订阅者
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-10-29 13:18
 */
@Slf4j
public class Subcriber {
    private static final ThreadPoolExecutor TASK_EXECUTOR_THREAD = new ThreadPoolExecutor(10, 20, 180L, TimeUnit.SECONDS, new ArrayBlockingQueue(10000), new ThreadPoolExecutor.DiscardOldestPolicy());

    private Subcriber() {

    }

    private Subcriber(Object target, Method method) {
        this.target = target;
        this.method = method;
    }

    /**
     * 目标对象
     */
    private Object target;
    /**
     * 目标方法
     */
    private Method method;

    /**
     * 回调方法
     *
     * @param t
     * @param <T>
     */
    public <T> void invoke(T t) {
        try {
            method.invoke(target, t);
        } catch (IllegalAccessException e) {
            log.error("回调失败", e);
        } catch (InvocationTargetException e) {
            log.error("回调失败", e);
        }
    }

    /**
     * 异步回调
     *
     * @param t
     * @param <T>
     */
    public <T> void asynCallback(T t) {
        TASK_EXECUTOR_THREAD.execute(() -> invoke(t));
    }

    /**
     * 创建对象
     *
     * @param method
     * @param target
     * @return
     */
    public static Subcriber create(Method method, Object target) {
        return new Subcriber(target, method);
    }
}
