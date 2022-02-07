package com.dewey.rpc.rpc;

import lombok.Data;
import lombok.ToString;

/**
 * @auther dewey
 * @date 2022/2/7 21:49
 */
@Data
@ToString
public class Response {
    private Long requestId;//对应请求中携带的messageId
    private int status;//99：异常 0: 正常
    private Object content;//响应内容  - 方法执行结果、异常信息
}
