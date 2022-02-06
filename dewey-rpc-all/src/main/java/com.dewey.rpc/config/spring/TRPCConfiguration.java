package com.dewey.rpc.config.spring;

import com.dewey.rpc.config.ProtocolConfig;
import com.dewey.rpc.config.RegisterConfig;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.reflect.Field;

/**
 * @auther dewey
 * @date 2022/2/6 15:20
 * 如何把自己创建的对象放到spring中管理
 */
public class TRPCConfiguration implements ImportBeanDefinitionRegistrar {
    StandardEnvironment environment;

    public TRPCConfiguration(Environment environment) {
        this.environment = (StandardEnvironment) environment;
    }
    /**
     * 让spring启动的时候，装置没有注解、xml配置
     * @param importingClassMetadata
     * @param registry
     * @param importBeanNameGenerator
     */
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        //告诉spring，让它完成配置对象加载
        BeanDefinitionBuilder beanDefinitionBuilder = null;
        //1.2 ProtocolConfig --读取配置并且赋值
        beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(ProtocolConfig.class);
        for (Field field : ProtocolConfig.class.getDeclaredFields()) {
            String value = environment.getProperty("trpc.protocol." + field.getName());
            beanDefinitionBuilder.addPropertyValue(field.getName(), value);
        }
        registry.registerBeanDefinition("protocolConfig",beanDefinitionBuilder.getBeanDefinition());
        //1.2 RegisterConfig --读取配置并且赋值
        beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(RegisterConfig.class);
        for (Field field : RegisterConfig.class.getDeclaredFields()) {
            String value = environment.getProperty("trpc.registry." + field.getName());//从配置文件找到相匹配的值
            beanDefinitionBuilder.addPropertyValue(field.getName(), value);
        }
        registry.registerBeanDefinition("registerConfig",beanDefinitionBuilder.getBeanDefinition());
    }
}
