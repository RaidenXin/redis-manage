package com.raiden.redis.cluster;

import com.raiden.redis.common.Separator;
import com.raiden.redis.model.RedisClusterNode;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 16:20 2022/5/8
 * @Modified By:
 */
public final class RedisClusterDecoder {

    public static List<RedisClusterNode> clusterNodesDecoder(String response){
        if (StringUtils.isBlank(response)){
            return Collections.emptyList();
        }else {
            String[] lines = StringUtils.split(response, Separator.LINE_BREAK);
            if (lines.length > 0){
                return Stream.of(lines).map(line -> {
                    if (StringUtils.isBlank(line)){
                        return null;
                    }
                    String[] data = StringUtils.split(line, Separator.BLANK);
                    return RedisClusterNode.build(data);
                }).filter(Objects::nonNull).collect(Collectors.toList());
            }else {
                return Collections.emptyList();
            }
        }
    }
}
