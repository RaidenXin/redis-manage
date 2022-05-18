package com.raiden.redis.ui.controller;

import com.raiden.redis.ui.Window;
import com.raiden.redis.ui.mode.RedisNode;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 19:20 2022/5/18
 * @Modified By:
 */
public class RedisMonitoringInfoController {

    private static final Logger LOGGER = LogManager.getLogger(Window.class);

    private static final String SERVER = "server";
    private static final String CLIENT = "client";
    private static final String MEMORY = "memory";
    private static final String PERSISTENCE = "persistence";

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab server;
    @FXML
    private Tab client;
    @FXML
    private Tab memory;
    @FXML
    private Tab persistence;

    private RedisNode redisNode;

    private RedisServerInfoController redisServerInfoController;


    public void init(RedisNode redisNode){
        if (redisNode == null){
            return;
        }
        this.redisNode = redisNode;
        try {
            FXMLLoader loader = new FXMLLoader(Window.class.getResource("redis_server_info_view.fxml"));
            AnchorPane redisServerInfoView = loader.load();
            this.redisServerInfoController = loader.getController();
            redisServerInfoController.init(redisNode);
            server.setContent(redisServerInfoView);
            //设置点击监听事件 切换新页面在刷新新页面的数据
            tabPane.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) -> {
                switch (newValue.getId()){
                    case SERVER://服务
                        refreshRedisServerInfo();
                        break;
                    case CLIENT://客户端
                        refreshRedisClientInfo();
                        break;
                    case MEMORY://内存
                        refreshRedisMemoryInfo();
                        break;
                    case PERSISTENCE://持久化
                        refreshRedisPersistenceInfo();
                        break;
                }
            });
        }catch (Exception e){
            String error = e.getMessage();
            LOGGER.error(error, e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "加载Redis服务信息错误:" + error);
            alert.showAndWait();
        }
    }

    public void refreshRedisServerInfo(){
        if (redisNode == null || redisServerInfoController == null){
            return;
        }
        redisServerInfoController.refresh(redisNode.getRedisClient());
    }

    public void refreshRedisClientInfo(){
        if (redisNode == null || redisServerInfoController == null){
            return;
        }
        redisServerInfoController.refresh(redisNode.getRedisClient());
    }
    public void refreshRedisMemoryInfo(){
        if (redisNode == null || redisServerInfoController == null){
            return;
        }
        redisServerInfoController.refresh(redisNode.getRedisClient());
    }
    public void refreshRedisPersistenceInfo(){
        if (redisNode == null || redisServerInfoController == null){
            return;
        }
        redisServerInfoController.refresh(redisNode.getRedisClient());
    }
}
