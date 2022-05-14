package com.raiden.redis.cluster;

import com.raiden.redis.model.RedisNodeInfo;
import org.apache.commons.lang3.StringUtils;


/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 14:23 2022/5/14
 * @Modified By:
 */
public final class RedisDecoder {

    public static final String LINE_BREAK = "\r\n";

    public static RedisNodeInfo redisNodesDecoder(String response){
        if (StringUtils.isBlank(response)){
            return null;
        }else {
            String[] lines = StringUtils.split(response, LINE_BREAK);
            if (lines.length > 0){
                return RedisNodeInfo.build(lines);
            }else {
                return null;
            }
        }
    }
}
