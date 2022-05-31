package com.raiden.redis.ui.controller;

import com.raiden.redis.net.client.RedisClusterClient;
import com.raiden.redis.net.model.RedisClusterNodeInfo;
import com.raiden.redis.ui.common.Path;
import com.raiden.redis.ui.context.BeanContext;
import com.raiden.redis.ui.dao.RecordDao;
import com.raiden.redis.ui.mode.RedisClusterNode;
import com.raiden.redis.ui.mode.RedisNode;
import com.raiden.redis.ui.tab.ClusterRedisInfoTabPane;
import com.raiden.redis.ui.util.RedisUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 8:57 2022/5/14
 * @Modified By:
 */
public class RedisClusterController extends AbstractRedisController {

    @FXML
    private TextField clusterHost;
    @FXML
    private TextField clusterPort;

    public RedisClusterController(){
        super(new RecordDao(Path.REDIS_CLUSTER_HISTORICAL_RECORD_DATA_PATH));
    }

    public void connectionRedisCluster(){
        String host = clusterHost.getText();
        String port = clusterPort.getText();
        RedisDataPageController redisController = BeanContext.getBean(RedisDataPageController.class.getName());
        if (StringUtils.isNoneBlank(host, port)){
            try {
                RedisClusterClient redisClient = RedisUtils.getRedisClusterClient(host.trim(), Integer.parseInt(port.trim()));
                if (isVerification.isSelected()){
                    //验证不成功停止执行
                    if (!verification(redisClient)){
                        return;
                    }
                }
                List<RedisClusterNodeInfo> redisClusterNodes = redisClient.clusterNodes();
                ClusterRedisInfoTabPane redisInfoTabPane = new ClusterRedisInfoTabPane();
                List<RedisNode> hosts = redisClusterNodes.stream()
                        .sorted()
                        .map(RedisClusterNode::build)
                        .collect(Collectors.toList());
                redisInfoTabPane.setRedisInfoTabPane(redisController.getRedisDataPage(), hosts);
            }catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @Override
    protected String getHost() {
        return clusterHost.getText();
    }

    @Override
    protected String getPort() {
        return clusterPort.getText();
    }

    @Override
    protected void setHost(String host) {
        this.clusterHost.setText(host);
    }

    @Override
    protected void setPort(String port) {
        this.clusterPort.setText(port);
    }
}
