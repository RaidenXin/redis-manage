package com.raiden.redis.model;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 20:07 2022/5/15
 * @Modified By:
 */
public class RedisKeyspace {
    /**
     * 仓库名称
     */
    private String keyspaceName;
    /**
     *  Key数量
     */
    private int keys;
    /**
     * 过期时间个数
     */
    private int expires;
    /**
     * 设置生存时间键的平均寿命
     */
    private int avgTtl;

    public String getKeyspaceName() {
        return keyspaceName;
    }

    public void setKeyspaceName(String keyspaceName) {
        this.keyspaceName = keyspaceName;
    }

    public int getKeys() {
        return keys;
    }

    public void setKeys(int keys) {
        this.keys = keys;
    }

    public int getExpires() {
        return expires;
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }

    public int getAvgTtl() {
        return avgTtl;
    }

    public void setAvgTtl(int avgTtl) {
        this.avgTtl = avgTtl;
    }

    @Override
    public String toString() {
        return "RedisKeyspace{" +
                "keyspaceName='" + keyspaceName + '\'' +
                ", keys=" + keys +
                ", expires=" + expires +
                ", avgTtl=" + avgTtl +
                '}';
    }
}
