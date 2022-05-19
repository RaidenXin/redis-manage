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

    public static RedisSingleNode build(RedisClusterNodeInfo node){
        RedisSingleNode redisNode = new RedisSingleNode();
        redisNode.host = node.getHost();
        StringBuilder hostAndPort = new StringBuilder();
        if (node.isMaster()){
            hostAndPort.append(RedisNodeType.MASTER);
        }else {
            hostAndPort.append(RedisNodeType.SLAVE);
        }
        hostAndPort.append(":");
        hostAndPort.append(node.getHostAndPort());
        redisNode.hostAndPort = hostAndPort.toString();
        redisNode.port = node.getPort();
        redisNode.myself = node.isMyself();
        return redisNode;
    }

    @Override
    public RedisClient getRedisClient() {
        return RedisUtils.getRedisSingleClient(host, port);
    }
}
