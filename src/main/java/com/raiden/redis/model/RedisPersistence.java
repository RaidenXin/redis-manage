package com.raiden.redis.model;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 12:40 2022/5/15
 * @Modified By:
 */
public class RedisPersistence {
    /**
     * 指示转储文件（dump）的加载是否正在进行的标志
     */
    private int loading;
    /**
     * 自上次转储以来的更改次数
     */
    private long rdbChangesSinceLastSave;
    /**
     * 指示RDB文件是否正在保存的标志
     */
    private int rdb_bgsave_in_progress;
    /**
     * 上次成功保存RDB的基于纪年的时间戳
     */
    private long rdbLastSaveTime;
    /**
     * 上次RDB保存操作的状态
     */
    private String rdbLastBgsaveStatus;
    /**
     * 上次RDB保存操作的持续时间（以秒为单位）
     */
    private int rdbLastBgsaveTimeSec;
    /**
     * 正在进行的RDB保存操作的持续时间（如果有） 没有就是 -1
     */
    private int rdbCurrentBgsaveTimeSec;

    /**
     *  表示AOF记录已激活的标志
     */
    private int aofEnabled;
    /**
     *  表示AOF记录已激活的标志
     */
    private int aofRewriteInProgress;
    /**
     *  表示一旦进行中的RDB保存操作完成，就会安排进行AOF重写操作的标志
     */
    private int aofRewriteScheduled;
    /**
     * 上次AOF重写操作的持续时间，以秒为单位
     */
    private int aofLastRewriteTimeSec;
    /**
     * 正在进行的AOF重写操作的持续时间（如果有）
     */
    private int aofCurrentRewriteTimeSec;
    /**
     *  上次AOF重写操作的持续时间，以秒为单位
     */
    private String aofLastBgrewriteStatus;
    /**
     * 上一次AOF写入操作的状态
     */
    private String aofLastWriteStatus;
    /**
     * 上次AOF重写操作期间copy-on-write分配的字节大小
     */
    private long aofLastCowSize;
    /**
     * 存储是会fork出子进程, 这表示fork出的子进程正在进行中的意思
     */
    private int moduleForkInProgress;
    /**
     * 子进程 写拷贝 所消耗的内存
     */
    private long moduleForkLastCowSize;
    /**
     * 当前的AOF文件大小
     */
    private long aofCurrentSize;
    /**
     * 当前的AOF文件大小
     */
    private long aofBaseSize;
    /**
     * 指示AOF重写操作是否会在当前RDB保存操作完成后立即执行的标志。
     */
    private int aofPendingRewrite;
    /**
     * AOF缓冲区大小
     */
    private long aofBufferLength;
    /**
     * AOF重写缓冲区大小
     */
    private long aofRewriteBufferLength;
    /**
     * 在后台IO队列中等待fsync处理的任务数
     */
    private int aofPendingBioFsync;
    /**
     * 延迟fsync计数器
     */
    private long aofDelayedFsync;

    /**
     * 加载操作的开始时间（基于纪元的时间戳）
     */
    private long loadingStartTime;
    /**
     * 文件总大小
     */
    private long loadingTotalBytes;
    /**
     * 已经加载的字节数
     */
    private long loadingstarttime;
    /**
     * 已经加载的百分比
     */
    private String loadingLoadedPerc;
    /**
     * 预计加载完成所需的剩余秒数
     */
    private int loadingEtaSeconds;

    public int getLoading() {
        return loading;
    }

    public void setLoading(int loading) {
        this.loading = loading;
    }

    public long getRdbChangesSinceLastSave() {
        return rdbChangesSinceLastSave;
    }

    public void setRdbChangesSinceLastSave(long rdbChangesSinceLastSave) {
        this.rdbChangesSinceLastSave = rdbChangesSinceLastSave;
    }

    public int getRdb_bgsave_in_progress() {
        return rdb_bgsave_in_progress;
    }

    public void setRdb_bgsave_in_progress(int rdb_bgsave_in_progress) {
        this.rdb_bgsave_in_progress = rdb_bgsave_in_progress;
    }

    public long getRdbLastSaveTime() {
        return rdbLastSaveTime;
    }

    public void setRdbLastSaveTime(long rdbLastSaveTime) {
        this.rdbLastSaveTime = rdbLastSaveTime;
    }

    public String getRdbLastBgsaveStatus() {
        return rdbLastBgsaveStatus;
    }

    public void setRdbLastBgsaveStatus(String rdbLastBgsaveStatus) {
        this.rdbLastBgsaveStatus = rdbLastBgsaveStatus;
    }

    public int getRdbLastBgsaveTimeSec() {
        return rdbLastBgsaveTimeSec;
    }

    public void setRdbLastBgsaveTimeSec(int rdbLastBgsaveTimeSec) {
        this.rdbLastBgsaveTimeSec = rdbLastBgsaveTimeSec;
    }

    public int getRdbCurrentBgsaveTimeSec() {
        return rdbCurrentBgsaveTimeSec;
    }

    public void setRdbCurrentBgsaveTimeSec(int rdbCurrentBgsaveTimeSec) {
        this.rdbCurrentBgsaveTimeSec = rdbCurrentBgsaveTimeSec;
    }

    public int getAofEnabled() {
        return aofEnabled;
    }

    public void setAofEnabled(int aofEnabled) {
        this.aofEnabled = aofEnabled;
    }

    public int getAofRewriteInProgress() {
        return aofRewriteInProgress;
    }

    public void setAofRewriteInProgress(int aofRewriteInProgress) {
        this.aofRewriteInProgress = aofRewriteInProgress;
    }

    public int getAofRewriteScheduled() {
        return aofRewriteScheduled;
    }

    public void setAofRewriteScheduled(int aofRewriteScheduled) {
        this.aofRewriteScheduled = aofRewriteScheduled;
    }

    public int getAofLastRewriteTimeSec() {
        return aofLastRewriteTimeSec;
    }

    public void setAofLastRewriteTimeSec(int aofLastRewriteTimeSec) {
        this.aofLastRewriteTimeSec = aofLastRewriteTimeSec;
    }

    public int getAofCurrentRewriteTimeSec() {
        return aofCurrentRewriteTimeSec;
    }

    public void setAofCurrentRewriteTimeSec(int aofCurrentRewriteTimeSec) {
        this.aofCurrentRewriteTimeSec = aofCurrentRewriteTimeSec;
    }

    public String getAofLastBgrewriteStatus() {
        return aofLastBgrewriteStatus;
    }

    public void setAofLastBgrewriteStatus(String aofLastBgrewriteStatus) {
        this.aofLastBgrewriteStatus = aofLastBgrewriteStatus;
    }

    public String getAofLastWriteStatus() {
        return aofLastWriteStatus;
    }

    public void setAofLastWriteStatus(String aofLastWriteStatus) {
        this.aofLastWriteStatus = aofLastWriteStatus;
    }

    public long getAofLastCowSize() {
        return aofLastCowSize;
    }

    public void setAofLastCowSize(long aofLastCowSize) {
        this.aofLastCowSize = aofLastCowSize;
    }

    public int getModuleForkInProgress() {
        return moduleForkInProgress;
    }

    public void setModuleForkInProgress(int moduleForkInProgress) {
        this.moduleForkInProgress = moduleForkInProgress;
    }

    public long getModuleForkLastCowSize() {
        return moduleForkLastCowSize;
    }

    public void setModuleForkLastCowSize(long moduleForkLastCowSize) {
        this.moduleForkLastCowSize = moduleForkLastCowSize;
    }

    public long getAofCurrentSize() {
        return aofCurrentSize;
    }

    public void setAofCurrentSize(long aofCurrentSize) {
        this.aofCurrentSize = aofCurrentSize;
    }

    public long getAofBaseSize() {
        return aofBaseSize;
    }

    public void setAofBaseSize(long aofBaseSize) {
        this.aofBaseSize = aofBaseSize;
    }

    public int getAofPendingRewrite() {
        return aofPendingRewrite;
    }

    public void setAofPendingRewrite(int aofPendingRewrite) {
        this.aofPendingRewrite = aofPendingRewrite;
    }

    public long getAofBufferLength() {
        return aofBufferLength;
    }

    public void setAofBufferLength(long aofBufferLength) {
        this.aofBufferLength = aofBufferLength;
    }

    public long getAofRewriteBufferLength() {
        return aofRewriteBufferLength;
    }

    public void setAofRewriteBufferLength(long aofRewriteBufferLength) {
        this.aofRewriteBufferLength = aofRewriteBufferLength;
    }

    public int getAofPendingBioFsync() {
        return aofPendingBioFsync;
    }

    public void setAofPendingBioFsync(int aofPendingBioFsync) {
        this.aofPendingBioFsync = aofPendingBioFsync;
    }

    public long getAofDelayedFsync() {
        return aofDelayedFsync;
    }

    public void setAofDelayedFsync(long aofDelayedFsync) {
        this.aofDelayedFsync = aofDelayedFsync;
    }

    public long getLoadingStartTime() {
        return loadingStartTime;
    }

    public void setLoadingStartTime(long loadingStartTime) {
        this.loadingStartTime = loadingStartTime;
    }

    public long getLoadingTotalBytes() {
        return loadingTotalBytes;
    }

    public void setLoadingTotalBytes(long loadingTotalBytes) {
        this.loadingTotalBytes = loadingTotalBytes;
    }

    public long getLoadingstarttime() {
        return loadingstarttime;
    }

    public void setLoadingstarttime(long loadingstarttime) {
        this.loadingstarttime = loadingstarttime;
    }

    public String getLoadingLoadedPerc() {
        return loadingLoadedPerc;
    }

    public void setLoadingLoadedPerc(String loadingLoadedPerc) {
        this.loadingLoadedPerc = loadingLoadedPerc;
    }

    public int getLoadingEtaSeconds() {
        return loadingEtaSeconds;
    }

    public void setLoadingEtaSeconds(int loadingEtaSeconds) {
        this.loadingEtaSeconds = loadingEtaSeconds;
    }
}
