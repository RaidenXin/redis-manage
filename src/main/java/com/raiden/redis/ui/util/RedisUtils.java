package com.raiden.redis.ui.util;

import com.raiden.redis.client.RedisClient;
import com.raiden.redis.client.RedisClusterClient;
import com.raiden.redis.client.RedisSingleClient;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 20:04 2022/5/11
 * @Modified By: Redis连接缓冲池
 */
public final class RedisUtils {

    private static final ConcurrentHashMap<String, RedisClient> CLIENT_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, RedisClient> SINGLE_CLIENT_CACHE = new ConcurrentHashMap<>();

    private static final String COLON = ":";

    public static final RedisClient getRedisClusterClient(String host,int port){
        return CLIENT_CACHE.computeIfAbsent(host + COLON + port, (key) -> new RedisClusterClient(host, port));
    }

    public static final RedisClient getRedisSingleClient(String host,int port){
        return SINGLE_CLIENT_CACHE.computeIfAbsent(host + COLON + port, (key) -> new RedisSingleClient(host, port));
    }

    public static synchronized void shutDown(){
        CLIENT_CACHE.values().stream().forEach(RedisClient::close);
        SINGLE_CLIENT_CACHE.values().stream().forEach(RedisClient::close);
    }
}
