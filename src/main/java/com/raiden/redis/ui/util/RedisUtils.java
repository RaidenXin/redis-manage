package com.raiden.redis.ui.util;

import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.net.client.RedisClusterClient;
import com.raiden.redis.net.client.RedisSentinelClient;
import com.raiden.redis.net.client.RedisSingleClient;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 20:04 2022/5/11
 * @Modified By: Redis连接缓冲池
 */
public final class RedisUtils {

    private static final ConcurrentHashMap<String, RedisClusterClient> CLIENT_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, RedisSingleClient> SINGLE_CLIENT_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, RedisSentinelClient> SENTINE_CLIENT_CACHE = new ConcurrentHashMap<>();

    private static final String COLON = ":";

    public static final RedisClusterClient getRedisClusterClient(String host,int port){
        return CLIENT_CACHE.computeIfAbsent(host + COLON + port, (key) -> new RedisClusterClient(host, port));
    }

    public static final RedisSingleClient getRedisSingleClient(String host,int port){
        return SINGLE_CLIENT_CACHE.computeIfAbsent(host + COLON + port, (key) -> new RedisSingleClient(host, port));
    }

    public static final RedisSentinelClient getRedisSentinelClient(String host,int port){
        return SENTINE_CLIENT_CACHE.computeIfAbsent(host + COLON + port, (key) -> new RedisSentinelClient(host, port));
    }

    public static final void delRedisClusterClient(String host,int port){
        RedisClusterClient client = CLIENT_CACHE.remove(host + COLON + port);
        if (client != null){
            client.close();
        }
    }

    public static final void delRedisSingleClient(String host,int port){
        RedisSingleClient client = SINGLE_CLIENT_CACHE.remove(host + COLON + port);
        if (client != null){
            client.close();
        }
    }

    public static final void delRedisSentinelClient(String host,int port){
        RedisSentinelClient client = SENTINE_CLIENT_CACHE.remove(host + COLON + port);
        if (client != null){
            client.close();
        }
    }

    public static synchronized void shutDown(){
        CLIENT_CACHE.values().stream().forEach(RedisClient::close);
        SINGLE_CLIENT_CACHE.values().stream().forEach(RedisClient::close);
    }
}
