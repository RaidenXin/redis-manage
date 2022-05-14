package com.raiden.redis.model;

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
    private String usedMemory;
    /**
     * 以人类可读的格式返回 Redis 分配的内存总量
     */
    private String usedMemoryHuman;
    /**
     * 以人类可读的格式返回 Redis 的内存消耗峰值
     */
    private String usedMemoryPeakHuman;
    /**
     * 由 Redis 分配器分配的内存总量，以字节（byte）为单位
     */
    private String usedMemoryPeakPerc;
    /**
     * 服务器为管理其内部数据结构而分配的所有开销的总和（以字节为单位）
     */
    private String usedMemoryOverhead;
    /**
     * 由 Redis 分配器分配的内存总量，以字节（byte）为单位
     */
    private String usedMemoryStartup;
    /**
     * 以字节为单位的数据集大小（used_memory减去used_memory_overhead）
     */
    private String usedMemoryDataset;
    /**
     * used_memory_dataset占净内存使用量的百分比（used_memory减去used_memory_startup）
     */
    private String usedMemoryDatasetPerc;
    /**
     * 以人类可读的格式返回 Redis主机具有的内存总量
     */
    private String totalSystemMemoryHuman;
    /**
     * maxmemory配置指令的值
     */
    private String maxmemory;
    /**
     * 以人类可读的格式返回 maxmemory配置指令的值
     */
    private String maxmemoryHuman;
    /**
     * used_memory_rss 和 used_memory 之间的比率
     */
    private String memFragmentationRatio;
    /**
     * 指示活动碎片整理是否处于活动状态的标志
     */
    private String activeDefragRunning;
    /**
     * 等待释放的对象数（由于使用ASYNC选项调用UNLINK或FLUSHDB和FLUSHALL）
     */
    private String lazyfreePendingObjects;

    public String getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(String usedMemory) {
        this.usedMemory = usedMemory;
    }

    public String getUsedMemoryHuman() {
        return usedMemoryHuman;
    }

    public void setUsedMemoryHuman(String usedMemoryHuman) {
        this.usedMemoryHuman = usedMemoryHuman;
    }

    public String getUsedMemoryPeakHuman() {
        return usedMemoryPeakHuman;
    }

    public void setUsedMemoryPeakHuman(String usedMemoryPeakHuman) {
        this.usedMemoryPeakHuman = usedMemoryPeakHuman;
    }

    public String getUsedMemoryPeakPerc() {
        return usedMemoryPeakPerc;
    }

    public void setUsedMemoryPeakPerc(String usedMemoryPeakPerc) {
        this.usedMemoryPeakPerc = usedMemoryPeakPerc;
    }

    public String getUsedMemoryOverhead() {
        return usedMemoryOverhead;
    }

    public void setUsedMemoryOverhead(String usedMemoryOverhead) {
        this.usedMemoryOverhead = usedMemoryOverhead;
    }

    public String getUsedMemoryStartup() {
        return usedMemoryStartup;
    }

    public void setUsedMemoryStartup(String usedMemoryStartup) {
        this.usedMemoryStartup = usedMemoryStartup;
    }

    public String getUsedMemoryDataset() {
        return usedMemoryDataset;
    }

    public void setUsedMemoryDataset(String usedMemoryDataset) {
        this.usedMemoryDataset = usedMemoryDataset;
    }

    public String getUsedMemoryDatasetPerc() {
        return usedMemoryDatasetPerc;
    }

    public void setUsedMemoryDatasetPerc(String usedMemoryDatasetPerc) {
        this.usedMemoryDatasetPerc = usedMemoryDatasetPerc;
    }

    public String getTotalSystemMemoryHuman() {
        return totalSystemMemoryHuman;
    }

    public void setTotalSystemMemoryHuman(String totalSystemMemoryHuman) {
        this.totalSystemMemoryHuman = totalSystemMemoryHuman;
    }

    public String getMaxmemory() {
        return maxmemory;
    }

    public void setMaxmemory(String maxmemory) {
        this.maxmemory = maxmemory;
    }

    public String getMaxmemoryHuman() {
        return maxmemoryHuman;
    }

    public void setMaxmemoryHuman(String maxmemoryHuman) {
        this.maxmemoryHuman = maxmemoryHuman;
    }

    public String getMemFragmentationRatio() {
        return memFragmentationRatio;
    }

    public void setMemFragmentationRatio(String memFragmentationRatio) {
        this.memFragmentationRatio = memFragmentationRatio;
    }

    public String getActiveDefragRunning() {
        return activeDefragRunning;
    }

    public void setActiveDefragRunning(String activeDefragRunning) {
        this.activeDefragRunning = activeDefragRunning;
    }

    public String getLazyfreePendingObjects() {
        return lazyfreePendingObjects;
    }

    public void setLazyfreePendingObjects(String lazyfreePendingObjects) {
        this.lazyfreePendingObjects = lazyfreePendingObjects;
    }
}
