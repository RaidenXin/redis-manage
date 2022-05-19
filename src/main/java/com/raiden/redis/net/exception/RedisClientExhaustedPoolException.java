package com.raiden.redis.net.exception;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:02 2022/1/2
 * @Modified By:
 */
public class RedisClientExhaustedPoolException extends RuntimeException{

    public RedisClientExhaustedPoolException(String message, Throwable cause){
        super(message, cause);
    }
}
