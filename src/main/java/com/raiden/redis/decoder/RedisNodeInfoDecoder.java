package com.raiden.redis.decoder;

import com.raiden.redis.model.*;
import com.raiden.redis.ui.Window;
import com.raiden.redis.utils.GeneralDataTypeConversionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:07 2022/5/16
 * @Modified By:
 */
public final class RedisNodeInfoDecoder {

    public static final Logger LOGGER = LogManager.getLogger(Window.class);

    private static final Map<String, Class> CLASS_MAP = new HashMap<>();

    private static final Map<String, BiConsumer> FUNCTION_MAP = new HashMap<>();


    private static final String SERVER = "# Server";
    private static final String CLIENTS = "# Clients";
    private static final String MEMORY = "# Memory";
    private static final String PERSISTENCE = "# Persistence";
    private static final String STATS = "# Stats";
    private static final String REPLICATION = "# Replication";
    private static final String CPU = "# CPU";
    private static final String CLUSTER = "# Cluster";
    private static final String KEYSPACE = "# Keyspace";

    static {
        CLASS_MAP.put(SERVER, RedisServerInfo.class);
        CLASS_MAP.put(CLIENTS, RedisClientInfo.class);
        CLASS_MAP.put(MEMORY, RedisMemoryInfo.class);
        CLASS_MAP.put(PERSISTENCE, RedisPersistence.class);
        CLASS_MAP.put(STATS, RedisStats.class);
        CLASS_MAP.put(REPLICATION, ReidsReplication.class);
        CLASS_MAP.put(CPU, RedisCpuInfo.class);
        CLASS_MAP.put(CLUSTER, RedisCluster.class);
        CLASS_MAP.put(CLUSTER, RedisCluster.class);

        BiConsumer<RedisNodeInfo, RedisServerInfo> setServer = RedisNodeInfo::setServer;
        FUNCTION_MAP.put(SERVER, setServer);
        BiConsumer<RedisNodeInfo, RedisClientInfo> setClients = RedisNodeInfo::setClients;
        FUNCTION_MAP.put(CLIENTS, setClients);
        BiConsumer<RedisNodeInfo, RedisMemoryInfo> setMemory = RedisNodeInfo::setMemory;
        FUNCTION_MAP.put(MEMORY, setMemory);
        BiConsumer<RedisNodeInfo, RedisPersistence> setPersistence = RedisNodeInfo::setPersistence;
        FUNCTION_MAP.put(PERSISTENCE, setPersistence);
        BiConsumer<RedisNodeInfo, RedisStats> setStats = RedisNodeInfo::setStats;
        FUNCTION_MAP.put(STATS, setStats);
        BiConsumer<RedisNodeInfo, ReidsReplication> setReplication = RedisNodeInfo::setReplication;
        FUNCTION_MAP.put(REPLICATION, setReplication);
        BiConsumer<RedisNodeInfo, RedisCpuInfo> setCpu = RedisNodeInfo::setCpu;
        FUNCTION_MAP.put(CPU, setCpu);
        BiConsumer<RedisNodeInfo, RedisCluster> setCluster = RedisNodeInfo::setCluster;
        FUNCTION_MAP.put(REPLICATION, setCluster);
    }


    public static final RedisNodeInfo decoder(String[] datum){
        RedisNodeInfo redisNodeInfo = new RedisNodeInfo();
        if (datum == null || datum.length == 0){
            return redisNodeInfo;
        }
        for (String data : datum){
            if (data.startsWith("#")){

            }
        }
        return redisNodeInfo;
    }


    public static final <T> T build(Class<T> clazz, Map<String, String> data){
        try {
            T t = clazz.newInstance();
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields){
                field.setAccessible(true);
                String name = field.getName();
                String value = data.get(name);
                if (StringUtils.isNotBlank(value)){
                    field.set(t, GeneralDataTypeConversionUtils.conversion(field.getType(), value));
                }
            }
            return t;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }
}
