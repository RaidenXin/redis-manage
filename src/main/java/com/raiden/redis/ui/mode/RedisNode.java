package com.raiden.redis.ui.mode;

import com.raiden.redis.model.RedisClusterNode;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 20:57 2022/5/9
 * @Modified By:
 */
public class RedisNode {

    private String hostAndPort;
    private String host;
    private int port;

    public String getHostAndPort() {
        return hostAndPort;
    }

    public void setHostAndPort(String hostAndPort) {
        this.hostAndPort = hostAndPort;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static RedisNode build(RedisClusterNode node){
        RedisNode redisNode = new RedisNode();
        redisNode.host = node.getHost();
        redisNode.hostAndPort = node.getHostAndPort();
        redisNode.port = node.getPort();
        return redisNode;
    }
}
