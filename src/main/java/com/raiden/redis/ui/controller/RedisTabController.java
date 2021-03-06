package com.raiden.redis.ui.controller;

import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.net.model.RedisKeyspace;
import com.raiden.redis.net.model.RedisNodeInfo;
import com.raiden.redis.ui.mode.RedisNode;
import com.raiden.redis.ui.util.FXMLLoaderUtils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 0:14 2022/5/14
 * @Modified By:
 */
public class RedisTabController {

    private static final Logger LOGGER = LogManager.getLogger(RedisTabController.class);


    @FXML
    private ListView sidebar;
    @FXML
    private HBox hBox;

    private RedisNode redisNode;
    private AtomicBoolean isInitTab;
    private AnchorPane dataView;
    private AnchorPane monitoringView;
    private RedisMonitoringInfoController monitoringInfoController;

    public RedisTabController(){
        this.isInitTab = new AtomicBoolean(false);
    }

    public void setRedisNode(RedisNode redisNode){
        this.redisNode = redisNode;
    }

    public void setDataVeiw(AnchorPane pane){
        hBox.getChildren().set(1, pane);
    }

    /**
     * 切换成数据视图
     */
    public void switchingDataViews(String dbIndex){
        try {
            RedisClient redisClient = redisNode.getRedisClient();
            redisClient.select(dbIndex);
            //初始化数据视图
            FXMLLoader loader = FXMLLoaderUtils.getFXMLLoader("redis_data_table_view.fxml");
            dataView = loader.load();
            RedisDataTableController dataTableController = loader.getController();
            dataTableController.setRedisNode(redisNode);
            dataTableController.initTable();
            //设置数据视图
            ObservableList<Node> children = hBox.getChildren();
            //如果存在视图就覆盖 注意0号位置是列表框
            if (children.size() > 1){
                children.set(1, dataView);
            }else {
                children.add(dataView);
            }
        }catch (Exception e){
            LOGGER.error(e);
            LOGGER.error(e.getMessage(), e);
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * 切换成监控视图
     */
    public void switchingMonitoringViews(){
        if (monitoringView == null){
            try {
                //初始化监控视图
                FXMLLoader loader = FXMLLoaderUtils.getFXMLLoader("redis_monitoring_info_view.fxml");
                monitoringView = loader.load();
                RedisMonitoringInfoController controller = loader.getController();
                controller.init(redisNode);
                this.monitoringInfoController = controller;
            }catch (Exception e){
                LOGGER.error(e);
                LOGGER.error(e.getMessage(), e);
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
            }
        }
        //设置监控视图
        ObservableList<Node> children = hBox.getChildren();
        //如果存在视图就覆盖 注意0号位置是列表框
        if (children.size() > 1){
            children.set(1, monitoringView);
        }else {
            children.add(monitoringView);
        }
    }

    public void initTable() {
        if (isInitTab.compareAndSet(false, true)){
            try {
                //初始化侧边栏
                RedisClient redisClient = redisNode.getRedisClient();
                RedisNodeInfo info = redisClient.info();
                RedisKeyspace keyspace = info.getKeyspace();
                List<RedisKeyspace.RedisDB> dbs;
                if (keyspace != null && (dbs = keyspace.getDb()) != null){
                    dbs.stream().sorted(Comparator.comparing(db -> Integer.parseInt(db.getIndex())))
                            .forEach(db -> {
                                ObservableList<Button> items = sidebar.getItems();
                                Button button = new Button(db.getName());
                                button.setPrefWidth(133.0);
                                button.setGraphic(new ImageView("/icon/db.jpg"));
                                button.setOnAction((event) -> {
                                    //这里设置View并加载数据 懒加载
                                    switchingDataViews(db.getIndex());
                                });
                                items.add(button);
                            });
                }
            }catch (Exception e){
                LOGGER.error("初始化数据页面失败！{}", e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
            }
        }
    }

    /**
     * 关闭方法
     */
    public void shutDown(){
        try {
            if (this.monitoringInfoController != null){
                this.monitoringInfoController.shutDown();
            }
        }finally {
            //清理监控页面
            this.monitoringView = null;
            if (hBox != null){
                //清理正在展示的页面
                ObservableList<Node> children = hBox.getChildren();
                //如果存在视图就覆盖 注意0号位置是列表框
                if (children.size() > 1){
                    children.remove(1);
                }
            }
        }
    }

    public boolean isInitTab() {
        return isInitTab.get();
    }
}
