package com.leelen.cloud.timerwheel;

import com.leelen.cloud.entity.Subcriber;
import org.springframework.util.CollectionUtils;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @version: 1.00.00
 * @description: 任务分发
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-10-29 13:12
 */
public abstract class Dispatch {

    /**
     * 分发任务
     *
     * @param event
     * @param subcribers
     */
    public void dispatchTask(Object event, CopyOnWriteArraySet<Subcriber> subcribers) {
        if (!CollectionUtils.isEmpty(subcribers)) {
            for (Subcriber subcriber : subcribers) {
                executorWay(event, subcriber);
            }
        }
    }

    /**
     * 执行方式
     *
     * @param event
     * @param subcriber
     */
    public abstract void executorWay(Object event, Subcriber subcriber);
}
