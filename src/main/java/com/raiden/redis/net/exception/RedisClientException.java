package com.raiden.redis.net.exception;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 22:29 2022/1/2
 * @Modified By:
 */
public class RedisClientException extends RuntimeException{

    public RedisClientException(String message, Throwable cause){
        super(message, cause);
    }

    public RedisClientException(String message){
        super(message);
    }
}
