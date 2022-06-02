package com.raiden.redis.net.model;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 15:09 2022/5/14
 * @Modified By:
 * 在理想情况下， used_memory_rss 的值应该只比 used_memory 稍微高一点儿。
 *
 * 当 rss > used ，且两者的值相差较大时，表示存在（内部或外部的）内存碎片。
 *
 * 内存碎片的比率可以通过 mem_fragmentation_ratio 的值看出。
 *
 * 当 used > rss 时，表示 Redis 的部分内存被操作系统换出到交换空间了，在这种情况下，操作可能会产生明显的延迟。
 *
 * 由于Redis无法控制其分配的内存如何映射到内存页，因此常住内存（used_memory_rss）很高通常是内存使用量激增的结果。
 *
 * 当 Redis 释放内存时，内存将返回给分配器，分配器可能会，也可能不会，将内存返还给操作系统。
 *
 * 如果 Redis 释放了内存，却没有将内存返还给操作系统，那么 used_memory 的值可能和操作系统显示的 Redis 内存占用并不一致。
 *
 * 查看 used_memory_peak 的值可以验证这种情况是否发生。
 */
public class RedisMemoryInfo {
    /**
     * 由 Redis 分配器分配的内存总量，以字节（byte）为单位
     */
    private long usedMemory;
    /**
     * 以人类可读的格式返回 Redis 分配的内存总量
     */
    private String usedMemoryHuman;
    /**
     * 从操作系统的角度，返回 Redis 已分配的内存总量（俗称常驻集大小）。
     */
    private long usedMemoryRss;
    /**
     * 向操作系统申请的内存大小(mb)
     */
    private String usedMemoryRssHuman;
    /**
     * Redis 的内存消耗峰值
     */
    private long usedMemoryPeak;
    /**
     * 以人类可读的格式返回 Redis 的内存消耗峰值
     */
    private String usedMemoryPeakHuman;
    /**
     * Lua 引擎所使用的内存大小（以字节为单位）
     */
    private long usedMemoryLua;
    /**
     * 以人类可读的格式返回 Lua 引擎所使用的内存大小
     */
    private String usedMemoryLuaHuman;
    /**
     * 由 Redis 分配器分配的内存总量，以字节（byte）为单位
     */
    private String usedMemoryPeakPerc;
    /**
     * 服务器为管理其内部数据结构而分配的所有开销的总和（以字节为单位）
     */
    private long usedMemoryOverhead;
    /**
     * 由 Redis 分配器分配的内存总量，以字节（byte）为单位
     */
    private long usedMemoryStartup;
    /**
     * 以字节为单位的数据集大小（used_memory减去used_memory_overhead）
     */
    private long usedMemoryDataset;
    /**
     * used_memory_dataset占净内存使用量的百分比（used_memory减去used_memory_startup）
     */
    private String usedMemoryDatasetPerc;
    /**
     * 分配器分配的内存
     */
    private long allocatorAllocated;
    /**
     * 分配器活跃的内存
     */
    private long allocatorActive;
    /**
     * 分配器常驻的内存
     */
    private long allocatorResident;
    /**
     * 主机内存总量(byte)
     */
    private long totalSystemMemory;
    /**
     * 主机内存总量(mb)
     */
    private String totalSystemMemoryHuman;
    /**
     * Lua引擎存储占用的内存(byte)
     */
    private long usedMemoryScripts;
    /**
     * Lua引擎存储占用的内存(mb)
     */
    private String usedMemoryScriptsHuman;
    /**
     * 缓存的Lua脚本数
     */
    private long numberOfCachedScripts;
    /**
     * maxmemory配置指令的值
     */
    private long maxmemory;
    /**
     * 以人类可读的格式返回 maxmemory配置指令的值
     */
    private String maxmemoryHuman;
    /**
     * 内存淘汰策略
     */
    private String maxmemoryPolicy;
    /**
     *分配器的碎片率
     */
    private double allocatorFragRatio;
    /**
     *分配器的碎片率
     */
    private long allocatorFragBytes;
    /**
     *分配器常驻内存比例
     */
    private double allocatorRssRatio;
    /**
     *分配器的常驻内存大小
     */
    private long allocatorRssBytes;
    /**
     *常驻内存开销比例
     */
    private double rssOverheadRatio;
    /**
     *常驻内存开销大小
     */
    private long rssOverheadBytes;
    /**
     * 内存碎片率 used_memory_rss 和 used_memory 之间的比率
     */
    private double memFragmentationRatio;
    /**
     *内存碎片大小
     */
    private long memFragmentationBytes;
    /**
     *被驱逐的内存
     */
    private long memNotCountedForEvict;
    /**
     *Redis复制积压缓冲区内存
     */
    private long memReplicationBacklog;
    /**
     *Redis节点客户端消耗内存
     */
    private long memClientsSlaves;
    /**
     *Redis所有常规客户端消耗内存
     */
    private long memClientsNormal;
    /**
     *AOF使用内存
     */
    private long memAofBuffer;
    /**
     * 指示活动碎片整理是否处于活动状态的标志
     */
    private int activeDefragRunning;
    /**
     * 等待释放的对象数（由于使用ASYNC选项调用UNLINK或FLUSHDB和FLUSHALL）
     */
    private int lazyfreePendingObjects;

    public long getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(long usedMemory) {
        this.usedMemory = usedMemory;
    }

    public String getUsedMemoryHuman() {
        return usedMemoryHuman;
    }

    public void setUsedMemoryHuman(String usedMemoryHuman) {
        this.usedMemoryHuman = usedMemoryHuman;
    }

    public long getUsedMemoryRss() {
        return usedMemoryRss;
    }

    public void setUsedMemoryRss(long usedMemoryRss) {
        this.usedMemoryRss = usedMemoryRss;
    }

    public String getUsedMemoryRssHuman() {
        return usedMemoryRssHuman;
    }

    public void setUsedMemoryRssHuman(String usedMemoryRssHuman) {
        this.usedMemoryRssHuman = usedMemoryRssHuman;
    }

    public long getUsedMemoryPeak() {
        return usedMemoryPeak;
    }

    public void setUsedMemoryPeak(long usedMemoryPeak) {
        this.usedMemoryPeak = usedMemoryPeak;
    }

    public String getUsedMemoryPeakHuman() {
        return usedMemoryPeakHuman;
    }

    public void setUsedMemoryPeakHuman(String usedMemoryPeakHuman) {
        this.usedMemoryPeakHuman = usedMemoryPeakHuman;
    }

    public long getUsedMemoryLua() {
        return usedMemoryLua;
    }

    public void setUsedMemoryLua(long usedMemoryLua) {
        this.usedMemoryLua = usedMemoryLua;
    }

    public String getUsedMemoryLuaHuman() {
        return usedMemoryLuaHuman;
    }

    public void setUsedMemoryLuaHuman(String usedMemoryLuaHuman) {
        this.usedMemoryLuaHuman = usedMemoryLuaHuman;
    }

    public String getUsedMemoryPeakPerc() {
        return usedMemoryPeakPerc;
    }

    public void setUsedMemoryPeakPerc(String usedMemoryPeakPerc) {
        this.usedMemoryPeakPerc = usedMemoryPeakPerc;
    }

    public long getUsedMemoryOverhead() {
        return usedMemoryOverhead;
    }

    public void setUsedMemoryOverhead(long usedMemoryOverhead) {
        this.usedMemoryOverhead = usedMemoryOverhead;
    }

    public long getUsedMemoryStartup() {
        return usedMemoryStartup;
    }

    public void setUsedMemoryStartup(long usedMemoryStartup) {
        this.usedMemoryStartup = usedMemoryStartup;
    }

    public long getUsedMemoryDataset() {
        return usedMemoryDataset;
    }

    public void setUsedMemoryDataset(long usedMemoryDataset) {
        this.usedMemoryDataset = usedMemoryDataset;
    }

    public String getUsedMemoryDatasetPerc() {
        return usedMemoryDatasetPerc;
    }

    public void setUsedMemoryDatasetPerc(String usedMemoryDatasetPerc) {
        this.usedMemoryDatasetPerc = usedMemoryDatasetPerc;
    }

    public long getAllocatorAllocated() {
        return allocatorAllocated;
    }

    public void setAllocatorAllocated(long allocatorAllocated) {
        this.allocatorAllocated = allocatorAllocated;
    }

    public long getAllocatorActive() {
        return allocatorActive;
    }

    public void setAllocatorActive(long allocatorActive) {
        this.allocatorActive = allocatorActive;
    }

    public long getAllocatorResident() {
        return allocatorResident;
    }

    public void setAllocatorResident(long allocatorResident) {
        this.allocatorResident = allocatorResident;
    }

    public long getTotalSystemMemory() {
        return totalSystemMemory;
    }

    public void setTotalSystemMemory(long totalSystemMemory) {
        this.totalSystemMemory = totalSystemMemory;
    }

    public String getTotalSystemMemoryHuman() {
        return totalSystemMemoryHuman;
    }

    public void setTotalSystemMemoryHuman(String totalSystemMemoryHuman) {
        this.totalSystemMemoryHuman = totalSystemMemoryHuman;
    }

    public long getUsedMemoryScripts() {
        return usedMemoryScripts;
    }

    public void setUsedMemoryScripts(long usedMemoryScripts) {
        this.usedMemoryScripts = usedMemoryScripts;
    }

    public String getUsedMemoryScriptsHuman() {
        return usedMemoryScriptsHuman;
    }

    public void setUsedMemoryScriptsHuman(String usedMemoryScriptsHuman) {
        this.usedMemoryScriptsHuman = usedMemoryScriptsHuman;
    }

    public long getNumberOfCachedScripts() {
        return numberOfCachedScripts;
    }

    public void setNumberOfCachedScripts(long numberOfCachedScripts) {
        this.numberOfCachedScripts = numberOfCachedScripts;
    }

    public long getMaxmemory() {
        return maxmemory;
    }

    public void setMaxmemory(long maxmemory) {
        this.maxmemory = maxmemory;
    }

    public String getMaxmemoryHuman() {
        return maxmemoryHuman;
    }

    public void setMaxmemoryHuman(String maxmemoryHuman) {
        this.maxmemoryHuman = maxmemoryHuman;
    }

    public String getMaxmemoryPolicy() {
        return maxmemoryPolicy;
    }

    public void setMaxmemoryPolicy(String maxmemoryPolicy) {
        this.maxmemoryPolicy = maxmemoryPolicy;
    }

    public double getAllocatorFragRatio() {
        return allocatorFragRatio;
    }

    public void setAllocatorFragRatio(double allocatorFragRatio) {
        this.allocatorFragRatio = allocatorFragRatio;
    }

    public long getAllocatorFragBytes() {
        return allocatorFragBytes;
    }

    public void setAllocatorFragBytes(long allocatorFragBytes) {
        this.allocatorFragBytes = allocatorFragBytes;
    }

    public double getAllocatorRssRatio() {
        return allocatorRssRatio;
    }

    public void setAllocatorRssRatio(double allocatorRssRatio) {
        this.allocatorRssRatio = allocatorRssRatio;
    }

    public long getAllocatorRssBytes() {
        return allocatorRssBytes;
    }

    public void setAllocatorRssBytes(long allocatorRssBytes) {
        this.allocatorRssBytes = allocatorRssBytes;
    }

    public double getRssOverheadRatio() {
        return rssOverheadRatio;
    }

    public void setRssOverheadRatio(double rssOverheadRatio) {
        this.rssOverheadRatio = rssOverheadRatio;
    }

    public long getRssOverheadBytes() {
        return rssOverheadBytes;
    }

    public void setRssOverheadBytes(long rssOverheadBytes) {
        this.rssOverheadBytes = rssOverheadBytes;
    }

    public double getMemFragmentationRatio() {
        return memFragmentationRatio;
    }

    public void setMemFragmentationRatio(double memFragmentationRatio) {
        this.memFragmentationRatio = memFragmentationRatio;
    }

    public long getMemFragmentationBytes() {
        return memFragmentationBytes;
    }

    public void setMemFragmentationBytes(long memFragmentationBytes) {
        this.memFragmentationBytes = memFragmentationBytes;
    }

    public long getMemNotCountedForEvict() {
        return memNotCountedForEvict;
    }

    public void setMemNotCountedForEvict(long memNotCountedForEvict) {
        this.memNotCountedForEvict = memNotCountedForEvict;
    }

    public long getMemReplicationBacklog() {
        return memReplicationBacklog;
    }

    public void setMemReplicationBacklog(long memReplicationBacklog) {
        this.memReplicationBacklog = memReplicationBacklog;
    }

    public long getMemClientsSlaves() {
        return memClientsSlaves;
    }

    public void setMemClientsSlaves(long memClientsSlaves) {
        this.memClientsSlaves = memClientsSlaves;
    }

    public long getMemClientsNormal() {
        return memClientsNormal;
    }

    public void setMemClientsNormal(long memClientsNormal) {
        this.memClientsNormal = memClientsNormal;
    }

    public long getMemAofBuffer() {
        return memAofBuffer;
    }

    public void setMemAofBuffer(long memAofBuffer) {
        this.memAofBuffer = memAofBuffer;
    }

    public int getActiveDefragRunning() {
        return activeDefragRunning;
    }

    public void setActiveDefragRunning(int activeDefragRunning) {
        this.activeDefragRunning = activeDefragRunning;
    }

    public int getLazyfreePendingObjects() {
        return lazyfreePendingObjects;
    }

    public void setLazyfreePendingObjects(int lazyfreePendingObjects) {
        this.lazyfreePendingObjects = lazyfreePendingObjects;
    }

    @Override
    public String toString() {
        return "RedisMemoryInfo{" +
                "usedMemory=" + usedMemory +
                ", usedMemoryHuman='" + usedMemoryHuman + '\'' +
                ", usedMemoryRss=" + usedMemoryRss +
                ", usedMemoryRssHuman='" + usedMemoryRssHuman + '\'' +
                ", usedMemoryPeak=" + usedMemoryPeak +
                ", usedMemoryPeakHuman='" + usedMemoryPeakHuman + '\'' +
                ", usedMemoryLua=" + usedMemoryLua +
                ", usedMemoryLuaHuman='" + usedMemoryLuaHuman + '\'' +
                ", usedMemoryPeakPerc='" + usedMemoryPeakPerc + '\'' +
                ", usedMemoryOverhead=" + usedMemoryOverhead +
                ", usedMemoryStartup=" + usedMemoryStartup +
                ", usedMemoryDataset=" + usedMemoryDataset +
                ", usedMemoryDatasetPerc='" + usedMemoryDatasetPerc + '\'' +
                ", allocatorAllocated=" + allocatorAllocated +
                ", allocatorActive=" + allocatorActive +
                ", allocatorResident=" + allocatorResident +
                ", totalSystemMemory=" + totalSystemMemory +
                ", totalSystemMemoryHuman='" + totalSystemMemoryHuman + '\'' +
                ", usedMemoryScripts=" + usedMemoryScripts +
                ", usedMemoryScriptsHuman='" + usedMemoryScriptsHuman + '\'' +
                ", numberOfCachedScripts=" + numberOfCachedScripts +
                ", maxmemory=" + maxmemory +
                ", maxmemoryHuman='" + maxmemoryHuman + '\'' +
                ", maxmemoryPolicy='" + maxmemoryPolicy + '\'' +
                ", allocatorFragRatio=" + allocatorFragRatio +
                ", allocatorFragBytes=" + allocatorFragBytes +
                ", allocatorRssRatio=" + allocatorRssRatio +
                ", allocatorRssBytes=" + allocatorRssBytes +
                ", rssOverheadRatio=" + rssOverheadRatio +
                ", rssOverheadBytes=" + rssOverheadBytes +
                ", memFragmentationRatio=" + memFragmentationRatio +
                ", memFragmentationBytes=" + memFragmentationBytes +
                ", memNotCountedForEvict=" + memNotCountedForEvict +
                ", memReplicationBacklog=" + memReplicationBacklog +
                ", memClientsSlaves=" + memClientsSlaves +
                ", memClientsNormal=" + memClientsNormal +
                ", memAofBuffer=" + memAofBuffer +
                ", activeDefragRunning=" + activeDefragRunning +
                ", lazyfreePendingObjects=" + lazyfreePendingObjects +
                '}';
    }
}
