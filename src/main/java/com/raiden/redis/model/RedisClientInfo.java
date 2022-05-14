package com.raiden.redis.model;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 15:06 2022/5/14
 * @Modified By:
 */
public class RedisClientInfo {
    /**
     * 已连接客户端的数量（不包括通过从属服务器连接的客户端）
     */
    private String connectedClients;
    /**
     * 当前连接的客户端当中，最长的输出列表
     */
    private String clientLongestOutputList;
    /**
     * 当前连接的客户端当中，最大输入缓存
     */
    private String clientBiggestInputBuf;
    /**
     * 正在等待阻塞命令（BLPOP、BRPOP、BRPOPLPUSH）的客户端的数量
     */
    private String blockedClients;

    public String getConnectedClients() {
        return connectedClients;
    }

    public void setConnectedClients(String connectedClients) {
        this.connectedClients = connectedClients;
    }

    public String getClientLongestOutputList() {
        return clientLongestOutputList;
    }

    public void setClientLongestOutputList(String clientLongestOutputList) {
        this.clientLongestOutputList = clientLongestOutputList;
    }

    public String getClientBiggestInputBuf() {
        return clientBiggestInputBuf;
    }

    public void setClientBiggestInputBuf(String clientBiggestInputBuf) {
        this.clientBiggestInputBuf = clientBiggestInputBuf;
    }

    public String getBlockedClients() {
        return blockedClients;
    }

    public void setBlockedClients(String blockedClients) {
        this.blockedClients = blockedClients;
    }
}
