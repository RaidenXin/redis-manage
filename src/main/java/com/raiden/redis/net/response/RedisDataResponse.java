package com.raiden.redis.net.response;

import com.raiden.redis.net.exception.RedisReadException;
import com.raiden.redis.net.exception.RedisWriteException;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 19:32 2022/5/29
 * @Modified By:
 */
public class RedisDataResponse implements RedisResponse{

    private Object body;
    private boolean success;
    private RuntimeException error;

    @Override
    public <T> T getBody() {
        return (T) body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public RuntimeException getError() {
        return error;
    }

    public void setError(RuntimeException error) {
        this.error = error;
    }


    public static final RedisDataResponse build(Object body){
        RedisDataResponse redisResponse = new RedisDataResponse();
        redisResponse.body = body;
        redisResponse.success = true;
        return redisResponse;
    }


    public static final RedisDataResponse build(Throwable error, boolean isRead){
        RedisDataResponse redisResponse = new RedisDataResponse();
        if (isRead){
            redisResponse.error = new RedisReadException(error.getMessage(), error);
        }else {
            redisResponse.error = new RedisWriteException(error.getMessage(), error);
        }
        redisResponse.success = false;
        return redisResponse;
    }
}
