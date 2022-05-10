package com.raiden.redis.exception;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 14:57 2022/1/1
 * @Modified By:
 */
public class RedisReadException extends RuntimeException {
    
    public RedisReadException(String message, Throwable cause){
        super(message, cause);
    }
}
