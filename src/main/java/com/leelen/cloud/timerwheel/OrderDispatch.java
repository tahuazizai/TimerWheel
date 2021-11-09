package com.leelen.cloud.timerwheel;

import com.leelen.cloud.entity.Subcriber;
import org.springframework.stereotype.Component;

/**
 * @version: 1.00.00
 * @description: 有序分发
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-11-06 13:28
 */
@Component
public class OrderDispatch extends Dispatch {

    @Override
    public void executorWay(Object event, Subcriber subcriber) {
        subcriber.invoke(event);
    }
}
