package com.raiden.redis.net.pool;

import com.raiden.redis.net.client.RedisClusterClient;
import com.raiden.redis.net.client.RedisClusterClientFactory;
import io.netty.channel.EventLoopGroup;
import org.apache.commons.pool2.PooledObjectFactory;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 23:56 2022/5/8
 * @Modified By:
 */
public class RedisClusterClientPool extends AbstractRedisClientPool<RedisClusterClient>{

    public RedisClusterClientPool(String host, int port, int poolSize) {
        super(host, port, poolSize);
    }

    @Override
    protected PooledObjectFactory getPooledObjectFactory(RedisClientPool pool, EventLoopGroup group, String host, int port) {
        return RedisClusterClientFactory.build(pool, group, host, port);
    }
}
