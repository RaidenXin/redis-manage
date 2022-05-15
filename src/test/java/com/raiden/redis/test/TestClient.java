package com.raiden.redis.test;

import com.raiden.redis.client.AbstractRedisClient;
import com.raiden.redis.client.RedisClient;
import com.raiden.redis.client.RedisClusterClient;
import com.raiden.redis.client.RedisSingleClient;
import com.raiden.redis.model.RedisClusterNodeInfo;
import com.raiden.redis.pool.RedisSingleClientPool;
import com.raiden.redis.pool.RedisClusterClientPool;
import com.raiden.redis.ui.Window;
import com.raiden.redis.ui.common.PathData;
import com.raiden.redis.ui.mode.Record;
import com.raiden.redis.ui.util.RecordStorageUtils;
import com.raiden.redis.utils.RedisClusterSlotUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
        System.err.println(client.info());
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
        RedisSingleClient redisClient = new RedisSingleClient("10.6.168.210",6379);
        System.err.println(redisClient.auth("foobared"));
        System.err.println(redisClient.info());
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
        RecordStorageUtils.refreshAndSaveRecords(records, PathData.REDIS_CLUSTER_HISTORICAL_RECORD_DATA_PATH);
        records = RecordStorageUtils.getRecords(PathData.REDIS_CLUSTER_HISTORICAL_RECORD_DATA_PATH);
        System.err.println(record);
    }
}
