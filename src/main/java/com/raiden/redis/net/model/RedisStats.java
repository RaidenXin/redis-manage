package com.raiden.redis.net.model;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 14:08 2022/5/15
 * @Modified By:
 */
public class RedisStats {
    /**
     * 服务器接受的连接总数
     */
    private long totalConnectionsReceived;
    /**
     * 服务器处理的命令总数
     */
    private long totalCommandsProcessed;
    /**
     *  每秒处理的命令数
     */
    private int instantaneousOpsPerSec;
    /**
     *  网络总入量
     */
    private long totalNetInputBytes;
    /**
     *  网络总出量
     */
    private long totalNetOutputBytes;
    /**
     *  每秒输入量，单位是kb/s
     */
    private double instantaneousInputKbps;
    /**
     *  每秒输出量，单位是kb/s
     */
    private double instantaneousOutputKbps;
    /**
     *  由于maxclients限制而拒绝的连接数
     */
    private int rejectedConnections;
    /**
     *  key到期事件的总数
     */
    private long expiredKeys;
    /**
     *  由于maxmemory限制而导致被驱逐的key的数量
     */
    private long evictedKeys;
    /**
     *  在主字典中成功查找到key的次数
     */
    private long keyspaceHits;
    /**
     * 在主字典中查找key失败的次数
     */
    private long keyspaceMisses;
    /**
     *  拥有客户端订阅的全局pub/sub通道数
     */
    private int pubsubChannels;
    /**
     *  拥有客户端订阅的全局pub/sub模式数
     */
    private int pubsubPatterns;
    /**
     * 最新fork操作的持续时间，以微秒为单位
     */
    private int latestForkUsec;
    /**
     * 处理的读取事件总数
     */
    private long totalReadsProcessed;
    /**
     * 处理的写入事件总数
     */
    private long totalWritesProcessed;
    /**
     * 主线程和I / O线程处理的读取事件数
     */
    private long ioThreadedReadsProcessed;
    /**
     * 主线程和I / O线程处理的写事件数
     */
    private long ioThreadedWritesProcessed;

    public long getTotalConnectionsReceived() {
        return totalConnectionsReceived;
    }

    public void setTotalConnectionsReceived(long totalConnectionsReceived) {
        this.totalConnectionsReceived = totalConnectionsReceived;
    }

    public long getTotalCommandsProcessed() {
        return totalCommandsProcessed;
    }

    public void setTotalCommandsProcessed(long totalCommandsProcessed) {
        this.totalCommandsProcessed = totalCommandsProcessed;
    }

    public int getInstantaneousOpsPerSec() {
        return instantaneousOpsPerSec;
    }

    public void setInstantaneousOpsPerSec(int instantaneousOpsPerSec) {
        this.instantaneousOpsPerSec = instantaneousOpsPerSec;
    }

    public long getTotalNetInputBytes() {
        return totalNetInputBytes;
    }

    public void setTotalNetInputBytes(long totalNetInputBytes) {
        this.totalNetInputBytes = totalNetInputBytes;
    }

    public long getTotalNetOutputBytes() {
        return totalNetOutputBytes;
    }

    public void setTotalNetOutputBytes(long totalNetOutputBytes) {
        this.totalNetOutputBytes = totalNetOutputBytes;
    }

    public double getInstantaneousInputKbps() {
        return instantaneousInputKbps;
    }

    public void setInstantaneousInputKbps(double instantaneousInputKbps) {
        this.instantaneousInputKbps = instantaneousInputKbps;
    }

    public double getInstantaneousOutputKbps() {
        return instantaneousOutputKbps;
    }

    public void setInstantaneousOutputKbps(double instantaneousOutputKbps) {
        this.instantaneousOutputKbps = instantaneousOutputKbps;
    }

    public int getRejectedConnections() {
        return rejectedConnections;
    }

    public void setRejectedConnections(int rejectedConnections) {
        this.rejectedConnections = rejectedConnections;
    }

    public long getExpiredKeys() {
        return expiredKeys;
    }

    public void setExpiredKeys(long expiredKeys) {
        this.expiredKeys = expiredKeys;
    }

    public long getEvictedKeys() {
        return evictedKeys;
    }

    public void setEvictedKeys(long evictedKeys) {
        this.evictedKeys = evictedKeys;
    }

    public long getKeyspaceHits() {
        return keyspaceHits;
    }

    public void setKeyspaceHits(long keyspaceHits) {
        this.keyspaceHits = keyspaceHits;
    }

    public long getKeyspaceMisses() {
        return keyspaceMisses;
    }

    public void setKeyspaceMisses(long keyspaceMisses) {
        this.keyspaceMisses = keyspaceMisses;
    }

    public int getPubsubChannels() {
        return pubsubChannels;
    }

    public void setPubsubChannels(int pubsubChannels) {
        this.pubsubChannels = pubsubChannels;
    }

    public int getPubsubPatterns() {
        return pubsubPatterns;
    }

    public void setPubsubPatterns(int pubsubPatterns) {
        this.pubsubPatterns = pubsubPatterns;
    }

    public int getLatestForkUsec() {
        return latestForkUsec;
    }

    public void setLatestForkUsec(int latestForkUsec) {
        this.latestForkUsec = latestForkUsec;
    }

    public long getTotalReadsProcessed() {
        return totalReadsProcessed;
    }

    public void setTotalReadsProcessed(long totalReadsProcessed) {
        this.totalReadsProcessed = totalReadsProcessed;
    }

    public long getTotalWritesProcessed() {
        return totalWritesProcessed;
    }

    public void setTotalWritesProcessed(long totalWritesProcessed) {
        this.totalWritesProcessed = totalWritesProcessed;
    }

    public long getIoThreadedReadsProcessed() {
        return ioThreadedReadsProcessed;
    }

    public void setIoThreadedReadsProcessed(long ioThreadedReadsProcessed) {
        this.ioThreadedReadsProcessed = ioThreadedReadsProcessed;
    }

    public long getIoThreadedWritesProcessed() {
        return ioThreadedWritesProcessed;
    }

    public void setIoThreadedWritesProcessed(long ioThreadedWritesProcessed) {
        this.ioThreadedWritesProcessed = ioThreadedWritesProcessed;
    }

    @Override
    public String toString() {
        return "RedisStats{" +
                "totalConnectionsReceived=" + totalConnectionsReceived +
                ", totalCommandsProcessed=" + totalCommandsProcessed +
                ", instantaneousOpsPerSec=" + instantaneousOpsPerSec +
                ", totalNetInputBytes=" + totalNetInputBytes +
                ", totalNetOutputBytes=" + totalNetOutputBytes +
                ", instantaneousInputKbps=" + instantaneousInputKbps +
                ", instantaneousOutputKbps=" + instantaneousOutputKbps +
                ", rejectedConnections=" + rejectedConnections +
                ", expiredKeys=" + expiredKeys +
                ", evictedKeys=" + evictedKeys +
                ", keyspaceHits=" + keyspaceHits +
                ", keyspaceMisses=" + keyspaceMisses +
                ", pubsubChannels=" + pubsubChannels +
                ", pubsubPatterns=" + pubsubPatterns +
                ", latestForkUsec=" + latestForkUsec +
                ", totalReadsProcessed=" + totalReadsProcessed +
                ", totalWritesProcessed=" + totalWritesProcessed +
                ", ioThreadedReadsProcessed=" + ioThreadedReadsProcessed +
                ", ioThreadedWritesProcessed=" + ioThreadedWritesProcessed +
                '}';
    }
}
