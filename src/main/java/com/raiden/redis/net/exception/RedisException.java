package com.raiden.redis.net.exception;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:20 2022/5/22
 * @Modified By:
 */
public class RedisException extends RuntimeException{

    public RedisException(String message, Throwable cause){
        super(message, cause);
    }

    public RedisException(String message){
        super(message);
    }
}
