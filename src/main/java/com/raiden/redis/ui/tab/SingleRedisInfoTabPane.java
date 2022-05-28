package com.raiden.redis.ui.tab;

import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.net.model.RedisKeyspace;
import com.raiden.redis.net.model.RedisNodeInfo;
import com.raiden.redis.ui.Window;
import com.raiden.redis.ui.common.ProjectValues;
import com.raiden.redis.ui.controller.RedisClusterDataTableController;
import com.raiden.redis.ui.controller.RedisTabController;
import com.raiden.redis.ui.mode.RedisNode;
import com.raiden.redis.ui.page.impl.TablePage;
import com.raiden.redis.ui.util.DataUtil;
import com.raiden.redis.ui.util.StyleUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:52 2022/5/7
 * @Modified By:
 */
public class SingleRedisInfoTabPane {

    private static final Logger LOGGER = LogManager.getLogger(SingleRedisInfoTabPane.class);

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
}
