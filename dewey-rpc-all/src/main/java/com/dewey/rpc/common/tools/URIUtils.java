package com.dewey.rpc.common.tools;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * @auther dewey
 * @date 2022/2/5 22:38
 */
public class URIUtils {
    public static String getParam(URI exportUrl, String paramName){
        //TrpcProtocal://127.0.0.1:10088/com.study.dubbo.sms.api.SmsService?transporter=Netty4Transporter&serialization=JsonSerialization
        String query = exportUrl.getQuery();//得到的是 ？后的 参数 ---transporter=Netty4Transporter&serialization=JsonSerialization
        String name = urlSplit(query).get(paramName);
        return name;
    }
    public static String getService(URI exportUrl){
        //TrpcProtocal://127.0.0.1:10088/com.study.dubbo.sms.api.SmsService?transporter=Netty4Transporter&serialization=JsonSerialization
        String path = exportUrl.getPath();// 得到的是   /com.study.dubbo.sms.api.SmsService》
        return path.substring(1);
    }
    /**
     * 获取请求地址中的某个参数
     * @param url
     * @param name
     * @return
     */
//    public static String getParam(String url, String name) {
//        return urlSplit(url).get(name);
//    }

//    /**
//     * 去掉url中的路径，留下请求参数部分
//     * @param url url地址
//     * @return url请求参数部分
//     */
//    private static String truncateUrlPage(String url) {
//        String strAllParam = null;
//        String[] arrSplit = null;
//        url = url.trim().toLowerCase();
//        arrSplit = url.split("[?]");
//        if (url.length() > 1) {
//            if (arrSplit.length > 1) {
//                for (int i = 1; i < arrSplit.length; i++) {
//                    strAllParam = arrSplit[i];
//                }
//            }
//        }
//        return strAllParam;
//    }

    /**
     * 将参数存入map集合
     * @param url  url地址
     * @return url请求参数部分存入map集合
     */
    public static Map<String, String> urlSplit(String url) {
        Map<String, String> mapRequest = new HashMap<>();
        String[] arrSplit = null;
        arrSplit = url.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");
            //解析出键值
            if (arrSplitEqual.length > 1) {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            } else {
                if (arrSplitEqual[0] != "") {
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }

    public static void main(String[] args) throws URISyntaxException {
        URI uri = new URI("TrpcProtocal://127.0.0.1:10088/com.study.dubbo.sms.api.SmsService?transporter=Netty4Transporter&serialization=JsonSerialization");
        String param2 = URIUtils.getParam(uri, "transporter");
        String service = URIUtils.getService(uri);
        System.out.println(service);
        System.out.println(param2);
    }
}
