package com.raiden.redis.net.decoder;

import com.raiden.redis.net.common.Separator;
import com.raiden.redis.net.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:07 2022/5/16
 * @Modified By:
 */
public final class RedisNodeInfoDecoder {

    private static final String NAME = "name";

    private static final Logger LOGGER = LogManager.getLogger(RedisNodeInfoDecoder.class);

    public static final RedisNodeInfo decoder(String[] datum){
        RedisNodeInfo redisNodeInfo = new RedisNodeInfo();
        if (datum == null || datum.length == 0){
            return redisNodeInfo;
        }
        Class<RedisNodeInfo> redisNodeInfoClass = RedisNodeInfo.class;
        Map<String, Object> dataMap = new HashMap<>();
        for (int i = datum.length - 1;i > -1;i--){
            String data = datum[i];
            if (data.startsWith("# ")){
                data = data.substring(2).toLowerCase();
                try {
                    Field field = redisNodeInfoClass.getDeclaredField(data);
                    field.setAccessible(true);
                    field.set(redisNodeInfo, DecoderUtils.build(field.getType(), dataMap));
                    dataMap = new HashMap<>();
                }catch (NoSuchFieldException noSuch){
                    continue;
                }catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    continue;
                }
            }else {
                String[] split = StringUtils.split(data, Separator.COLON);
                if (split.length == 2){
                    String key = split[0];
                    Object value = split[1];
                    int count = countTheNumberOfTrailingDigits(key);
                    if (count > 0){
                        //截去数字
                        String name = key;
                        key = key.substring(0, key.length() - count);
                        value = dataMap.get(key);
                        List<Map> list = value instanceof List? (List) value : new ArrayList<>();
                        Map<String, Object> map = string2Map(split[1]);
                        map.put(NAME, name);
                        list.add(map);
                        dataMap.put(lineToHump(key), list);
                    }else {
                        dataMap.put(lineToHump(key), value);
                    }
                }
            }
        }
        return redisNodeInfo;
    }

    public static Map<String, Object> string2Map(String str){
        if (StringUtils.isBlank(str)){
            return Collections.emptyMap();
        }
        Map<String, Object> dataMap = new HashMap<>();
        String[] split = StringUtils.split(str, Separator.COMMA);
        for (String value : split){
            String[] arr = StringUtils.split(value, Separator.EQUALS_SIGN);
            if (arr.length == 2){
                dataMap.put(lineToHump(arr[0]), arr[1]);
            }
        }
        return dataMap;
    }

    public static int countTheNumberOfTrailingDigits(String key){
        int length = key.length();
        char c = key.charAt(length - 1);
        int index = 0;
        for (;c > 47 && c < 58;){
            index++;
            c = key.charAt(--length - 1);
        }
        return index;
    }


    public static String lineToHump(String str) {
        return DecoderUtils.lineToHump(str, (char) 95);
    }
}
