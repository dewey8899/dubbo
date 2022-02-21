package com.dewey.rpc.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注入一个rpc远程引用
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TRpcReference {
    //TODO 限流相关参数
    String loadBalance() default "RandomLoadBalance";
}
