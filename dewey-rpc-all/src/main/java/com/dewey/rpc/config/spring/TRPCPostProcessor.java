package com.dewey.rpc.config.spring;

import com.dewey.rpc.common.tools.SpiUtils;
import com.dewey.rpc.config.ProtocolConfig;
import com.dewey.rpc.config.RegisterConfig;
import com.dewey.rpc.config.ServiceConfig;
import com.dewey.rpc.config.annotation.TRpcService;
import com.dewey.rpc.config.util.TrpcBootstrap;
import com.dewey.rpc.remoting.Codec;
import com.dewey.rpc.remoting.Handler;
import com.dewey.rpc.remoting.Transporter;
import com.dewey.rpc.remoting.TrpcChannel;
import com.dewey.rpc.rpc.protocol.Protocol;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.URI;
import java.util.List;

/**
 * @auther dewey
 * @date 2022/2/5 22:57
 * spring扫描初始化对象之后，我要找到里面trpcService
 */
@Slf4j
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
    @SneakyThrows
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //1、服务提供者
        if (bean.getClass().isAnnotationPresent(TRpcService.class)){
            //启动网络服务，接受请求
            log.info("找到了需要开放网络访问的service实现类。启动网络服务，接受请求.构建serviceConfig配置");
            ServiceConfig serviceConfig = new ServiceConfig();
            serviceConfig.addProtocolConfig(applicationContext.getBean(ProtocolConfig.class));
            serviceConfig.addRegistryConfig(applicationContext.getBean(RegisterConfig.class));
            serviceConfig.setReference(bean);
            TRpcService tRpcService = bean.getClass().getAnnotation(TRpcService.class);
            Class<?> anInterface = bean.getClass().getInterfaces()[0];
            if (tRpcService.interfaceClass() == void.class) {
                serviceConfig.setService(anInterface);
            }else {
                serviceConfig.setService(tRpcService.interfaceClass());
            }
            //协议名称 找到 具体协议实现
            serviceConfig.setService(anInterface);
//            String name = serviceConfig.getProtocolConfigs().get(0).getName();
//            Protocol protocol = (Protocol) SpiUtils.getServiceImpl(name, Protocol.class);
            TrpcBootstrap.export(serviceConfig);
        }
        if (bean.getClass().equals(RegisterConfig.class)){
            RegisterConfig config = (RegisterConfig) bean;
            log.info("证明成功加载了配置文件并且spring创建bean:{}",config.getAddress());
        }
        return bean;
    }

}
















