package com.raiden.redis.ui.controller;

import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.net.client.RedisSingleClient;
import com.raiden.redis.ui.common.AlertText;
import com.raiden.redis.ui.tab.SingleRedisInfoTabPane;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 8:56 2022/5/14
 * @Modified By:
 */
public class RedisSinglePointController implements Initializable {


    @FXML
    private TextField singlePointHost;
    @FXML
    private TextField singlePointPort;
    @FXML
    private CheckBox isVerification;
    @FXML
    private Label passwordText;
    @FXML
    private PasswordField password;

    public void connectionRedis(){
        String host = singlePointHost.getText();
        String port = singlePointPort.getText();
        RedisController redisController = BeanContext.getBean(RedisController.class.getName());
        if (StringUtils.isNoneBlank(host, port)){
            try {
                RedisSingleClient redisClient = new RedisSingleClient(host.trim(),Integer.parseInt(port.trim()));
                if (isVerification.isSelected()){
                    //验证不成功停止执行
                    if (!verification(redisClient)){
                        return;
                    }
                }
                SingleRedisInfoTabPane redisInfoTabPane = new SingleRedisInfoTabPane();
                redisInfoTabPane.setRedisInfoTabPane(redisController.getRedisDataPage(), null);
                redisClient.close();
            }catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
            }
        }
    }

    private boolean verification(RedisClient redisClient){
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

    public void addRecord(){

    }


    public void clearInputField(){

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
