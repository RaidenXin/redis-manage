package com.raiden.redis.net.exception;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 22:16 2022/5/22
 * @Modified By:
 */
public class MovedException extends RedisException{

    public MovedException(String message, Throwable cause){
        super(message, cause);
    }

    public MovedException(String message) {
        super(message);
    }
}
