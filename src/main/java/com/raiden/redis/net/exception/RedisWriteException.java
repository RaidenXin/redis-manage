package com.raiden.redis.net.exception;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 14:58 2022/1/1
 * @Modified By:
 */
public class RedisWriteException extends RuntimeException {

    public RedisWriteException(String message, Throwable cause){
        super(message, cause);
    }
}
