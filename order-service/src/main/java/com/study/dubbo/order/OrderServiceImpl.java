package com.study.dubbo.order;

import com.dewey.rpc.config.annotation.TRpcReference;
import com.dewey.rpc.config.annotation.TRpcService;
import com.study.dubbo.order.api.OrderService;
import com.study.dubbo.sms.api.SmsService;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @auther dewey
 * @date 2022/2/6 14:00
 */
@Service
public class OrderServiceImpl implements OrderService {
    @TRpcReference(loadBalance = "RandomLoadBalance")
    private SmsService smsService;//本质是RPC调用，网络数据传输
    public void create(String orderContent) {
        System.out.println("订单创建成功");
        Object smsResult = smsService.send("10086" + UUID.randomUUID().toString(), orderContent);
        System.out.println("smsService调用结果" + smsResult);
    }
}
