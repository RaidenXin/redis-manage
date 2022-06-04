package com.raiden.redis.ui.mode;

import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.net.client.RedisClusterClient;
import com.raiden.redis.net.model.RedisClusterNodeInfo;
import com.raiden.redis.ui.util.RedisUtils;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 20:57 2022/5/9
 * @Modified By:
 */
public class RedisClusterNode implements RedisNode{

    private String hostAndPort;
    private String host;
    private String password;
    private boolean isAuth;
    private int port;
    private boolean myself;

    @Override
    public boolean isMyself() {
        return myself;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
        this.isAuth = true;
    }


    public String getHostAndPort() {
        return hostAndPort;
    }

    @Override
    public RedisClient getRedisClient() {
        RedisClusterClient redisClusterClient = RedisUtils.getRedisClusterClient(host, port);
        if (isAuth){
            redisClusterClient.auth(password);
        }
        return redisClusterClient;
    }

    @Override
    public String toString() {
        return "RedisClusterNode{" +
                "hostAndPort='" + hostAndPort + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", myself=" + myself +
                '}';
    }

    public static RedisClusterNode build(RedisClusterNodeInfo node){
        RedisClusterNode redisNode = new RedisClusterNode();
        redisNode.host = node.getHost();
        StringBuilder hostAndPort = new StringBuilder();
        hostAndPort.append(node.getHostAndPort());
        redisNode.hostAndPort = hostAndPort.toString();
        redisNode.port = node.getPort();
        redisNode.myself = node.isMyself();
        return redisNode;
    }
}
