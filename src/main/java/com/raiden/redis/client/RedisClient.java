package com.raiden.redis.client;

import com.raiden.redis.model.RedisNodeInfo;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 22:01 2022/5/10
 * @Modified By:
 */
public interface RedisClient {

    String set(String key,String value);

    String get(String key);

    String[] scan(String startIndex,String limit);

    String[] scanMatch(String startIndex,String pattern,String limit);

    String[] mGet(String... keys);

    String select(String index);

    boolean auth(String password);

    RedisNodeInfo info();

    void close();

    boolean isActive();
}
