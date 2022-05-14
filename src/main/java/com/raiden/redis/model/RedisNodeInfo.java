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

    public static RedisNodeInfo build(String[] data) {
        return null;
    }
}
