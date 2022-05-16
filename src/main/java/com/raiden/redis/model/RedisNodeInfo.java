package com.raiden.redis.model;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 14:30 2022/5/14
 * @Modified By:
 */
public class RedisNodeInfo {

    private RedisServerInfo server;
    private RedisClientInfo clients;
    private RedisMemoryInfo memory;
    private RedisPersistence persistence;
    private RedisStats stats;
    private ReidsReplication replication;
    private RedisCpuInfo cpu;
    private RedisCluster cluster;
    private RedisKeyspace keyspace;

    public RedisServerInfo getServer() {
        return server;
    }

    public void setServer(RedisServerInfo server) {
        this.server = server;
    }

    public RedisClientInfo getClients() {
        return clients;
    }

    public void setClients(RedisClientInfo clients) {
        this.clients = clients;
    }

    public RedisMemoryInfo getMemory() {
        return memory;
    }

    public void setMemory(RedisMemoryInfo memory) {
        this.memory = memory;
    }

    public RedisPersistence getPersistence() {
        return persistence;
    }

    public void setPersistence(RedisPersistence persistence) {
        this.persistence = persistence;
    }

    public RedisStats getStats() {
        return stats;
    }

    public void setStats(RedisStats stats) {
        this.stats = stats;
    }

    public ReidsReplication getReplication() {
        return replication;
    }

    public void setReplication(ReidsReplication replication) {
        this.replication = replication;
    }

    public RedisCpuInfo getCpu() {
        return cpu;
    }

    public void setCpu(RedisCpuInfo cpu) {
        this.cpu = cpu;
    }

    public RedisCluster getCluster() {
        return cluster;
    }

    public void setCluster(RedisCluster cluster) {
        this.cluster = cluster;
    }

    public RedisKeyspace getKeyspace() {
        return keyspace;
    }

    public void setKeyspace(RedisKeyspace keyspace) {
        this.keyspace = keyspace;
    }

    @Override
    public String toString() {
        return "RedisNodeInfo{" +
                "server=" + server +
                ", clients=" + clients +
                ", memory=" + memory +
                ", persistence=" + persistence +
                ", stats=" + stats +
                ", replication=" + replication +
                ", cpu=" + cpu +
                ", cluster=" + cluster +
                ", keyspace=" + keyspace +
                '}';
    }
}
