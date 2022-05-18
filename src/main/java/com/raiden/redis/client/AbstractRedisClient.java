package com.raiden.redis.client;

import com.raiden.redis.cluster.RedisDecoder;
import com.raiden.redis.core.RedisClientInitializer;
import com.raiden.redis.handle.RedisClientHandler;
import com.raiden.redis.model.RedisNodeInfo;
import com.raiden.redis.pool.RedisClientPool;
import com.raiden.redis.ui.Window;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    public String set(String key,String value){
        if (StringUtils.isBlank(key)){
            throw new NullPointerException("key is null");
        }
        if (StringUtils.isBlank(value)){
            throw new NullPointerException("value is null");
        }
        return sendCommands(RedisCommand.SET, key, value);
    }

    public String get(String key){
        if (StringUtils.isBlank(key)){
            throw new NullPointerException("key is null");
        }
        return sendCommands(RedisCommand.GET, key);
    }

    public String[] scan(String startIndex,String limit){
        return sendCommands(RedisCommand.SCAN, startIndex,RedisCommand.Scan.COUNT, limit);
    }

    public String[] scanMatch(String startIndex,String pattern,String limit){
        return sendCommands(RedisCommand.SCAN, startIndex,RedisCommand.Scan.MATCH, pattern, RedisCommand.Scan.COUNT, limit);
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
        LOGGER.error(response);
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


    protected static class RedisCommand {
        public static final String SET = "SET";
        public static final String GET = "GET";
        public static final String INFO = "INFO";
        public static final String SCAN = "SCAN";
        public static final String SELECT = "SELECT";
        public static final String MGET = "MGET";
        public static final String AUTH = "AUTH";
        public static final String MEMORY = "MEMORY";
        //集群
        public static final String CLUSTER  = "CLUSTER";

        protected static class Memory{
            public static final String USAGE = "USAGE";
        }

        protected static class Scan{
            public static final String COUNT = "COUNT";
            public static final String MATCH = "MATCH";
        }
    }
}
