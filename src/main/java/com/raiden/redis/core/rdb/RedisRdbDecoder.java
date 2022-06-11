package com.raiden.redis.core.rdb;

import java.nio.ByteBuffer;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:18 2022/6/11
 * @Modified By:
 */
public interface RedisRdbDecoder<T> {

    T decoder(ByteBuffer data,int offset);
}
