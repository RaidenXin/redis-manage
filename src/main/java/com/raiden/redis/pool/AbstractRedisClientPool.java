package com.raiden.redis.pool;

import com.raiden.redis.client.RedisClient;
import com.raiden.redis.exception.RedisClientConnectionException;
import com.raiden.redis.exception.RedisClientException;
import com.raiden.redis.exception.RedisClientExhaustedPoolException;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.NoSuchElementException;


/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 20:38 2022/1/1
 * @Modified By:
 */
public abstract class AbstractRedisClientPool<T extends RedisClient> implements RedisClientPool<T> {

    private EventLoopGroup group;

    private GenericObjectPool<T> genericObjectPool;

    public AbstractRedisClientPool(String host, int port, int poolSize){
        this.group = new NioEventLoopGroup(poolSize);
        GenericObjectPoolConfig<T> genericObjectPoolConfig = new GenericObjectPoolConfig<>();
        genericObjectPoolConfig.setMaxTotal(poolSize);
        this.genericObjectPool = new GenericObjectPool<T>(getPooledObjectFactory(this, this.group, host, port) , genericObjectPoolConfig);
    }

    protected abstract PooledObjectFactory getPooledObjectFactory(RedisClientPool pool, EventLoopGroup group, String host, int port);

    public T getClient(){
        try {
            return genericObjectPool.borrowObject();
        } catch (NoSuchElementException nse) {
            if (null == nse.getCause()) {
                throw new RedisClientExhaustedPoolException(
                        "Could not get a resource since the pool is exhausted", nse);
            }
            throw nse;
        } catch (Exception e) {
            throw new RedisClientConnectionException("Could not get a resource from the pool", e);
        }
    }

    /**
     * 回收对象
     * @param client
     */
    public void recycleObject(T client){
        if (client != null){
            if (client.isActive()){
                genericObjectPool.returnObject(client);
            }else {
                try {
                    genericObjectPool.invalidateObject(client);
                } catch (Exception e) {
                    throw new RedisClientException("", e);
                }
            }
        }
    }


    public void close(){
        if (group != null){
            group.shutdownGracefully();
        }
    }
}
