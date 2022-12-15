package com.raiden.redis.net.utils;

/**
 * @author 疆戟
 * @date 2022/12/15 19:11
 */
public final class ArrUtil {

    public static boolean isEmpty(Object[] arr) {
        return arr == null || arr.length == 0;
    }

    public static boolean isNotEmpty(Object[] arr) {
        return !isEmpty(arr);
    }
}
