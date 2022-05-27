package com.raiden.redis.net.client;

import com.raiden.redis.net.cluster.RedisDecoder;
import com.raiden.redis.net.common.DataType;
import com.raiden.redis.net.common.RedisCommand;
import com.raiden.redis.net.core.RedisClientInitializer;
import com.raiden.redis.net.handle.RedisClientHandler;
import com.raiden.redis.net.model.RedisNodeInfo;
import com.raiden.redis.net.model.ScanResult;
import com.raiden.redis.net.pool.RedisClientPool;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRedisClient implements RedisClient{

    private static final Logger LOGGER = LogManager.getLogger(AbstractRedisClient.class);

    private static final String SUCCESS = "OK";


    private Channel channel;
    private RedisClientHandler handler;
    private EventLoopGroup group;
    private RedisClientPool pool;
    //是否池化对象
    private boolean isObjectPooling;

    public AbstractRedisClient(String host, int port){
        this.group = new NioEventLoopGroup();
        this.handler = new RedisClientHandler();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new RedisClientInitializer(handler));

        try {
            this.channel = bootstrap.connect(host, port).sync().channel();
        } catch (InterruptedException e) {
            group.shutdownGracefully();
        }
    }

    public AbstractRedisClient(Channel channel, RedisClientPool pool, RedisClientHandler handler){
        this.channel = channel;
        this.pool = pool;
        this.handler = handler;
        this.isObjectPooling = pool != null;
    }

    public boolean set(String key,String value){
        if (StringUtils.isBlank(key)){
            throw new NullPointerException("key is null");
        }
        if (StringUtils.isBlank(value)){
            throw new NullPointerException("value is null");
        }
        String response = sendCommands(RedisCommand.SET, key, value);
        return SUCCESS.equals(response);
    }

    @Override
    public String hGet(String key, String field) {
        if (StringUtils.isBlank(key)){
            throw new NullPointerException("key is null");
        }
        if (StringUtils.isBlank(field)){
            throw new NullPointerException("field is null");
        }
        return sendCommands(RedisCommand.Hash.H_GET, key, field);
    }

    @Override
    public String hDel(String key, String... field) {
        if (StringUtils.isBlank(key)){
            throw new NullPointerException("key is null");
        }
        String[] commands = encapsulationCommands(RedisCommand.Hash.H_DEL, key, field);
        return sendCommands(commands);
    }

    @Override
    public boolean hSet(String key, String field, String value) {
        String response = sendCommands(RedisCommand.Hash.H_SET, key, field, value);
        return SUCCESS.equalsIgnoreCase(response);
    }

    @Override
    public boolean lSet(String key, String index, String value) {
        String response = sendCommands(RedisCommand.List.L_SET, key, index, value);
        return SUCCESS.equalsIgnoreCase(response);
    }

    @Override
    public int sAdd(String key, String... value) {
        String[] commands = encapsulationCommands(RedisCommand.Set.S_ADD, key, value);
        String response = sendCommands(commands);
        return Integer.parseInt(response);
    }

    @Override
    public int sRem(String key, String... value) {
        String[] commands = encapsulationCommands(RedisCommand.Set.S_REM, key, value);
        String response = sendCommands(commands);
        return Integer.parseInt(response);
    }

    @Override
    public int rPush(String key, String... value) {
        String[] commands = encapsulationCommands(RedisCommand.List.R_PUSH, key, value);
        String response = sendCommands(commands);
        return Integer.parseInt(response);
    }

    @Override
    public int lPush(String key, String... value) {
        String[] commands = encapsulationCommands(RedisCommand.List.L_PUSH, key, value);
        String response = sendCommands(commands);
        return Integer.parseInt(response);
    }

    @Override
    public String[] lrAnge(String key, String start,String stop) {
        return  sendCommands(RedisCommand.List.LR_ANGE, key, start, stop);
    }

    @Override
    public int lLen(String key) {
        return Integer.parseInt(sendCommands(RedisCommand.List.L_LEN, key));
    }

    public String get(String key){
        if (StringUtils.isBlank(key)){
            throw new NullPointerException("key is null");
        }
        return sendCommands(RedisCommand.GET, key);
    }

    public DataType type(String key){
        if (StringUtils.isBlank(key)){
            throw new NullPointerException("key is null");
        }
        String type = sendCommands(RedisCommand.TYPE, key);
        return DataType.of(type);
    }

    @Override
    public String debugObject(String key) {
        if (StringUtils.isBlank(key)){
            throw new NullPointerException("key is null");
        }
        return sendCommands(RedisCommand.DEBUG, RedisCommand.Debug.OBJECT, key);
    }

    public String[] scan(String startIndex, String limit){
        return sendCommands(RedisCommand.SCAN, startIndex, RedisCommand.Scan.COUNT, limit);
    }

    public String[] scanMatch(String startIndex,String pattern,String limit){
        return sendCommands(RedisCommand.SCAN, startIndex, RedisCommand.Scan.MATCH, pattern, RedisCommand.Scan.COUNT, limit);
    }

    public ScanResult<String> sScan(String key,String startIndex, String limit){
        String[] result =  sendCommands(RedisCommand.Set.S_SCAN, key, startIndex, RedisCommand.Scan.COUNT, limit);
        String cursor = result[0];
        List<String> pairs = new ArrayList<>(result.length - 1);
        for (int i = 1;i < result.length;i++){
            pairs.add(result[i]);
        }
        return ScanResult.build(cursor, pairs);
    }

    public ScanResult<String> sScanMatch(String key,String startIndex,String pattern,String limit){
        String[] result = sendCommands(RedisCommand.Set.S_SCAN, key, startIndex, RedisCommand.Scan.MATCH, pattern, RedisCommand.Scan.COUNT, limit);
        String cursor = result[0];
        List<String> pairs = new ArrayList<>(result.length - 1);
        for (int i = 1;i < result.length;i++){
            pairs.add(result[i]);
        }
        return ScanResult.build(cursor, pairs);
    }

    public ScanResult<Pair<String, String>> hScan(String key, String startIndex, String limit){
        String[] result = sendCommands(RedisCommand.Hash.H_SCAN, key, startIndex, RedisCommand.Scan.COUNT, limit);
        String cursor = result[0];
        List<Pair<String,String>> pairs = new ArrayList<>(result.length - 1);
        for (int i = 1;i < result.length;i+=2){
            pairs.add(new Pair<>(result[i], result[i + 1]));
        }
        return ScanResult.build(cursor, pairs);
    }

    public ScanResult<Pair<String, String>> hScanMatch(String key, String startIndex, String pattern, String limit){
        String[] result = sendCommands(RedisCommand.Hash.H_SCAN, key, startIndex,RedisCommand.Scan.MATCH, pattern, RedisCommand.Scan.COUNT, limit);
        String cursor = result[0];
        List<Pair<String,String>> pairs = new ArrayList<>(result.length - 1);
        for (int i = 1;i < result.length;i+=2){
            pairs.add(new Pair<>(result[i], result[i + 1]));
        }
        return ScanResult.build(cursor, pairs);
    }

    @Override
    public String select(String index) {
        return sendCommands(RedisCommand.SELECT, index);
    }

    protected void setChannel(Channel channel){
        this.channel = channel;
    }

    public RedisNodeInfo info(){
        String response = sendCommands(RedisCommand.INFO);
        return RedisDecoder.redisNodesDecoder(response);
    }

    public boolean auth(String password){
        String response = sendCommands(RedisCommand.AUTH, password);
        return SUCCESS.equalsIgnoreCase(response);
    }

    public String memoryUsage(String key){
        String response = sendCommands(RedisCommand.MEMORY, RedisCommand.Memory.USAGE, key);
        return response;
    }


    private String[] encapsulationCommands(String command1,String command2,String... values){
        String[] commands = new String[values.length + 2];
        commands[0] = command1;
        commands[1] = command2;
        System.arraycopy(values, 0, commands, 2, values.length);
        return commands;
    }

    protected <T> T sendCommands(String... commands){
        if (channel != null){
            //发送命令
            ChannelFuture channelFuture = channel.writeAndFlush(commands);
            //设置错误监听
            channelFuture.addListener((ChannelFuture future) -> {
                if (!future.isSuccess()) {
                    Throwable cause = future.cause();
                    Channel channel = future.channel();
                    handler.caught(channel, cause);
                }
            });
        }
        return handler.getResponse(channel);
    }

    public void close(){
        //是否是池化对象
        if (isObjectPooling && pool != null){
            //如果是池化对象 用池化对象回收方式
            pool.recycleObject(this);
        }else {
            //如果不是池化对象 关闭时关闭连接
            if (channel != null && channel.isActive()){
                try {
                    sendCommands(RedisCommand.QUIT);
                    channel.close();
                }catch (Exception e){
                }
            }
            //关闭 netty 客户端线程池
            if (group != null){
                group.shutdownGracefully();
            }
        }
    }

    public boolean isActive(){
        if (channel == null){
            return false;
        }else {
            return channel.isActive();
        }
    }
}
