package com.raiden.redis.ui.common;

import java.io.File;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 10:55 2022/5/15
 * @Modified By:
 */
public final class Path {
    private static final String ROOT_PATH = File.separator + "data" + File.separator;
    public static final String REDIS_CLUSTER_HISTORICAL_RECORD_DATA_PATH = ROOT_PATH + "redisClusterHistoricalRecord.data";
    public static final String REDIS_SINGLE_HISTORICAL_RECORD_DATA_PATH = ROOT_PATH + "redisSingleHistoricalRecord.data";
    public static final String REDIS_SENTINEL_HISTORICAL_RECORD_DATA_PATH = ROOT_PATH + "redisSentinelHistoricalRecord.data";
}
