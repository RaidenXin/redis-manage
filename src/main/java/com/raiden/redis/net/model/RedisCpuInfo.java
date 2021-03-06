package com.raiden.redis.net.model;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 20:02 2022/5/15
 * @Modified By:
 */
public class RedisCpuInfo {
    /**
     * 由Redis服务器消耗的系统CPU
     */
    private double usedCpuSys;
    /**
     * 由Redis服务器消耗的用户CPU
     */
    private double usedCpuUser;
    /**
     * 由后台进程消耗的系统CPU
     */
    private double usedCpuSysChildren;
    /**
     * 由后台进程消耗的用户CPU
     */
    private double usedCpuUserChildren;
    /**
     * Redis主线程消耗的系统CPU
     */
    private double usedCpuSysMainThread;
    /**
     * Redis主线程消耗的用户CPU
     */
    private double usedCpuUserMainThread;

    public double getUsedCpuSys() {
        return usedCpuSys;
    }

    public void setUsedCpuSys(double usedCpuSys) {
        this.usedCpuSys = usedCpuSys;
    }

    public double getUsedCpuUser() {
        return usedCpuUser;
    }

    public void setUsedCpuUser(double usedCpuUser) {
        this.usedCpuUser = usedCpuUser;
    }

    public double getUsedCpuSysChildren() {
        return usedCpuSysChildren;
    }

    public void setUsedCpuSysChildren(double usedCpuSysChildren) {
        this.usedCpuSysChildren = usedCpuSysChildren;
    }

    public double getUsedCpuUserChildren() {
        return usedCpuUserChildren;
    }

    public void setUsedCpuUserChildren(double usedCpuUserChildren) {
        this.usedCpuUserChildren = usedCpuUserChildren;
    }

    public double getUsedCpuSysMainThread() {
        return usedCpuSysMainThread;
    }

    public void setUsedCpuSysMainThread(double usedCpuSysMainThread) {
        this.usedCpuSysMainThread = usedCpuSysMainThread;
    }

    public double getUsedCpuUserMainThread() {
        return usedCpuUserMainThread;
    }

    public void setUsedCpuUserMainThread(double usedCpuUserMainThread) {
        this.usedCpuUserMainThread = usedCpuUserMainThread;
    }

    @Override
    public String toString() {
        return "RedisCpuInfo{" +
                "usedCpuSys=" + usedCpuSys +
                ", usedCpuUser=" + usedCpuUser +
                ", usedCpuSysChildren=" + usedCpuSysChildren +
                ", usedCpuUserChildren=" + usedCpuUserChildren +
                ", usedCpuSysMainThread=" + usedCpuSysMainThread +
                ", usedCpuUserMainThread=" + usedCpuUserMainThread +
                '}';
    }
}
