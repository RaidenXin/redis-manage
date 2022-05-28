package com.raiden.redis.net.decoder;

import com.raiden.redis.net.common.Separator;
import com.raiden.redis.net.model.*;
import com.raiden.redis.ui.Window;
import com.raiden.redis.net.utils.GeneralDataTypeConversionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

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
                    field.set(redisNodeInfo, build(field.getType(), dataMap));
                    dataMap = new HashMap<>();
                } catch (Exception e) {
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


    private static final <T> T build(Class<T> clazz, Map<String, Object> data){
        try {
            T t = clazz.newInstance();
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields){
                field.setAccessible(true);
                String name = field.getName();
                Object value = data.get(name);
                if (value != null){
                    Class<?> curFieldType = field.getType();;
                    if (value instanceof List){
                        //如果字段不是List接口类型 就放弃赋值
                        if (curFieldType != List.class){
                            continue;
                        }
                        //获取字段的泛型没有获取到则放弃赋值
                        Type genericType = field.getGenericType();
                        if (genericType == null){
                            continue;
                        }
                        if (genericType instanceof ParameterizedType) {
                            ParameterizedType pt = (ParameterizedType) genericType;
                            Class<?> actualTypeArgument = (Class<?>)pt.getActualTypeArguments()[0];
                            value = ((List) value).stream().map(v -> build(actualTypeArgument, (Map<String, Object>) v)).collect(Collectors.toList());
                            field.set(t, value);
                        }
                    }else {
                        field.set(t, GeneralDataTypeConversionUtils.conversion(curFieldType, value));
                    }
                }
            }
            return t;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }
}
