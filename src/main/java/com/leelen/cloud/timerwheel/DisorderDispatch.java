package com.leelen.cloud.timerwheel;

import com.leelen.cloud.entity.Subcriber;
import org.springframework.stereotype.Component;

/**
 * @version: 1.00.00
 * @description: 无序执行
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-11-06 13:41
 */
@Component
public class DisorderDispatch extends Dispatch {

    @Override
    public void executorWay(Object event, Subcriber subcriber) {
        subcriber.asynCallback(event);
    }
}
