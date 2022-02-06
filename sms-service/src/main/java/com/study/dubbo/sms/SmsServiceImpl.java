package com.study.dubbo.sms;

import com.dewey.rpc.config.annotation.TRpcService;
import com.study.dubbo.sms.api.SmsService;

import java.util.UUID;

/**
 * @auther dewey
 * @date 2022/2/5 22:40
 */
@TRpcService
public class SmsServiceImpl implements SmsService {
    public Object send(String phone, String content) {
        System.out.println("发送短信"+phone +":"+content);
        return String.format("短信发送成功%s", UUID.randomUUID().toString());
    }
}
