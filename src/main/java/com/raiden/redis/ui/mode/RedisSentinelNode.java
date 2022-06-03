package com.raiden.redis.ui.mode;

import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.net.client.RedisSentinelClient;
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
    private String password;
    private boolean isAuth;
    private int port;
    private boolean myself;

    @Override
    public String getHostAndPort() {
        return hostAndPort;
    }

    @Override
    public RedisClient getRedisClient() {
        RedisSentinelClient redisSentinelClient = RedisUtils.getRedisSentinelClient(host, port);
        if (isAuth){
            redisSentinelClient.auth(password);
        }
        return redisSentinelClient;
    }

    @Override
    public boolean isMyself() {
        return myself;
    }

    @Override
    public void clear() {
        RedisUtils.delRedisSentinelClient(host, port);
    }


    @Override
    public void setPassword(String password) {
        this.password = password;
        this.isAuth = true;
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
