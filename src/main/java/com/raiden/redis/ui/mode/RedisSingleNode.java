package com.raiden.redis.ui.mode;

import com.raiden.redis.net.client.RedisClient;
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
    private int port;
    private boolean myself;

    public String getHostAndPort() {
        return hostAndPort;
    }

    @Override
    public boolean isMyself() {
        return myself;
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

    @Override
    public RedisClient getRedisClient() {
        return RedisUtils.getRedisSingleClient(host, port);
    }
}
