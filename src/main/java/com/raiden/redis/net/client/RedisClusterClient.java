package com.raiden.redis.net.client;

import com.raiden.redis.net.cluster.RedisClusterDecoder;
import com.raiden.redis.net.common.RedisCommand;
import com.raiden.redis.net.handle.RedisClientHandler;
import com.raiden.redis.net.model.RedisClusterNodeInfo;
import com.raiden.redis.net.pool.RedisClientPool;
import com.raiden.redis.net.utils.RedisClusterSlotUtil;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 23:45 2022/5/8
 * @Modified By:
 */
public class RedisClusterClient extends AbstractRedisClient{

    public RedisClusterClient(String host, int port){
        super(host, port);
    }
    
    public RedisClusterClient(Channel channel, RedisClientPool pool, RedisClientHandler handler) {
        super(channel, pool, handler);
    }

    public String clusterInfo(){
        String response = sendCommands(RedisCommand.CLUSTER, RedisClusterCommand.CLUSTER_INFO);
        return response;
    }

    public String[] mGet(String... keys){
        Map<Integer, List<String>> keysMap = Stream.of(keys).collect(Collectors.groupingBy(RedisClusterSlotUtil::getSlot));
        Map<String, String> map = new HashMap<>(keys.length << 1);
        for (Map.Entry<Integer, List<String>> entry : keysMap.entrySet()){
            List<String> keyList = entry.getValue();
            int size = keyList.size();
            String[] commands = new String[size + 1];
            commands[0] = RedisCommand.M_GET;
            System.arraycopy(keyList.toArray(new String[0]), 0, commands, 1, size);
            String[] values = sendCommands(commands);
            int index = 0;
            for (String key : keyList){
                map.put(key, values[index++]);
            }
        }
        keysMap = null;
        String[] result = new String[keys.length];
        int index = 0;
        for (String key : keys){
            result[index++] = map.get(key);
        }
        return result;
    }

    public String clusterKeySlot(String key){
        return sendCommands(RedisCommand.CLUSTER, RedisClusterCommand.KEY_SLOT, key);
    }

    public List<RedisClusterNodeInfo> clusterNodes(){
        String response = sendCommands(RedisCommand.CLUSTER, RedisClusterCommand.CLUSTER_NODES);
        return RedisClusterDecoder.clusterNodesDecoder(response);
    }

    public String[] clusterSlots(){
        return sendCommands(RedisCommand.CLUSTER, RedisClusterCommand.CLUSTER_SLOTS);
    }

    private static class RedisClusterCommand{
        //集群信息
        public static final String CLUSTER_INFO  = "INFO";
        //集群节点
        public static final String CLUSTER_NODES  = "NODES";
        //返回有关哪些集群插槽映射到哪些 Redis 实例的详细信息
        public static final String CLUSTER_SLOTS  = "SLOTS";

        public static final String KEY_SLOT = "KEYSLOT";
    }
}
