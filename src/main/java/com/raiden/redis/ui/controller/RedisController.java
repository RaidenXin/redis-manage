package com.raiden.redis.ui.controller;

import com.raiden.redis.client.RedisClusterClient;
import com.raiden.redis.model.RedisClusterNodeInfo;
import com.raiden.redis.ui.mode.RedisClusterNode;
import com.raiden.redis.ui.mode.RedisNode;
import com.raiden.redis.ui.tab.ClusterRedisInfoTabPane;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 20:26 2022/5/8
 * @Modified By:
 */
public class RedisController {

    @FXML
    private Pane redisInfo;
    @FXML
    private AnchorPane cluster;

    public void connectionRedisCluster(){
        TextField clusterHost = (TextField) cluster.lookup("#clusterHost");
        TextField clusterPort = (TextField) cluster.lookup("#clusterPort");
        String host = clusterHost.getText();
        String port = clusterPort.getText();
        if (StringUtils.isNoneBlank(host, port)){
            RedisClusterClient redisClient = new RedisClusterClient(host.trim(),Integer.parseInt(port.trim()));
            List<RedisClusterNodeInfo> redisClusterNodes = redisClient.clusterNodes();
            ClusterRedisInfoTabPane redisInfoTabPane = new ClusterRedisInfoTabPane();
            List<RedisNode> hosts = redisClusterNodes.stream()
                    .sorted()
                    .map(RedisClusterNode::build)
                    .collect(Collectors.toList());
            redisInfoTabPane.setRedisInfoTabPane(redisInfo, hosts);
            redisClient.close();
        }
    }
}
