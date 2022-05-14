package com.raiden.redis.ui.controller;

import com.raiden.redis.client.RedisClusterClient;
import com.raiden.redis.model.RedisClusterNodeInfo;
import com.raiden.redis.ui.common.AlertText;
import com.raiden.redis.ui.mode.RedisClusterNode;
import com.raiden.redis.ui.mode.RedisNode;
import com.raiden.redis.ui.tab.ClusterRedisInfoTabPane;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 8:57 2022/5/14
 * @Modified By:
 */
public class ReidsClusterController implements Initializable {

    @FXML
    private TextField clusterHost;
    @FXML
    private TextField clusterPort;
    @FXML
    private CheckBox isVerification;
    @FXML
    private Label passwordText;
    @FXML
    private PasswordField password;

    public void connectionRedisCluster(){
        String host = clusterHost.getText();
        String port = clusterPort.getText();
        RedisController redisController = BeanContext.getBean(RedisController.class.getName());
        if (StringUtils.isNoneBlank(host, port)){
            try {
                RedisClusterClient redisClient = new RedisClusterClient(host.trim(),Integer.parseInt(port.trim()));
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
                redisClient.close();
            }catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
            }
        }
    }

    private boolean verification(RedisClusterClient redisClient){
        String pd = password.getText();
        if (StringUtils.isNotBlank(pd)){
            boolean auth = redisClient.auth(pd.trim());
            if (!auth){
                Alert alert = new Alert(Alert.AlertType.ERROR, AlertText.PLEASE_FILL_IN_THE_CORRECT_PASSWORD);
                alert.showAndWait();
            }
            return auth;
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR, AlertText.PLEASE_FILL_IN_THE_CORRECT_PASSWORD);
            alert.showAndWait();
            return false;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isVerification.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            passwordText.setVisible(newValue);
            password.setVisible(newValue);
        });
        BeanContext.setBean(this.getClass().getName(), this);
    }
}
