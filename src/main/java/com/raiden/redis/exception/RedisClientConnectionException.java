package com.raiden.redis.exception;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:07 2022/1/2
 * @Modified By:
 */
public class RedisClientConnectionException extends RuntimeException{

    public RedisClientConnectionException(String message, Throwable cause){
        super(message, cause);
    }
}
