package com.raiden.redis.ui.tab;

import com.raiden.redis.ui.Window;
import com.raiden.redis.ui.controller.RedisTabController;
import com.raiden.redis.ui.mode.RedisNode;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:52 2022/5/7
 * @Modified By:
 */
public class SingleRedisInfoTabPane implements RedisInfoTabPane{

    private static final Logger LOGGER = LogManager.getLogger(SingleRedisInfoTabPane.class);

    private RedisTabController redisTabController;

    public void setRedisInfoTabPane(Pane root, RedisNode redisNode) {
        double prefHeight = root.getPrefHeight();
        double prefWidth = root.getPrefWidth();
        ObservableList<Node> children = root.getChildren();
        children.clear();
        TabPane tabPane = new TabPane();
        tabPane.setPrefHeight(prefHeight);
        tabPane.setPrefWidth(prefWidth);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        if (redisNode != null) {
            Tab tab = new Tab();
            tab.setText(redisNode.getHostAndPort());
            tab.setGraphic(new ImageView("/icon/redis2.jpg"));
            FXMLLoader fxmlLoader = new FXMLLoader(Window.class.getResource("redis_tab_view.fxml"));
            try {
                AnchorPane anchorPane = fxmlLoader.load();
                tab.setContent(anchorPane);
                RedisTabController controller = fxmlLoader.getController();
                this.redisTabController = controller;
                controller.setRedisNode(redisNode);
                tab.setContent(anchorPane);
                controller.initTable();
                tabPane.getTabs().add(tab);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        children.add(tabPane);
    }

    @Override
    public void shutDown() {
        //关闭tab控制器
        if (redisTabController != null){
            redisTabController.shutDown();
            redisTabController = null;
        }
    }
}
