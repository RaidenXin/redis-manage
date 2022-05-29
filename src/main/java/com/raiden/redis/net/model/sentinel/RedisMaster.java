package com.raiden.redis.net.model.sentinel;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 11:29 2022/5/29
 * @Modified By:
 */
public class RedisMaster {
    /**
     * 配置中定义的名称
     */
    private String name;
    private String ip;
    private int port;
    private String runid;
    private String flags;
    /**
     * 等待命令数
     */
    private int pendingCommands;
    /**
     * 最后一次ping发送时间
     */
    private int lastPingSent;
    /**
     * 最后一次ping返回成功的时间
     */
    private int lastOkPingReply;
    /**
     * 最后一次ping返回的时间
     */
    private int lastPingReply;
    /**
     * 监控主节点不可达超时时间
     */
    private int downAfterMilliseconds;
    /**
     * 信息刷新时间
     */
    private int infoRefresh;
    /**
     * 角色
     */
    private String roleReported;
    private long roleReportedTime;
    /**
     * 时代
     */
    private int configEpoch;
    /**
     * 子节点数量
     */
    private int numSlaves;
    /**
     * 发现其他哨兵的数量
     */
    private int numOtherSentinels;
    /**
     * 需要同意主节点不可用的Sentinels的数量
     */
    private int quorum;
    /**
     * 故障转移延迟的时间
     */
    private int failoverTimeout;
    /**
     * 复制转移数量
     */
    private int parallelSyncs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRunid() {
        return runid;
    }

    public void setRunid(String runid) {
        this.runid = runid;
    }

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }

    public int getPendingCommands() {
        return pendingCommands;
    }

    public void setPendingCommands(int pendingCommands) {
        this.pendingCommands = pendingCommands;
    }

    public int getLastPingSent() {
        return lastPingSent;
    }

    public void setLastPingSent(int lastPingSent) {
        this.lastPingSent = lastPingSent;
    }

    public int getLastOkPingReply() {
        return lastOkPingReply;
    }

    public void setLastOkPingReply(int lastOkPingReply) {
        this.lastOkPingReply = lastOkPingReply;
    }

    public int getLastPingReply() {
        return lastPingReply;
    }

    public void setLastPingReply(int lastPingReply) {
        this.lastPingReply = lastPingReply;
    }

    public int getDownAfterMilliseconds() {
        return downAfterMilliseconds;
    }

    public void setDownAfterMilliseconds(int downAfterMilliseconds) {
        this.downAfterMilliseconds = downAfterMilliseconds;
    }

    public int getInfoRefresh() {
        return infoRefresh;
    }

    public void setInfoRefresh(int infoRefresh) {
        this.infoRefresh = infoRefresh;
    }

    public String getRoleReported() {
        return roleReported;
    }

    public void setRoleReported(String roleReported) {
        this.roleReported = roleReported;
    }

    public long getRoleReportedTime() {
        return roleReportedTime;
    }

    public void setRoleReportedTime(long roleReportedTime) {
        this.roleReportedTime = roleReportedTime;
    }

    public int getConfigEpoch() {
        return configEpoch;
    }

    public void setConfigEpoch(int configEpoch) {
        this.configEpoch = configEpoch;
    }

    public int getNumSlaves() {
        return numSlaves;
    }

    public void setNumSlaves(int numSlaves) {
        this.numSlaves = numSlaves;
    }

    public int getNumOtherSentinels() {
        return numOtherSentinels;
    }

    public void setNumOtherSentinels(int numOtherSentinels) {
        this.numOtherSentinels = numOtherSentinels;
    }

    public int getQuorum() {
        return quorum;
    }

    public void setQuorum(int quorum) {
        this.quorum = quorum;
    }

    public int getFailoverTimeout() {
        return failoverTimeout;
    }

    public void setFailoverTimeout(int failoverTimeout) {
        this.failoverTimeout = failoverTimeout;
    }

    public int getParallelSyncs() {
        return parallelSyncs;
    }

    public void setParallelSyncs(int parallelSyncs) {
        this.parallelSyncs = parallelSyncs;
    }
}
