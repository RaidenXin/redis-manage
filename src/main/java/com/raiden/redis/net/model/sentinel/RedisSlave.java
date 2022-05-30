package com.raiden.redis.net.model.sentinel;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 11:30 2022/5/29
 * @Modified By:
 */
public class RedisSlave {
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
    private int linkPendingCommands;
    /**
     * 连接数
     */
    private int linkRefcount;
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
     * 主机连接状态
     */
    private String masterLinkStatus;
    /**
     * 主机Host
     */
    private String masterHost;
    /**
     * 主机端口号
     */
    private int masterPort;
    /**
     * 需要同意主节点不可用的Sentinels的数量
     */
    private int quorum;
    /**
     * 从节点优先级
     */
    private int slavePriority;
    /**
     * 从节点读取到的Offset
     */
    private long slaveReplOffset;

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

    public int getLinkPendingCommands() {
        return linkPendingCommands;
    }

    public void setLinkPendingCommands(int linkPendingCommands) {
        this.linkPendingCommands = linkPendingCommands;
    }

    public int getLinkRefcount() {
        return linkRefcount;
    }

    public void setLinkRefcount(int linkRefcount) {
        this.linkRefcount = linkRefcount;
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

    public String getMasterLinkStatus() {
        return masterLinkStatus;
    }

    public void setMasterLinkStatus(String masterLinkStatus) {
        this.masterLinkStatus = masterLinkStatus;
    }

    public String getMasterHost() {
        return masterHost;
    }

    public void setMasterHost(String masterHost) {
        this.masterHost = masterHost;
    }

    public int getMasterPort() {
        return masterPort;
    }

    public void setMasterPort(int masterPort) {
        this.masterPort = masterPort;
    }

    public int getQuorum() {
        return quorum;
    }

    public void setQuorum(int quorum) {
        this.quorum = quorum;
    }

    public int getSlavePriority() {
        return slavePriority;
    }

    public void setSlavePriority(int slavePriority) {
        this.slavePriority = slavePriority;
    }

    public long getSlaveReplOffset() {
        return slaveReplOffset;
    }

    public void setSlaveReplOffset(long slaveReplOffset) {
        this.slaveReplOffset = slaveReplOffset;
    }
}
