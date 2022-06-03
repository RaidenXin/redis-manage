package com.raiden.redis.ui.mode;

import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.net.client.RedisSingleClient;
import com.raiden.redis.net.model.RedisClusterNodeInfo;
import com.raiden.redis.ui.util.RedisUtils;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 20:57 2022/5/9
 * @Modified By:
 */
public class RedisSingleNode implements RedisNode{

    private String hostAndPort;
    private String host;
    private String password;
    private boolean isAuth;
    private int port;
    private boolean myself;

    public String getHostAndPort() {
        return hostAndPort;
    }

    @Override
    public boolean isMyself() {
        return myself;
    }

    @Override
    public void clear() {
        RedisUtils.delRedisSingleClient(host, port);
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
        this.isAuth = true;
    }

    @Override
    public RedisClient getRedisClient() {
        RedisSingleClient redisSingleClient = RedisUtils.getRedisSingleClient(host, port);
        if (isAuth){
            redisSingleClient.auth(password);
        }
        return redisSingleClient;
    }

    @Override
    public String toString() {
        return "RedisSingleNode{" +
                "hostAndPort='" + hostAndPort + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", myself=" + myself +
                '}';
    }

    public static RedisSingleNode build(String host,int port){
        RedisSingleNode redisNode = new RedisSingleNode();
        redisNode.host = host;
        StringBuilder hostAndPort = new StringBuilder();
        hostAndPort.append(host + ":" + port);
        redisNode.hostAndPort = hostAndPort.toString();
        redisNode.port = port;
        redisNode.myself = true;
        return redisNode;
    }

}
