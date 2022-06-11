package com.raiden.redis.net.client;

import com.raiden.redis.net.cluster.RedisDecoder;
import com.raiden.redis.net.common.DataType;
import com.raiden.redis.net.common.RedisCommand;
import com.raiden.redis.net.decoder.RedisSentinelDecoder;
import com.raiden.redis.net.exception.RedisException;
import com.raiden.redis.net.handle.RedisClientHandler;
import com.raiden.redis.net.model.RedisNodeInfo;
import com.raiden.redis.net.model.ScanResult;
import com.raiden.redis.net.model.sentinel.RedisMaster;
import com.raiden.redis.net.model.sentinel.RedisSlave;
import com.raiden.redis.net.pool.RedisClientPool;
import com.raiden.redis.ui.mode.RedisNode;
import com.raiden.redis.ui.mode.RedisSingleNode;
import io.netty.channel.Channel;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 22:23 2022/5/28
 * @Modified By:
 */
public class RedisSentinelClient extends AbstractRedisClient{

    public RedisSentinelClient(String host, int port){
        super(host, port);
    }

    public RedisSentinelClient(Channel channel, RedisClientPool pool, RedisClientHandler handler) {
        super(channel, pool, handler);
    }

    @Override
    public boolean set(String key, String value) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public boolean hSet(String key, String field, String value) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public boolean lSet(String key, String index, String value) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public int sAdd(String key, String... value) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public int zAdd(String key, String score, String value) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public int rPush(String key, String... value) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public int lPush(String key, String... value) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public String[] lrAnge(String key, String start, String stop) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public int lLen(String key) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public String get(String key) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public String hGet(String key, String field) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public int hDel(String key, String... field) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public int zRemRangeByScore(String key, String min, String max) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public int sRem(String key, String... value) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public int zRem(String key, String... value) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public String zScore(String key, String member) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public boolean sIsMember(String key, String value) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public DataType type(String key) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public String[] scan(String startIndex, String limit) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public String[] scanMatch(String startIndex, String pattern, String limit) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public ScanResult<String> sScan(String key, String startIndex, String limit) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public ScanResult<String> sScanMatch(String key, String startIndex, String pattern, String limit) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public ScanResult<Pair<String, String>> hScan(String key, String startIndex, String limit) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public ScanResult<Pair<String, String>> hScanMatch(String key, String startIndex, String pattern, String limit) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public ScanResult<Pair<String, String>> zScan(String key, String startIndex, String limit) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public ScanResult<Pair<String, String>> zScanMatch(String key, String startIndex, String pattern, String limit) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public Pair<String, String>[] zRangeByScore(String key, String min, String max, String startIndex, String limit) {
       throw new RedisException("The method cannot be used!");
    }

    @Override
    public int zCount(String key, String min, String max) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public String[] mGet(String... keys) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public String select(String index) {
        throw new RedisException("The method cannot be used!");
    }

    @Override
    public String debugObject(String key) {
        throw new RedisException("The method cannot be used!");
    }

    public List<RedisMaster> getMasters(){
        String[][] masters = sendCommands(RedisCommand.SENTINEL, RedisCommand.Sentinel.MASTERS);
        List<RedisMaster> redisNodes = new ArrayList<>(masters.length);
        for (String[] master: masters){
            RedisMaster redisMaster = RedisSentinelDecoder.decoder(RedisMaster.class, master);
            redisNodes.add(redisMaster);
        }
        return redisNodes;
    }
    public List<RedisSlave> getSlaves(String masterName){
        String[][] slaves = sendCommands(RedisCommand.SENTINEL, RedisCommand.Sentinel.SLAVES, masterName);
        List<RedisSlave> redisNodes = new ArrayList<>(slaves.length);
        for (String[] master: slaves){
            RedisSlave redisSlave = RedisSentinelDecoder.decoder(RedisSlave.class, master);
            redisNodes.add(redisSlave);
        }
        return redisNodes;
    }

    public List<RedisNode> getMasterAddrByName(String masterName){
        String[] hostAndPort = sendCommands(RedisCommand.SENTINEL, RedisCommand.Sentinel.SENTINEL_GET_MASTER_ADDR_BY_NAME, masterName);
        List<RedisNode> redisNodes = new ArrayList<>(hostAndPort.length >> 1);
        for (int i = 0; i < hostAndPort.length; i += 2){
            RedisSingleNode redisNode = RedisSingleNode.build(hostAndPort[0], Integer.parseInt(hostAndPort[1]));
            redisNodes.add(redisNode);
        }
        return redisNodes;
    }
}
