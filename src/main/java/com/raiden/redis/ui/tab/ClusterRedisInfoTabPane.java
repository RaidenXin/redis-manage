package com.raiden.redis.ui.tab;

import com.raiden.redis.ui.Window;
import com.raiden.redis.ui.controller.RedisClusterDataTableController;
import com.raiden.redis.ui.controller.RedisClusterTabController;
import com.raiden.redis.ui.mode.RedisNode;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:04 2022/5/11
 * @Modified By:
 */
public class ClusterRedisInfoTabPane {

    private static final Logger LOGGER = LogManager.getLogger(ClusterRedisInfoTabPane.class);

    //初始化缓存 用来记录 已经初始化的 Tab
    private volatile Map<String, RedisClusterTabController> initCache;

    public ClusterRedisInfoTabPane(){
        this.initCache = new ConcurrentHashMap<>();
    }

    public void setRedisInfoTabPane(Pane root, List<RedisNode> hosts) {
        ObservableList<Node> children = root.getChildren();
        //清理过去的东西
        children.clear();;
        double prefHeight = root.getPrefHeight();
        double prefWidth = root.getPrefWidth();
        TabPane tabPane = new TabPane();
        tabPane.setPrefHeight(prefHeight);
        tabPane.setPrefWidth(prefWidth);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        //初始化FXML布局文件内容
        if (hosts != null) {
            List<Tab> tabs = hosts.stream().map(host -> {
                String hostAndPort = host.getHostAndPort();
                try {
                    Tab tab = new Tab();
                    tab.setText(host.getHostAndPort());
                    tab.setGraphic(new ImageView("/icon/redis2.jpg"));
                    FXMLLoader fxmlLoader = new FXMLLoader(Window.class.getResource("redis_cluster_tab_view.fxml"));
                    AnchorPane anchorPane = fxmlLoader.load();
                    tab.setContent(anchorPane);
                    RedisClusterTabController controller = fxmlLoader.getController();
                    controller.setRedisNode(host);
                    initCache.put(hostAndPort, controller);
                    if (host.isMyself()){
                        controller.initTable();
                    }
                    return tab;
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
            tabPane.getTabs().addAll(tabs);
            //设置点击监听事件 切换新页面在刷新新页面的数据
            tabPane.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) -> {
                String hostAndPort = newValue.getText();
                RedisClusterTabController controller = initCache.get(hostAndPort);
                if (controller != null && !controller.isInitTab()){
                    controller.initTable();
                }
            });
        }
        children.add(tabPane);
    }
}
