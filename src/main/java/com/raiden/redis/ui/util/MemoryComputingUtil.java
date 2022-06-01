package com.raiden.redis.ui.util;

import com.raiden.redis.net.exception.RedisException;
import org.apache.commons.lang3.StringUtils;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 23:35 2022/5/31
 * @Modified By: 内存计算器
 */
public final class MemoryComputingUtil {

    private static final int STANDARD_LENGTH = 10;
    private static final String TB = "TB";
    private static final String GB = "GB";
    private static final String KB = "KB";
    private static final String B = "B";

    /**
     * 获取可以阅读的内存
     * @param b
     * @return
     */
    public static String getReadableMemory(long b){
        int index = 0;
        while (b > 1024){
            b >>= STANDARD_LENGTH;
            index++;
        }
        switch (index){
            case 0: return b + B;
            case 1: return b + KB;
            case 2: return b + GB;
            case 3: return b + TB;
        }
        return StringUtils.EMPTY;
    }

    public static long byteToKB(long size){
        return size >> STANDARD_LENGTH;
    }

    public static long byteToMB(long size){
        return byteToKB(size) >> STANDARD_LENGTH;
    }

    public static long byteToGB(long size){
        return byteToMB(size) >> STANDARD_LENGTH;
    }
}
