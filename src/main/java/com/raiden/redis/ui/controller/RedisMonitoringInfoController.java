package com.raiden.redis.ui.controller;

import com.raiden.redis.core.common.TaskProcessingCenter;
import com.raiden.redis.core.task.Task;
import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.net.model.RedisNodeInfo;
import com.raiden.redis.ui.Window;
import com.raiden.redis.ui.mode.RedisNode;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 19:20 2022/5/18
 * @Modified By:
 */
public class RedisMonitoringInfoController {

    private static final Logger LOGGER = LogManager.getLogger(Window.class);

    private static final String SERVER = "server";
    private static final String STATS = "stats";
    private static final String MEMORY = "memory";
    private static final String PERSISTENCE = "persistence";

    @FXML
    private TabPane tabPane;

    private RedisNode redisNode;

    private RedisServerInfoController redisServerInfoController;
    private RedisStatsInfoController redisStatsInfoController;

    private AtomicBoolean isInit;

    public RedisMonitoringInfoController(){
        this.isInit = new AtomicBoolean(false);
    }


    public void init(RedisNode redisNode){
        if (redisNode == null){
            return;
        }
        this.redisNode = redisNode;
        try {
            refreshTabPane();
            isInit.compareAndSet(false, true);
            TaskProcessingCenter.submit(new RedisMonitoringInfoController.RefreshTask(), 5, TimeUnit.SECONDS);
        }catch (Exception e){
            String error = e.getMessage();
            LOGGER.error(error, e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "加载Redis服务信息错误:" + error);
            alert.showAndWait();
        }
    }

    public void refreshTabPane() throws IOException {
        if (tabPane == null || redisNode == null){
            return;
        }
        List<Tab> tabs = tabPane.getTabs();
        if (tabs != null){
            RedisClient client = redisNode.getRedisClient();
            RedisNodeInfo info = client.info();
            for (Tab tab : tabs){
                switch (tab.getId()){
                    case SERVER://服务
                        refreshRedisServerInfo(tab, info);
                        break;
                    case STATS://统计数据
                        refreshRedisStatsInfo(tab, info);
                        break;
                    case MEMORY://内存
//                        refreshRedisMemoryInfo(tab, info);
                        break;
                    case PERSISTENCE://持久化
//                        refreshRedisPersistenceInfo(tab, info);
                        break;
                }
            }
        }
    }

    public void refreshRedisServerInfo(Tab server,RedisNodeInfo info) throws IOException {
        if (!isInit.get()){
            FXMLLoader loader = new FXMLLoader(Window.class.getResource("redis_server_info_view.fxml"));
            AnchorPane redisServerInfoView = loader.load();
            server.setContent(redisServerInfoView);
            this.redisServerInfoController = loader.getController();
        }
        if (redisServerInfoController != null){
            redisServerInfoController.refresh(info);
        }
    }

    public void refreshRedisStatsInfo(Tab stats, RedisNodeInfo info)throws IOException{
        if (!isInit.get()){
            FXMLLoader loader = new FXMLLoader(Window.class.getResource("redis_stats_info_view.fxml"));
            AnchorPane redisStatsInfoView = loader.load();
            this.redisStatsInfoController = loader.getController();
            ScrollPane scrollPane = (ScrollPane) stats.getContent();
            scrollPane.setContent(redisStatsInfoView);
        }
        if (redisStatsInfoController != null){
            redisStatsInfoController.refresh(info);
        }
    }
    public void refreshRedisMemoryInfo(Tab memory,RedisNodeInfo info){
        if (redisNode == null || redisServerInfoController == null){
            return;
        }
        redisServerInfoController.refresh(info);
    }
    public void refreshRedisPersistenceInfo(Tab persistence,RedisNodeInfo info){
        if (redisNode == null || redisServerInfoController == null){
            return;
        }
        redisServerInfoController.refresh(info);
    }

    public class RefreshTask implements Task {

        @Override
        public void run() {
            Platform.runLater(() -> {
                try {
                    RedisMonitoringInfoController.this.refreshTabPane();
                } catch (IOException e) {
                    String error = e.getMessage();
                    LOGGER.error(error, e);
                    Alert alert = new Alert(Alert.AlertType.ERROR, "加载Redis服务信息错误:" + error);
                    alert.showAndWait();
                }
            });
            TaskProcessingCenter.submit(new RefreshTask(), 5, TimeUnit.SECONDS);
        }
    }
}
