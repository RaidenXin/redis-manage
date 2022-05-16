package com.raiden.redis.model;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:36 2022/5/16
 * @Modified By:
 */
public class RedisCluster {
    private int clusterEnabled;

    public int getClusterEnabled() {
        return clusterEnabled;
    }

    public void setClusterEnabled(int clusterEnabled) {
        this.clusterEnabled = clusterEnabled;
    }

    @Override
    public String toString() {
        return "RedisCluster{" +
                "clusterEnabled=" + clusterEnabled +
                '}';
    }
}
