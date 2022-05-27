package com.raiden.redis.net.client;

import com.raiden.redis.net.common.DataType;
import com.raiden.redis.net.model.RedisNodeInfo;
import com.raiden.redis.net.model.ScanResult;
import javafx.util.Pair;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 22:01 2022/5/10
 * @Modified By:
 */
public interface RedisClient {

    boolean set(String key,String value);

    boolean hSet(String key,String field,String value);

    boolean lSet(String key,String index,String value);

    int sAdd(String key, String... value);

    int rPush(String key,String... value);

    int lPush(String key,String... value);

    String[] lrAnge(String key, String start,String stop);

    int lLen(String key);

    String get(String key);

    String hGet(String key,String field);

    String hDel(String key,String... field);

    int sRem(String key,String... value);

    DataType type(String key);

    String[] scan(String startIndex,String limit);

    String[] scanMatch(String startIndex,String pattern,String limit);

    ScanResult<String> sScan(String key,String startIndex,String limit);

    ScanResult<String> sScanMatch(String key,String startIndex,String pattern,String limit);

    ScanResult<Pair<String, String>> hScan(String key, String startIndex, String limit);

    ScanResult<Pair<String, String>> hScanMatch(String key, String startIndex,String pattern,String limit);

    String[] mGet(String... keys);

    String select(String index);

    String debugObject(String key);

    boolean auth(String password);

    RedisNodeInfo info();

    void close();

    boolean isActive();
}
