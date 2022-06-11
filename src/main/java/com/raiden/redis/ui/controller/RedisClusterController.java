package com.raiden.redis.ui.controller;

import com.raiden.redis.net.client.RedisClusterClient;
import com.raiden.redis.net.model.RedisClusterNodeInfo;
import com.raiden.redis.ui.DataPageView;
import com.raiden.redis.ui.common.Path;
import com.raiden.redis.ui.context.BeanContext;
import com.raiden.redis.ui.dao.RecordDao;
import com.raiden.redis.ui.mode.RedisClusterNode;
import com.raiden.redis.ui.mode.RedisNode;
import com.raiden.redis.ui.tab.ClusterRedisInfoTabPane;
import com.raiden.redis.ui.util.RedisUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 8:57 2022/5/14
 * @Modified By:
 */
public class RedisClusterController extends AbstractRedisController {

    private static final Logger LOGGER = LogManager.getLogger(RedisClusterController.class);
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
        RedisLoginController redisController = BeanContext.getBean(RedisLoginController.class.getName());
        if (StringUtils.isNoneBlank(host, port)){
            try {
                RedisClusterClient redisClient = RedisUtils.getRedisClusterClient(host.trim(), Integer.parseInt(port.trim()));
                boolean isAuth;
                final String password = getPassword();
                if ((isAuth = isVerification.isSelected())){
                    //验证不成功停止执行
                    if (!verification(redisClient)){
                        return;
                    }
                }
                //清理旧的数据
                redisController.clear();
                List<RedisClusterNodeInfo> redisClusterNodes = redisClient.clusterNodes();
                ClusterRedisInfoTabPane redisInfoTabPane = new ClusterRedisInfoTabPane();
                List<RedisNode> hosts = redisClusterNodes.stream()
                        .sorted()
                        .map(cluster -> {
                            RedisClusterNode clusterNode = RedisClusterNode.build(cluster);
                            if (isAuth){
                                clusterNode.setPassword(password);
                            }
                            return clusterNode;
                        })
                        .collect(Collectors.toList());
                Pane pane = redisInfoTabPane.setRedisInfoTabPane(hosts);
                //设置关闭回调
                redisController.setShutDownCallback(() -> redisInfoTabPane.shutDown());
                DataPageView dataPageView = new DataPageView(pane, () -> showLoginView());
                dataPageView.start();
                //关闭登录界面
                closeLoginView();
            }catch (Exception e){
                LOGGER.error(e.getMessage(), e);
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
