package com.raiden.redis.ui.tab;

import com.raiden.redis.ui.Window;
import com.raiden.redis.ui.controller.RedisTabController;
import com.raiden.redis.ui.mode.RedisNode;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:04 2022/5/11
 * @Modified By:
 */
public class SentinelRedisInfoTabPane {

    private static final Logger LOGGER = LogManager.getLogger(SentinelRedisInfoTabPane.class);

    //初始化缓存 用来记录 已经初始化的 Tab
    private volatile Map<String, RedisTabController> initCache;

    public SentinelRedisInfoTabPane(){
        this.initCache = new ConcurrentHashMap<>();
    }

    public void setRedisInfoTabPane(Pane root, List<RedisNode> hosts) {
        ObservableList<Node> children = root.getChildren();
        //清理过去的东西
        children.clear();
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
                    FXMLLoader fxmlLoader = new FXMLLoader(Window.class.getResource("redis_tab_view.fxml"));
                    AnchorPane anchorPane = fxmlLoader.load();
                    tab.setContent(anchorPane);
                    RedisTabController controller = fxmlLoader.getController();
                    controller.setRedisNode(host);
                    initCache.put(hostAndPort, controller);
                    if (host.isMyself()){
                        controller.initTable();
                    }
                    return tab;
                } catch (IOException e) {
                    LOGGER.error(e);
                    LOGGER.error(e.getMessage(), e);
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
            tabPane.getTabs().addAll(tabs);
            //设置点击监听事件 切换新页面在刷新新页面的数据
            tabPane.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) -> {
                String oldOstAndPort = oldValue.getText();
                RedisTabController oldController = initCache.get(oldOstAndPort);
                //关闭之前的控制器
                oldController.shutDown();
                String hostAndPort = newValue.getText();
                RedisTabController newController = initCache.get(hostAndPort);
                //初始化新的控制器
                if (newController != null && !newController.isInitTab()){
                    newController.initTable();
                }
            });
        }
        children.add(tabPane);
    }
}
