package com.raiden.redis.model;

import com.raiden.redis.exception.RedisReadException;
import com.raiden.redis.exception.RedisWriteException;

public class RedisSingleResponse implements RedisResponse {

    private String body;
    private boolean success;
    private RuntimeException error;

    public <T> T getBody() {
        return (T) body;
    }

    public void setBody(String body) {
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

    public static final RedisSingleResponse build(String body){
        RedisSingleResponse redisResponse = new RedisSingleResponse();
        redisResponse.body = body;
        redisResponse.success = true;
        return redisResponse;
    }


    public static final RedisSingleResponse build(Throwable error, boolean isRead){
        RedisSingleResponse redisResponse = new RedisSingleResponse();
        if (isRead){
            redisResponse.error = new RedisReadException(error.getMessage(), error);
        }else {
            redisResponse.error = new RedisWriteException(error.getMessage(), error);
        }
        redisResponse.success = false;
        return redisResponse;
    }
}
