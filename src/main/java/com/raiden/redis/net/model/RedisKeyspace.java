package com.raiden.redis.net.model;

import java.util.List;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 20:07 2022/5/15
 * @Modified By:
 */
public class RedisKeyspace {
    private List<RedisDB> db;

    public List<RedisDB> getDb() {
        return db;
    }

    public void setDb(List<RedisDB> db) {
        this.db = db;
    }

    @Override
    public String toString() {
        return "RedisKeyspace{" +
                "db=" + db +
                '}';
    }

    public static class RedisDB {
        /**
         * 仓库名称
         */
        private String name;
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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
            return "RedisDB{" +
                    "name='" + name + '\'' +
                    ", keys=" + keys +
                    ", expires=" + expires +
                    ", avgTtl=" + avgTtl +
                    '}';
        }
    }

}
