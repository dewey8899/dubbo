package com.study.dubbo.sms.api;

/**
 * @auther dewey
 * @date 2022/2/5 23:07
 */
public interface SmsService {
    Object send(String phone, String content);
}
