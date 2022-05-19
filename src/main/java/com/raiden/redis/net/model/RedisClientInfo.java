package com.raiden.redis.net.model;

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
     * 集群之间通讯使用的连接数
     */
    private String clusterConnections;
    /**
     * 最大客户端连接数
     */
    private String maxclients;
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
    /**
     * 被跟踪的客户数量
     */
    private String trackingClients;
    /**
     * 客户端超时表中的客户端数量
     */
    private String clientsInTimeoutTable;

    public String getConnectedClients() {
        return connectedClients;
    }

    public void setConnectedClients(String connectedClients) {
        this.connectedClients = connectedClients;
    }

    public String getClusterConnections() {
        return clusterConnections;
    }

    public void setClusterConnections(String clusterConnections) {
        this.clusterConnections = clusterConnections;
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

    public String getMaxclients() {
        return maxclients;
    }

    public void setMaxclients(String maxclients) {
        this.maxclients = maxclients;
    }

    public String getTrackingClients() {
        return trackingClients;
    }

    public void setTrackingClients(String trackingClients) {
        this.trackingClients = trackingClients;
    }

    public String getClientsInTimeoutTable() {
        return clientsInTimeoutTable;
    }

    public void setClientsInTimeoutTable(String clientsInTimeoutTable) {
        this.clientsInTimeoutTable = clientsInTimeoutTable;
    }

    @Override
    public String toString() {
        return "RedisClientInfo{" +
                "connectedClients='" + connectedClients + '\'' +
                ", clusterConnections='" + clusterConnections + '\'' +
                ", maxclients='" + maxclients + '\'' +
                ", clientLongestOutputList='" + clientLongestOutputList + '\'' +
                ", clientBiggestInputBuf='" + clientBiggestInputBuf + '\'' +
                ", blockedClients='" + blockedClients + '\'' +
                ", trackingClients='" + trackingClients + '\'' +
                ", clientsInTimeoutTable='" + clientsInTimeoutTable + '\'' +
                '}';
    }
}
