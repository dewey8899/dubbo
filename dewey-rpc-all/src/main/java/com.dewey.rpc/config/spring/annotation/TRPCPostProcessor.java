package com.dewey.rpc.config.spring.annotation;

import com.dewey.rpc.config.annotation.TRpcService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @auther dewey
 * @date 2022/2/5 22:57
 * spring扫描初始化对象之后，我要找到里面trpcService
 */
public class TRPCPostProcessor implements ApplicationContextAware, InstantiationAwareBeanPostProcessor {
    ApplicationContext applicationContext;


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * bean做完，其他拓展
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //1、服务提供者
        if (bean.getClass().isAnnotationPresent(TRpcService.class)){
            //启动网络服务，接受请求
            System.out.println("找到了需要开放网络访问的service实现类。启动网络服务，接受请求");
        }
        return bean;
    }

}
