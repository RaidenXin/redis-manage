package com.raiden.redis.ui.controller;

import com.raiden.redis.client.RedisClient;
import com.raiden.redis.model.RedisKeyspace;
import com.raiden.redis.model.RedisNodeInfo;
import com.raiden.redis.ui.Window;
import com.raiden.redis.ui.mode.RedisNode;
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
public class RedisClusterTabController{

    private static final Logger LOGGER = LogManager.getLogger(RedisClusterTabController.class);


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
     * 切换成监控视图
     */
    public void switchingMonitoringViews(){
        if (monitoringView == null){
            try {
                //初始化监控视图
                FXMLLoader loader = new FXMLLoader(Window.class.getResource("redis_monitoring_info_view.fxml"));
                monitoringView = loader.load();
                RedisMonitoringInfoController controller = loader.getController();
                controller.init(redisNode);
            }catch (Exception e){
                LOGGER.error(e.getMessage(), e);
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
                //初始化侧边栏
                RedisClient redisClient = redisNode.getRedisClient();
                RedisNodeInfo info = redisClient.info();
                RedisKeyspace keyspace = info.getKeyspace();
                if (keyspace != null){
                    List<RedisKeyspace.RedisDB> dbs = keyspace.getDb();
                    if (dbs != null){
                        dbs.stream().sorted(Comparator.comparing(RedisKeyspace.RedisDB::getName))
                                .forEach(db -> {
                                    ObservableList items = sidebar.getItems();
                                    Button button = new Button(db.getName());
                                    button.setPrefWidth(133.0);
                                    button.setGraphic(new ImageView("/icon/db.jpg"));
                                    button.setOnAction((event) -> {
                                        //这里设置View并加载数据 懒加载
                                        switchingDataViews();
                                    });
                                    items.add(button);
                                });
                    }
                }
            }catch (Exception e){
                LOGGER.error("初始化数据页面失败！", e);
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
            }
        }
    }

    public boolean isInitTab() {
        return isInitTab.get();
    }
}
