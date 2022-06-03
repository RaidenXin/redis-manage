package com.raiden.redis.ui.mode;

import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.ui.util.RedisUtils;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 22:56 2022/5/28
 * @Modified By:
 */
public class RedisSentinelNode implements RedisNode{

    private String hostAndPort;
    private String host;
    private int port;
    private boolean myself;

    @Override
    public String getHostAndPort() {
        return hostAndPort;
    }

    @Override
    public RedisClient getRedisClient() {
        return RedisUtils.getRedisSentinelClient(host, port);
    }

    @Override
    public boolean isMyself() {
        return myself;
    }


    public static RedisSentinelNode build(String host,int port){
        RedisSentinelNode redisNode = new RedisSentinelNode();
        redisNode.host = host;
        StringBuilder hostAndPort = new StringBuilder();
        hostAndPort.append(host + ":" + port);
        redisNode.hostAndPort = hostAndPort.toString();
        redisNode.port = port;
        return redisNode;
    }

    @Override
    public String toString() {
        return "RedisSentinelNode{" +
                "hostAndPort='" + hostAndPort + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", myself=" + myself +
                '}';
    }
}
