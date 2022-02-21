package com.study.dubbo;

import com.dewey.rpc.config.spring.annotation.EnableTRPC;
import com.study.dubbo.sms.SmsServiceImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;

/**
 * @auther dewey
 * @date 2022/2/5 22:40
 */
//@Configuration
@ComponentScan("com.study.dubbo")
@PropertySource("classpath:/trpc.properties")
@EnableTRPC
public class SmsApplication {
    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SmsApplication.class);
        context.start();
        SmsServiceImpl bean = context.getBean(SmsServiceImpl.class);
        System.out.println(bean.send("10086","SmsApplication启动时测试一条短信"));
        System.in.read();
        context.close();
    }
}
