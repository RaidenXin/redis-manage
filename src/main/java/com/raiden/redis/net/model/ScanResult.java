package com.raiden.redis.net.model;

import java.util.List;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:47 2022/5/22
 * @Modified By:
 */
public class ScanResult<T> {
    //游标
    private String cursor;

    private List<T> result;

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public static  <T> ScanResult<T> build(String cursor,List<T> result){
        ScanResult<T> scanResult = new ScanResult<>();
        scanResult.cursor = cursor;
        scanResult.result = result;
        return scanResult;
    }

    @Override
    public String toString() {
        return "ScanResult{" +
                "cursor='" + cursor + '\'' +
                ", result=" + result +
                '}';
    }
}
