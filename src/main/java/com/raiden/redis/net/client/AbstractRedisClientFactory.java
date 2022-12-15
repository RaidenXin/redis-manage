package com.raiden.redis.net.client;

import com.raiden.redis.net.core.RedisClientInitializer;
import com.raiden.redis.net.exception.RedisClientConnectionException;
import com.raiden.redis.net.handle.RedisClientHandler;
import com.raiden.redis.net.pool.RedisClientPool;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * Redis client 抽象工厂
 * @author xinlei
 */
public abstract class AbstractRedisClientFactory<T extends AbstractRedisClient> implements PooledObjectFactory<T> {

    private RedisClientHandler handler;
    private Bootstrap bootstrap;
    private RedisClientPool pool;

    private String host;
    private int port;

    protected AbstractRedisClientFactory(EventLoopGroup group, RedisClientPool pool, String host, int port) {
        this.handler = new RedisClientHandler();
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new RedisClientInitializer(handler));
        this.pool = pool;
        this.host = host;
        this.port = port;
    }

    /**
     * 获取池中对象的时候使用
     * @param p
     * @throws Exception
     */
    @Override
    public void activateObject(PooledObject<T> p) throws Exception {
        T client = p.getObject();
        //如果连接不活跃 重新创建新的
        if (!client.isActive()) {
            Channel channel = this.bootstrap.connect(host, port).sync().channel();
            client.setChannel(channel);
        }
    }

    /**
     * 销毁一个连接
     * @param p
     * @throws Exception
     */
    @Override
    public void destroyObject(PooledObject<T> p) throws Exception {
        T client = p.getObject();
        client.close();
    }

    /**
     * 创建对象
     * @return
     * @throws Exception
     */
    @Override
    public PooledObject<T> makeObject() throws Exception {
        ChannelFuture channelFuture = this.bootstrap.connect(host, port);
        channelFuture.addListener((ChannelFuture future) -> {
            if (!future.isSuccess()) {
                throw new RedisClientConnectionException("The connection fails", future.cause());
            }
        });
        Channel channel = channelFuture.sync().channel();
        T client = getRedisClient(channel, this.pool, this.handler);
        return new DefaultPooledObject<T>(client);
    }

    /**
     * 获取 redis client
     * @param channel
     * @param pool
     * @param handler
     * @return
     */
    protected abstract T getRedisClient(Channel channel, RedisClientPool pool, RedisClientHandler handler);

    /**
     * 归还连接时候调用
     * @param p
     * @throws Exception
     */
    @Override
    public void passivateObject(PooledObject<T> p) throws Exception {
    }

    /**
     * 获取 和 归还 以及内置后台线程检查闲置情况时，可以通过验证去除一些对象
     */
    @Override
    public boolean validateObject(PooledObject<T> p) {
        T client = p.getObject();
        //只要 活跃的连接
        return client.isActive();
    }
}
