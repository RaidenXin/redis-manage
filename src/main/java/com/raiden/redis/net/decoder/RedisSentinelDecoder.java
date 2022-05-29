package com.raiden.redis.net.decoder;

import java.util.HashMap;
import java.util.Map;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 13:54 2022/5/29
 * @Modified By:
 */
public final class RedisSentinelDecoder {

    public static final <T> T decoder(Class<T> clazz,String[] datum){
        char separator = '-';
        Map<String, Object> data = new HashMap<>(datum.length);
        for (int i = 0; i < datum.length; i+=2) {
            String name = DecoderUtils.lineToHump(datum[i], separator);
            data.put(name, datum[i + 1]);
        }
        return DecoderUtils.build(clazz, data);
    }
}
