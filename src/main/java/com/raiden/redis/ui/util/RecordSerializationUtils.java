package com.raiden.redis.ui.util;

import com.raiden.redis.common.Separator;
import com.raiden.redis.ui.mode.Record;
import org.apache.commons.lang3.StringUtils;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 10:02 2022/5/15
 * @Modified By:
 */
public final class RecordSerializationUtils {
    /**
     * 一条信息的标准长度 三百个字符串的长度
     */
    private static final int FULL_LENGTH = 300;

    /**
     * 一条信息的数据长度
     */
    private static final int DATA_LENGTH = 297;

    private static final String ZERO = "0";

    private static final String PLACEHOLDER = "*";

    public static final String encode(Record record){
        String data = record.toString();
        int dataSize = data.length();
        if (dataSize > DATA_LENGTH){
            throw new IllegalArgumentException("存储数据过长！");
        }
        String lengthStr = StringUtils.leftPad(String.valueOf(dataSize + 3), 3, ZERO);
        return lengthStr + StringUtils.rightPad(data, DATA_LENGTH, PLACEHOLDER) + Separator.LINE_BREAK;
    }


    public static final Record decoder(String data){
        if (StringUtils.isBlank(data) || data.length() != FULL_LENGTH){
            throw new IllegalArgumentException("数据长度问题无法解析！");
        }
        int dataSize = Integer.parseInt(data.substring(0, 3));
        Record record = Record.build(data.substring(3, dataSize));
        return record;
    }
}
