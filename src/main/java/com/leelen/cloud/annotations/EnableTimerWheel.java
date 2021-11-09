package com.leelen.cloud.annotations;

import com.leelen.cloud.timerwheel.CustomerImportBeanDefinitionRegistrar;
import com.leelen.cloud.timerwheel.CustomerInterfaceRegistryPostProcesser;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Version: 1.0
 * @Description: 启动时间轮注解
 * @copyright: Copyright (c) 2019 立林科技 All Rights Reserved
 * @company 厦门立林科技有限公司
 * @Author: hj
 * @date: 2021-11-08 08:54
 * @history:
 */

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
@Import(CustomerImportBeanDefinitionRegistrar.class)
public @interface EnableTimerWheel {
    String[] basePackages() default {};
}
