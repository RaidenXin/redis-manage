package com.raiden.redis.net.decoder;

import com.raiden.redis.net.utils.GeneralDataTypeConversionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 13:58 2022/5/29
 * @Modified By:
 */
public final class DecoderUtils {

    private static final Logger LOGGER = LogManager.getLogger(DecoderUtils.class);


    public static String lineToHump(String str,char separator) {
        if (StringUtils.isBlank(str)){
            return StringUtils.EMPTY;
        }
        StringBuilder builder = new StringBuilder();
        char[] chars = str.toCharArray();
        boolean sign = false;
        for (char c : chars){
            if (c == separator){
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

    public static final <T> T build(Class<T> clazz, Map<String, Object> data){
        try {
            T t = clazz.newInstance();
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields){
                field.setAccessible(true);
                String name = field.getName();
                Object value = data.get(name);
                Class<?> curFieldType = field.getType();
                if (value != null){
                    if (curFieldType.isInstance(value) && (value instanceof Collection || curFieldType.isArray())){
                        //获取字段的泛型没有获取到则放弃赋值
                        Type genericType = field.getGenericType();
                        if (genericType == null){
                            continue;
                        }
                        if (genericType instanceof ParameterizedType) {
                            ParameterizedType pt = (ParameterizedType) genericType;
                            Class<?> actualTypeArgument = (Class<?>)pt.getActualTypeArguments()[0];
                            Stream stream = ((Collection) value).stream().map(v -> build(actualTypeArgument, (Map<String, Object>) v));
                            if (curFieldType == List.class){
                                value = stream.collect(Collectors.toList());
                            }else if (curFieldType == Set.class){
                                value = stream.collect(Collectors.toList());
                            }else if (curFieldType.isArray()){
                                value = stream.toArray();
                            }
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
