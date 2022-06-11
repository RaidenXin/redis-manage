package com.raiden.redis.core.rdb.decoder;

import com.raiden.redis.core.rdb.RedisRdbDecoder;

import java.nio.ByteBuffer;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:20 2022/6/11
 * @Modified By:
 */
public class StringRdbDecoder implements RedisRdbDecoder {
    @Override
    public Object decoder(ByteBuffer data, int offset) {
        return null;
    }
}
