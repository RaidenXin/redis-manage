package com.raiden.redis.pool;

import com.raiden.redis.client.RedisSingleClient;
import com.raiden.redis.client.RedisClientFactory;
import io.netty.channel.EventLoopGroup;
import org.apache.commons.pool2.PooledObjectFactory;


/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 20:38 2022/1/1
 * @Modified By:
 */
public final class RedisSingleClientPool extends AbstractRedisClientPool<RedisSingleClient>{

    public RedisSingleClientPool(String host, int port, int poolSize){
        super(host, port, poolSize);
    }

    @Override
    protected PooledObjectFactory getPooledObjectFactory(RedisClientPool pool, EventLoopGroup group, String host, int port) {
        return RedisClientFactory.build(pool, group, host, port);
    }
}
