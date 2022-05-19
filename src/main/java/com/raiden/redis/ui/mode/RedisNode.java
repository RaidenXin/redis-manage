package com.raiden.redis.ui.mode;

import com.raiden.redis.net.client.RedisClient;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 22:10 2022/5/10
 * @Modified By:
 */
public interface RedisNode {

    String getHostAndPort();

    RedisClient getRedisClient();

    boolean isMyself();
}
