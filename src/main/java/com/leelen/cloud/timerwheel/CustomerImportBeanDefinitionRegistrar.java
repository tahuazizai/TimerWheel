package com.leelen.cloud.timerwheel;

import com.leelen.cloud.annotations.EnableTimerWheel;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version: 1.00.00
 * @description: 自定义注册类
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-11-08 9:23
 */
public class CustomerImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes mapperScanAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableTimerWheel.class.getName()));
        if (mapperScanAttrs != null) {
            this.registerBeanDefinitions(importingClassMetadata, mapperScanAttrs, registry, generateBaseBeanName(importingClassMetadata, 0));
        }
    }

    private void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, AnnotationAttributes mapperScanAttrs, BeanDefinitionRegistry registry, String beanName) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(CustomerInterfaceRegistryPostProcesser.class);
        List<String> basePackages = new ArrayList();
        basePackages.addAll(Arrays.stream(mapperScanAttrs.getStringArray("basePackages")).filter(StringUtils::hasText).collect(Collectors.toList()));
        if (CollectionUtils.isEmpty(basePackages)) {
            basePackages.add(getDefaultBasePackage(importingClassMetadata));
        }
        builder.addPropertyValue("basePackages", basePackages);
        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
    }

    private static String generateBaseBeanName(AnnotationMetadata importingClassMetadata, int index) {
        return importingClassMetadata.getClassName() + "#" + CustomerImportBeanDefinitionRegistrar.class.getSimpleName() + "#" + index;
    }

    private static String getDefaultBasePackage(AnnotationMetadata importingClassMetadata) {
        return ClassUtils.getPackageName(importingClassMetadata.getClassName());
    }
}
