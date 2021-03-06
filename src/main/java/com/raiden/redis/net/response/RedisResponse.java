package com.raiden.redis.net.response;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 22:45 2022/5/9
 * @Modified By:
 */
public interface RedisResponse {

    <T> T getBody();

    boolean isSuccess();

    RuntimeException getError();
}
