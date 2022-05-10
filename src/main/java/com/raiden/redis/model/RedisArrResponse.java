package com.raiden.redis.model;

import com.raiden.redis.exception.RedisReadException;
import com.raiden.redis.exception.RedisWriteException;

public class RedisArrResponse implements RedisResponse{

    private String[] bodys;
    private boolean success;
    private RuntimeException error;

    public String[] getBodys() {
        return bodys;
    }

    public void setBodys(String[] bodys) {
        this.bodys = bodys;
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

    public static final RedisArrResponse build(String[] bodys){
        RedisArrResponse redisResponse = new RedisArrResponse();
        redisResponse.bodys = bodys;
        redisResponse.success = true;
        return redisResponse;
    }


    public static final RedisArrResponse build(Throwable error, boolean isRead){
        RedisArrResponse redisResponse = new RedisArrResponse();
        if (isRead){
            redisResponse.error = new RedisReadException(error.getMessage(), error);
        }else {
            redisResponse.error = new RedisWriteException(error.getMessage(), error);
        }
        redisResponse.success = false;
        return redisResponse;
    }

    @Override
    public <T> T getBody() {
        return (T) bodys;
    }
}
