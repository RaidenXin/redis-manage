package com.raiden.redis.test;


import com.raiden.redis.client.AbstractRedisClient;
import com.raiden.redis.client.RedisClient;
import com.raiden.redis.client.RedisClusterClient;
import com.raiden.redis.client.RedisSingleClient;
import com.raiden.redis.decoder.RedisNodeInfoDecoder;
import com.raiden.redis.model.RedisClusterNodeInfo;
import com.raiden.redis.model.RedisNodeInfo;
import com.raiden.redis.pool.RedisClusterClientPool;
import com.raiden.redis.pool.RedisSingleClientPool;
import com.raiden.redis.ui.common.Path;
import com.raiden.redis.ui.mode.Record;
import com.raiden.redis.ui.util.PathUtils;
import com.raiden.redis.ui.util.RecordStorageUtils;
import com.raiden.redis.utils.RedisClusterSlotUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:02 2022/1/1
 * @Modified By:
 */
public class TestClient {

    private static final Logger LOGGER = LogManager.getLogger(TestClient.class);

    @Test
    public void testRedisClient(){
        RedisSingleClientPool redisClientPool = new RedisSingleClientPool("192.168.31.154",8010, 5);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            scheduledExecutorService.execute(() ->{
                AbstractRedisClient client = redisClientPool.getClient();
                System.out.println(client);
                countDownLatch.countDown();
                client.close();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试获取信息的方法
     */
    @Test
    public void testClusterInfo(){
        RedisClusterClientPool redisClientPool = new RedisClusterClientPool("127.0.0.1",8013, 5);
        RedisClient client = redisClientPool.getClient();
        String index = "0";
        for (;;){
            String[] scan = client.scan(index, "20");
            System.err.println(Arrays.toString(scan));
            index = scan[0];
            if ("0".equals(index)){
                break;
            }
        }
    }

    /**
     * 测试获取槽位的方法
     */
    @Test
    public void testSlotUtils(){
        //8192 - 10922
        for (int i = 1; i < 100; i++){
            int slot = RedisClusterSlotUtil.getSlot("aaa" + i);
            if (8191 < slot && slot < 10923){
                System.err.println("aaa" + i);
            }
        }
    }

    /**
     * 测试切换仓库
     */
    @Test
    public void testSelect(){
        RedisClusterClientPool redisClientPool = new RedisClusterClientPool("127.0.0.1",8013, 5);
        RedisClient client = redisClientPool.getClient();
        LOGGER.info(client.info());
    }


    @Test
    public void testRedisNodeSort(){
        RedisClusterClient redisClient = new RedisClusterClient("127.0.0.1",8013);
        List<RedisClusterNodeInfo> redisClusterNodes = redisClient.clusterNodes();
        System.err.println(redisClusterNodes.stream().sorted().map(RedisClusterNodeInfo::getHostAndPort).collect(Collectors.joining(" ")));
    }


    /**
     * 测试验证
     */
    @Test
    public void testRedisAuth(){
        RedisSingleClient redisClient = new RedisSingleClient("redis.test.yiyaowang.com",6379);
        System.err.println(redisClient.auth("foobared"));
        RedisNodeInfo info = redisClient.info();
        System.err.println(info);
    }

    /**
     * 测试槽位
     */
    @Test
    public void testRedisSlot(){
        RedisClusterClient redisClient = new RedisClusterClient("127.0.0.1",8013);
        System.err.println(redisClient.auth("foobared"));
        System.err.println(Arrays.toString(redisClient.clusterSlots()));
    }

    /**
     * 测试获取内存
     */
    @Test
    public void testRedisMemoryUsage(){
        RedisClusterClient redisClient = new RedisClusterClient("127.0.0.1",8013);
        System.err.println(redisClient.memoryUsage("aaa33"));
    }


    /**
     * 测试 Scan命令
     */
    @Test
    public void testRedisScanCommand(){
        RedisClusterClient redisClient = new RedisClusterClient("127.0.0.1",8013);
        System.err.println(Arrays.toString(redisClient.scan("0", "20")));
    }

    /**
     * 测试 Scan模糊匹配命令
     */
    @Test
    public void testRedisScanMatchCommand(){
        RedisClusterClient redisClient = new RedisClusterClient("127.0.0.1",8013);
        System.err.println(Arrays.toString(redisClient.scanMatch("0", "aaa*","20")));
    }

    /**
     * 测试存储记录的方法
     * @throws IOException
     */
    @Test
    public void testHistoricalRecord() throws IOException {
        String data = "048name:一号啊啊啊|host:127.0.0.1|port:8013|password:************************************************************************************************************************************************************************************************************************************************************";
        Record record = Record.build(data);
        List<Record> records = new ArrayList<>(1);
        records.add(record);
        RecordStorageUtils.refreshAndSaveRecords(records, Path.REDIS_CLUSTER_HISTORICAL_RECORD_DATA_PATH);
        records = RecordStorageUtils.getRecords(Path.REDIS_CLUSTER_HISTORICAL_RECORD_DATA_PATH);
        System.err.println(record);
    }

    /**
     * 测试获取根目录
     * @throws IOException
     */
    @Test
    public void testPath() throws IOException {
        String path = PathUtils.getRootPath();
        LOGGER.error(path);
    }


    /**
     * 测试下划线转驼峰
     * @throws IOException
     */
    @Test
    public void testLineToHump() {
        String str = "used_cpu_user_main_thread";
        String path = RedisNodeInfoDecoder.lineToHump(str);
        LOGGER.error(path);
    }


    /**
     * 测试清除尾部字符串
     * @throws IOException
     */
    @Test
    public void testClearTheTrailingNumber() {
        String str = "used_cpu_user_main_thread0000";
        int count = RedisNodeInfoDecoder.countTheNumberOfTrailingDigits(str);
        str = str.substring(0, str.length() - count);
        LOGGER.error(count);
        LOGGER.error(str);
    }

    /**
     * 测试 String 转 Map
     * @throws IOException
     */
    @Test
    public void testString2Map() {
        String value = "keys=19,expires=0,avg_ttl=0";
        Map<String, Object> stringObjectMap = RedisNodeInfoDecoder.string2Map(value);
        LOGGER.error(stringObjectMap);
    }
}
