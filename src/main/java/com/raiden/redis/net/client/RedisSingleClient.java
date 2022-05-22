package com.raiden.redis.net.client;

import com.raiden.redis.net.common.RedisCommand;
import com.raiden.redis.net.handle.RedisClientHandler;
import com.raiden.redis.net.pool.RedisClientPool;
import io.netty.channel.Channel;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 23:43 2022/5/8
 * @Modified By:
 */
public class RedisSingleClient extends AbstractRedisClient{

    public RedisSingleClient(String host, int port){
        super(host, port);
    }

    public RedisSingleClient(Channel channel, RedisClientPool pool, RedisClientHandler handler) {
        super(channel, pool, handler);
    }

    public String[] mGet(String... keys){
        String[] commands = new String[keys.length + 1];
        commands[0] = RedisCommand.M_GET;
        System.arraycopy(keys, 0, commands, 0, keys.length);
        return sendCommands(commands);
    }
}
