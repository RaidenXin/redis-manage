package com.raiden.redis.client;

import com.raiden.redis.handle.RedisClientHandler;
import com.raiden.redis.pool.RedisClientPool;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import org.apache.commons.pool2.PooledObjectFactory;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 20:21 2022/5/9
 * @Modified By:
 */
public class RedisClusterClientFactory extends AbstractRedisClientFactory{

    private RedisClusterClientFactory(EventLoopGroup group, RedisClientPool pool, String host, int port) {
        super(group, pool, host, port);
    }

    @Override
    protected AbstractRedisClient getRedisClient(Channel channel, RedisClientPool pool, RedisClientHandler handler) {
        return new RedisClusterClient(channel, pool, handler);
    }

    public static final PooledObjectFactory build(RedisClientPool pool, EventLoopGroup group, String host, int port){
        return new RedisClusterClientFactory(group, pool, host, port);
    }
}
