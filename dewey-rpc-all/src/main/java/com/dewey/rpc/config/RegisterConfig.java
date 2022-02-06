package com.dewey.rpc.config;

import lombok.Data;

/**
 * @auther dewey
 * @date 2022/2/6 15:14
 * 配置中心
 */
@Data
public class RegisterConfig {
    private String address;
    private String username;
    private String password;
}
