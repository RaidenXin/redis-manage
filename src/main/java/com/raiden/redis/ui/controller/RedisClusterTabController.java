package com.raiden.redis.ui.controller;

import com.raiden.redis.ui.Window;
import com.raiden.redis.ui.mode.RedisNode;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 0:14 2022/5/14
 * @Modified By:
 */
public class RedisClusterTabController implements Initializable {

    @FXML
    private ListView sidebar;
    @FXML
    private HBox hBox;

    private RedisNode redisNode;
    private AtomicBoolean isInitTab;
    private AnchorPane dataView;
    private AnchorPane monitoringView;

    public RedisClusterTabController(){
        this.isInitTab = new AtomicBoolean(false);
    }

    public void setRedisNode(RedisNode redisNode){
        this.redisNode = redisNode;
    }

    public void setDataVeiw(AnchorPane pane){
        hBox.getChildren().set(1, pane);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //初始化侧边栏
    }

    /**
     * 切换成数据视图
     */
    public void switchingDataViews(){
        if (dataView == null){
            try {
                //初始化数据视图
                FXMLLoader loader = new FXMLLoader(Window.class.getResource("redis_cluster_data_table_view.fxml"));
                dataView = loader.load();
                RedisClusterDataTableController dataTableController = loader.getController();
                dataTableController.setRedisNode(redisNode);
                dataTableController.initTable();
            }catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
            }
        }
        //设置数据视图
        ObservableList<Node> children = hBox.getChildren();
        if (children.size() > 1){
            children.set(1, dataView);
        }else {
            children.add(dataView);
        }
    }

    /**
     * 切换成数据视图
     */
    public void switchingMonitoringViews(){
        if (monitoringView == null){
            try {
                //初始化监控视图
                FXMLLoader loader = new FXMLLoader(Window.class.getResource("redis_server_info_view.fxml"));
                monitoringView = loader.load();
            }catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
            }
        }
        //设置监控视图
        ObservableList<Node> children = hBox.getChildren();
        if (children.size() > 1){
            children.set(1, monitoringView);
        }else {
            children.add(monitoringView);
        }
    }

    public void initTable() {
        if (isInitTab.compareAndSet(false, true)){
            try {
                FXMLLoader loader = new FXMLLoader(Window.class.getResource("redis_cluster_data_table_view.fxml"));
                dataView = loader.load();
                //设置数据视图
                hBox.getChildren().add(dataView);
                RedisClusterDataTableController dataTableController = loader.getController();
                dataTableController.setRedisNode(redisNode);
                dataTableController.initTable();
            }catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
            }
        }
    }

    public boolean isInitTab() {
        return isInitTab.get();
    }
}
