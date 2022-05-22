package com.raiden.redis.net.common;

public class RedisCommand {
        public static final String SET = "SET";
        public static final String GET = "GET";
        public static final String M_GET = "MGET";
        public static final String INFO = "INFO";
        public static final String SCAN = "SCAN";
        public static final String SELECT = "SELECT";
        public static final String AUTH = "AUTH";
        public static final String MEMORY = "MEMORY";
        //获取类型
        public static final String TYPE = "type";
        //关闭连接退出
        public static final String QUIT = "QUIT";
        //集群
        public static final String CLUSTER  = "CLUSTER";

        //Debug
        public static final String DEBUG  = "DEBUG";

        public static class Memory{
            public static final String USAGE = "USAGE";
        }

        public static class Scan{
            public static final String COUNT = "COUNT";
            public static final String MATCH = "MATCH";
        }

        public static class Debug {
            public static final String OBJECT = "OBJECT";
        }

        public static class HSet{
            public static final String H_SET = "HSET";
            public static final String H_GET = "HGET";
            public static final String H_SCAN = "HSCAN";
            public static final String H_M_SET = "HMSET";
            public static final String H_M_GET = "HMGET";
        }
    }
