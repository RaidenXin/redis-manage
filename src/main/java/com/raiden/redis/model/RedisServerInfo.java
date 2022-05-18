package com.raiden.redis.model;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 14:41 2022/5/14
 * @Modified By:
 */
public class RedisServerInfo {
    /**
     * Redis 服务器版本
     */
    private String redisVersion;
    /**
     * 服务器模式（standalone，sentinel或者cluster）
     */
    private String redisMode;
    /**
     *  Redis 服务器的随机标识符（用于 Sentinel 和集群）
     */
    private String runId;
    /**
     * TCP/IP 监听端口
     */
    private String tcpPort;
    /**
     * 自 Redis 服务器启动以来，经过的秒数
     */
    private String uptimeInSeconds;
    /**
     * 自 Redis 服务器启动以来，经过的天数
     */
    private String uptimeInDays;

    public String getRedisVersion() {
        return redisVersion;
    }

    public void setRedisVersion(String redisVersion) {
        this.redisVersion = redisVersion;
    }

    public String getRedisMode() {
        return redisMode;
    }

    public void setRedisMode(String redisMode) {
        this.redisMode = redisMode;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(String tcpPort) {
        this.tcpPort = tcpPort;
    }

    public String getUptimeInSeconds() {
        return uptimeInSeconds;
    }

    public void setUptimeInSeconds(String uptimeInSeconds) {
        this.uptimeInSeconds = uptimeInSeconds;
    }

    public String getUptimeInDays() {
        return uptimeInDays;
    }

    public void setUptimeInDays(String uptimeInDays) {
        this.uptimeInDays = uptimeInDays;
    }

    @Override
    public String toString() {
        return "RedisServerInfo{" +
                "redisVersion='" + redisVersion + '\'' +
                ", redisMode='" + redisMode + '\'' +
                ", runId='" + runId + '\'' +
                ", tcpPort='" + tcpPort + '\'' +
                ", uptimeInSeconds='" + uptimeInSeconds + '\'' +
                ", uptimeInDays='" + uptimeInDays + '\'' +
                '}';
    }
}
