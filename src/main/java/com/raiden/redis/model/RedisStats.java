package com.raiden.redis.model;

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
}
