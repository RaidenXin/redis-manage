package com.raiden.redis.client;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 22:01 2022/5/10
 * @Modified By:
 */
public interface RedisClient {

    String set(String key,String value);

    String get(String key);

    String[] scan(String startIndex);

    String[] mGet(String... keys);

    String select(String index);

    String auth(String password);

    String info();

    void close();

    boolean isActive();
}
