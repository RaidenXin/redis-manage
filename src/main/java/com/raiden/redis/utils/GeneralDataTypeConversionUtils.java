package com.raiden.redis.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:12 2022/5/16
 * @Modified By: 常规数据类型转换工具
 */
public final class GeneralDataTypeConversionUtils {

    private static final Map<Class, Function> FUNCTION_MAP = new HashMap<>();

    static {
        Function<String, Long> parseLong = Long::parseLong;
        FUNCTION_MAP.put(long.class, parseLong);
        Function<String, Long> valueOf = Long::valueOf;
        FUNCTION_MAP.put(Long.class, valueOf);
        Function<String, Integer> parseInt = Integer::parseInt;
        FUNCTION_MAP.put(int.class, parseInt);
        Function<String, Integer> integerValueOf = Integer::valueOf;
        FUNCTION_MAP.put(Integer.class, integerValueOf);
        Function<String, Double> parseDouble = Double::parseDouble;
        FUNCTION_MAP.put(double.class, parseDouble);
        Function<String, Double> dubboValueOf = Double::valueOf;
        FUNCTION_MAP.put(Double.class, dubboValueOf);
    }

    public static <T> T conversion(Class<T> clazz,String value){
        Function function = FUNCTION_MAP.get(clazz);
        if (function != null){
            return (T) function.apply(value);
        }
        return (T) value;
    }
}
