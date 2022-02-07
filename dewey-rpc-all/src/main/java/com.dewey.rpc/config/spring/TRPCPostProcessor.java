package com.dewey.rpc.config.spring;

import com.dewey.rpc.common.tools.SpiUtils;
import com.dewey.rpc.config.ProtocolConfig;
import com.dewey.rpc.config.RegisterConfig;
import com.dewey.rpc.config.annotation.TRpcService;
import com.dewey.rpc.remoting.Codec;
import com.dewey.rpc.remoting.Handler;
import com.dewey.rpc.remoting.Transporter;
import com.dewey.rpc.remoting.TrpcChannel;
import lombok.SneakyThrows;
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
            System.out.println("找到了需要开放网络访问的service实现类。启动网络服务，接受请求");
            ProtocolConfig protocolConfig = applicationContext.getBean(ProtocolConfig.class);
            String transporterName = protocolConfig.getTransporter();
            Transporter transporter = (Transporter) SpiUtils.getServiceImpl(transporterName, Transporter.class);
            transporter.start(new URI("xxx://127.0.0.1:8080/"), new Codec() {
                @Override
                public byte[] encode(Object msg) throws Exception {
                    return new byte[0];
                }

                @Override
                public List<Object> decode(byte[] message) throws Exception {
                    return null;
                }
            }, new Handler() {
                @Override
                public void onReceive(TrpcChannel trpcChannel, Object message) throws Exception {

                }

                @Override
                public void onWrite(TrpcChannel trpcChannel, Object message) throws Exception {

                }
            });
        }
        if (bean.getClass().equals(RegisterConfig.class)){
            RegisterConfig config = (RegisterConfig) bean;
            System.out.println("证明成功加载了配置文件并且spring创建bean:" + config.getAddress());
        }
        return bean;
    }

}
















