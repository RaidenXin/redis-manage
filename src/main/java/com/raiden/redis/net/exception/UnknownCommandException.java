package com.raiden.redis.net.exception;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:37 2022/6/1
 * @Modified By:
 */
public class UnknownCommandException extends RedisException{

    public UnknownCommandException(String message, Throwable cause){
        super(message, cause);
    }

    public UnknownCommandException(String message) {
        super(message);
    }
}
