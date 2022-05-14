package com.raiden.redis.ui.mode;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 10:58 2022/5/14
 * @Modified By:
 */
public class RedisDataItem {

    private String key;
    private String value;

    public RedisDataItem(String key,String value){
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
