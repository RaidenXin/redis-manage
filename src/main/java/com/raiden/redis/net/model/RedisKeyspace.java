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
        private long keys;
        /**
         * 过期时间个数
         */
        private long expires;
        /**
         * 设置生存时间键的平均寿命
         */
        private long avgTtl;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getKeys() {
            return keys;
        }

        public void setKeys(long keys) {
            this.keys = keys;
        }

        public long getExpires() {
            return expires;
        }

        public void setExpires(long expires) {
            this.expires = expires;
        }

        public long getAvgTtl() {
            return avgTtl;
        }

        public void setAvgTtl(long avgTtl) {
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
