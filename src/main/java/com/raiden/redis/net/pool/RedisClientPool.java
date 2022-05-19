package com.raiden.redis.net.pool;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 19:57 2022/5/9
 * @Modified By:
 */
public interface RedisClientPool<T> {

    void recycleObject(T client);
}
