package com.leelen.cloud.listener;

import com.leelen.cloud.timerwheel.TimingWheel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @version: 1.00.00
 * @description: 送达队列监听
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-08-24 13:16
 */
@Component
@Slf4j
public class TimingWheelListener implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private TimingWheel timingWheel;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        timingWheel.init();
    }

}
