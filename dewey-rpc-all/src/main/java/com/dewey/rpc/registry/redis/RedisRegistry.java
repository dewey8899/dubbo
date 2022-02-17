package com.dewey.rpc.registry.redis;

import com.dewey.rpc.common.tools.URIUtils;
import com.dewey.rpc.registry.NotifyListener;
import com.dewey.rpc.registry.RegistryService;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @auther dewey
 * @date 2022/2/16 22:42
 */
public class RedisRegistry implements RegistryService {
    private static final int TIME_OUT = 15;//15秒过期
    URI address;
    ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(5);
    //服务提供者相关
    List<URI> servicesHeartBeat = new ArrayList<>();

    JedisPubSub jedisPubSub;
    Map<String, Set<URI>> localCache = new ConcurrentHashMap<>();
    Map<String, NotifyListener> listenerMap = new ConcurrentHashMap<>();

    @Override
    public void registry(URI uri) {
        String key = "trpc-" + uri.toString();
        Jedis jedis = new Jedis(address.getHost(), address.getPort());
        jedis.setex(key, TIME_OUT, String.valueOf(System.currentTimeMillis()));
        jedis.close();
        //开始心跳
        servicesHeartBeat.add(uri);
    }

    @Override
    public void subscribe(String service, NotifyListener notifyListener) {
        if (localCache.get(service) == null){
            localCache.putIfAbsent(service, new HashSet<>());
            listenerMap.putIfAbsent(service, notifyListener);
            //第一次直接获取
        }
    }

    @Override
    public void init(URI address) {
        this.address = address;
        executorService.scheduleWithFixedDelay(() -> {
            try {
                // -- 心跳 -- 延长时间
                Jedis jedis = new Jedis(address.getHost(), address.getPort());
                for (URI service : servicesHeartBeat) {
                    String key = "trpc-" + service.toString();
                    jedis.expire(key, TIME_OUT);
                }
                jedis.close();
            }catch (Exception e){
                e.printStackTrace();
            }

        }, 3000, 5000, TimeUnit.MILLISECONDS);
        //监听服务变动 - redis conf notify-keyspace-events KE$xg
        executorService.execute(()->{
            jedisPubSub = new JedisPubSub() {
                @Override
                public void onPMessage(String pattern, String channel, String message) {
                    try {
                        URI serviceURI = new URI(channel.replace("__keyspace@0__:trpc-*", ""));
                        if ("set".equals(message)) {
                            Set<URI> uris = localCache.get(URIUtils.getParam(serviceURI, ""));
                            if (uris != null) {
                                uris.add(serviceURI);
                            }
                        }
                        if ("expired".equals(message)) {
                            Set<URI> uris = localCache.get(URIUtils.getParam(serviceURI, ""));
                            if (uris != null) {
                                uris.remove(serviceURI);
                            }
                        }
                        if ("set".equals(message) || "expired".equals(message)) {
                            System.out.println("服务实例有变化，开始刷新");
                            NotifyListener notifyListener = listenerMap.get(URIUtils.getParam(serviceURI, ""));
                            if (notifyListener != null) {
                                notifyListener.notify(localCache.get(URIUtils.getParam(serviceURI,"")));
                            }
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    super.onPMessage(pattern, channel, message);
                }

                @Override
                public void onPSubscribe(String pattern, int subscribedChannels) {
                    System.out.println("注册中心开始监听：" + pattern);
//                    super.onPSubscribe(pattern, subscribedChannels);
                }
            };
        });
    }
}
