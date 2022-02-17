package com.study.dubbo;

import com.dewey.rpc.config.spring.annotation.EnableTRPC;
import com.study.dubbo.order.api.OrderService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

@Configuration
@ComponentScan("com.study.dubbo")
@PropertySource("classpath:/trpc.properties")
@EnableTRPC
public class OrderApplication {
    public static void main(String[] args) {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(OrderApplication.class);
        context.start();
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(1);
        for (int i = 0; i < 1; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        cyclicBarrier.await();
                        OrderService orderService = context.getBean(OrderService.class);
                        orderService.create("买一瓶水");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
