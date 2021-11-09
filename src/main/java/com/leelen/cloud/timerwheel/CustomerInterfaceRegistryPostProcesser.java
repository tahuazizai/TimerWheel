package com.leelen.cloud.timerwheel;

import com.google.common.base.Strings;
import com.leelen.cloud.annotations.TimerWheel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.type.filter.TypeFilter;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @version: 1.00.00
 * @description: 自定义注解处理
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-11-06 15:22
 */
@Slf4j
public class CustomerInterfaceRegistryPostProcesser implements PriorityOrdered, BeanDefinitionRegistryPostProcessor {

   private String[] basePackages;


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableListableBeanFactory;
        Set<BeanDefinitionHolder> beanDefinitionHolderSet = this.getBeanDefinitionHolderSet(basePackages);
        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolderSet) {
            try {
                Class<?> cl = Class.forName(beanDefinitionHolder.getBeanDefinition().getBeanClassName());
                SubcriberRegister.getInstance().register(cl.newInstance());
                if (cl.getAnnotations() != null && cl.getAnnotations().length > 0) {
                    for (Annotation annotation : cl.getAnnotations()) {
                        if (annotation instanceof TimerWheel) {
                            TimerWheel timerWheel = (TimerWheel) annotation;
                            registerAliaName(configurableListableBeanFactory, beanDefinitionHolder.getBeanName(), cl.getName());
                            if (!Strings.isNullOrEmpty(timerWheel.value())) {
                                registerAliaName(configurableListableBeanFactory, beanDefinitionHolder.getBeanName(), timerWheel.value());
                            }
                            //为了让autowired注解生效
                            RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(cl);
                            defaultListableBeanFactory.registerBeanDefinition(beanDefinitionHolder.getBeanName(), rootBeanDefinition);
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                log.error("CustomerInterfaceRegistryPostProcesser.postProcessBeanFactory className={}", beanDefinitionHolder.getBeanDefinition().getBeanClassName(), e);
            } catch (InstantiationException e) {
                log.error("CustomerInterfaceRegistryPostProcesser.postProcessBeanFactory className={}", beanDefinitionHolder.getBeanDefinition().getBeanClassName(), e);
            } catch (IllegalAccessException e) {
                log.error("CustomerInterfaceRegistryPostProcesser.postProcessBeanFactory className={}", beanDefinitionHolder.getBeanDefinition().getBeanClassName(), e);
            }
        }
    }

    /**
     * 注册别名
     *
     * @param configurableListableBeanFactory
     * @param beanId
     * @param value
     */
    private void registerAliaName(ConfigurableListableBeanFactory configurableListableBeanFactory, String beanId, String value) {
        if (!configurableListableBeanFactory.containsBeanDefinition(value)) {
            configurableListableBeanFactory.registerAlias(beanId, value);
        }
    }

    /**
     * 获取bean定义
     *
     * @return
     */
    private Set<BeanDefinitionHolder> getBeanDefinitionHolderSet(String[] basePackages) {
        BeanDefinitionRegistry beanDefinitionRegistry = new SimpleBeanDefinitionRegistry();
        CustomerClassPathBeanDefinitionScanner scanner = new CustomerClassPathBeanDefinitionScanner(beanDefinitionRegistry, false);
        TypeFilter typeFilter = (metadataReader, metadataReaderFactory) -> {
            if (metadataReader.getClassMetadata().isConcrete() && metadataReader.getAnnotationMetadata().hasAnnotation(TimerWheel.class.getName())) {
                return true;
            }
            return false;
        };
        scanner.addIncludeFilter(typeFilter);
        return scanner.doScan(basePackages);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    public String[] getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(String[] basePackages) {
        this.basePackages = basePackages;
    }
}
