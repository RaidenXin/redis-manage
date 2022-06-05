package com.raiden.redis.ui.controller;

import com.raiden.redis.ui.mode.RedisNode;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 20:05 2022/5/22
 * @Modified By:
 */
public interface DataViewController {

    void init(RedisNode redisNode, String key);
}
