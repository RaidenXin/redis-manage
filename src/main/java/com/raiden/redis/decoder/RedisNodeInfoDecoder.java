package com.raiden.redis.decoder;

import com.raiden.redis.common.Separator;
import com.raiden.redis.model.*;
import com.raiden.redis.ui.Window;
import com.raiden.redis.utils.GeneralDataTypeConversionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:07 2022/5/16
 * @Modified By:
 */
public final class RedisNodeInfoDecoder {

    public static final Logger LOGGER = LogManager.getLogger(Window.class);

    public static final RedisNodeInfo decoder(String[] datum){
        RedisNodeInfo redisNodeInfo = new RedisNodeInfo();
        if (datum == null || datum.length == 0){
            return redisNodeInfo;
        }
        Class<RedisNodeInfo> redisNodeInfoClass = RedisNodeInfo.class;
        Map<String, String> dataMap = new HashMap<>();
        for (int i = datum.length - 1;i > -1;i--){
            String data = datum[i];
            if (data.startsWith("# ")){
                data = data.substring(2).toLowerCase();
                try {
                    Field field = redisNodeInfoClass.getDeclaredField(data);
                    field.setAccessible(true);
                    field.set(redisNodeInfo, build(field.getType(), dataMap));
                    dataMap = new HashMap<>();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    continue;
                }
            }else {
                String[] split = StringUtils.split(data, Separator.COLON);
                if (split.length == 2){
                    dataMap.put(lineToHump(split[0]), split[1]);
                }
            }
        }
        return redisNodeInfo;
    }


    public static String lineToHump(String str) {
        if (StringUtils.isBlank(str)){
            return StringUtils.EMPTY;
        }
        StringBuilder builder = new StringBuilder();
        char[] chars = str.toCharArray();
        boolean sign = false;
        for (char c : chars){
            if (c == 95){
                sign = true;
                continue;
            }
            if (sign){
                if (c > 64 && c < 91){
                    builder.append(c);
                }else {
                    builder.append((char) (c - 32));
                }
                sign = false;
            }else {
                builder.append(c);
            }
        }
        return builder.toString();
    }


    private static final <T> T build(Class<T> clazz, Map<String, String> data){
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
