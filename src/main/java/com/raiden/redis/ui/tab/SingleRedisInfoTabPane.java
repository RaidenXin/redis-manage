package com.raiden.redis.ui.tab;

import com.raiden.redis.ui.common.ProjectValues;
import com.raiden.redis.ui.mode.RedisNode;
import com.raiden.redis.ui.page.impl.TablePage;
import com.raiden.redis.ui.util.DataUtil;
import com.raiden.redis.ui.util.StyleUtil;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:52 2022/5/7
 * @Modified By:
 */
public class SingleRedisInfoTabPane {

    private Integer currentMenuIndex;
    private Integer tempIndex;
    private static final Pair<String, String>[] WAREHOUSES = new Pair[]{new Pair<>("1号库", "1"),
            new Pair<>("2号库", "2"),
            new Pair<>("3号库", "3"),
            new Pair<>("4号库", "4"),
            new Pair<>("5号库", "5"),
            new Pair<>("6号库", "6"),
            new Pair<>("7号库", "7"),
            new Pair<>("8号库", "8"),
            new Pair<>("9号库", "9"),
            new Pair<>("10号库", "10"),
            new Pair<>("11号库", "11"),
            new Pair<>("12号库", "12")
    };

    public void setRedisInfoTabPane(Pane root, List<RedisNode> hosts) {
        double prefHeight = root.getPrefHeight();
        double prefWidth = root.getPrefWidth();
        TabPane tabPane = new TabPane();
        tabPane.setPrefHeight(prefHeight);
        tabPane.setPrefWidth(prefWidth);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        if (hosts != null) {
            List<Tab> tabs = hosts.stream().map(host -> {
                Tab tab = new Tab();
                tab.setText(host.getHostAndPort());
                AnchorPane anchorPane = new AnchorPane();
                HBox hBox = new HBox();
                hBox.setPrefHeight(prefHeight);
                hBox.setPrefWidth(prefWidth);
                setLeftMenu(host, hBox);
                anchorPane.getChildren().add(hBox);
                tab.setContent(anchorPane);
                return tab;
            }).collect(Collectors.toList());
            tabPane.getTabs().addAll(tabs);
        }
        root.getChildren().add(tabPane);
    }


    /**
     * 设置左侧菜单栏
     */
    private void setLeftMenu(RedisNode node, HBox root) {
        double leftWidth = ProjectValues.LEFT_MENU_WIDTH;
        VBox vbox = new VBox();
        vbox.setMinHeight(root.getPrefHeight());
        vbox.setMinWidth(leftWidth);
        StyleUtil.setPaneBackground(vbox, Color.web(ProjectValues.COLOR_PRIMARY));
        // 增加菜单中的项目
        setLeftMenuItemList(node, root, vbox, leftWidth);
        root.getChildren().add(vbox);
    }

    /**
     * 生成左侧菜单按钮
     */
    private void setLeftMenuItemList(RedisNode redisNode, HBox root, VBox leftMenu, double width) {
        double buttonHeight = 30;
        List<Button> buttonList = new ArrayList<>(12);
        for (Pair<String, String> warehouse : WAREHOUSES) {
            Button button = new Button(warehouse.getKey());
            button.setMinWidth(width);
            button.setMinHeight(buttonHeight);
            StyleUtil.setButtonBackground(button, Color.web(ProjectValues.COLOR_PRIMARY), Color.WHITE);
            //增加鼠标移动到菜单上到hover效果
            button.setOnMouseMoved(event -> {
                StyleUtil.setButtonBackground(button, Color.BLACK, Color.WHITE);
                StyleUtil.setFont(button, Color.web("#E6A23C"), -1);
            });
            button.setOnMouseExited(event -> {
                if (currentMenuIndex == null || !button.getText().equals(WAREHOUSES[currentMenuIndex])) {
                    StyleUtil.setButtonBackground(button, Color.web(ProjectValues.COLOR_PRIMARY), Color.WHITE);
                } else {
                    StyleUtil.setButtonBackground(button, Color.web(ProjectValues.COLOR_PRIMARY), Color.web(ProjectValues.COLOR_SELECTED));
                }
            });
            button.setOnMouseClicked(event -> {
                currentMenuIndex = DataUtil.getIndexForArray(WAREHOUSES, button.getText());
                Node currentPageNode = new TablePage().generatePage(redisNode, root, warehouse.getValue(), root.getPrefWidth() - ProjectValues.LEFT_MENU_WIDTH);
                ObservableList<Node> items = root.getChildren();
                if (items.size() > 1) {
                    items.remove(1);    //清楚右侧页面路由组件节点
                }
                HBox.setHgrow(currentPageNode, Priority.ALWAYS);
                items.add(currentPageNode);
                StyleUtil.setFont(button, Color.web(ProjectValues.COLOR_SELECTED), -1);
                //选中状态逻辑
                if (tempIndex != null) {
                    Button node = (Button) leftMenu.getChildren().get(tempIndex);
                    StyleUtil.setFont(node, Color.WHITE, -1);    //清空选中状态样式
                    StyleUtil.setButtonBackground(node, Color.web(ProjectValues.COLOR_PRIMARY), Color.WHITE);
                }
                StyleUtil.setFont(button, Color.web(ProjectValues.COLOR_SELECTED), -1);    //设置选中样式
                tempIndex = currentMenuIndex;
            });
            buttonList.add(button);
        }
        leftMenu.getChildren().addAll(buttonList);
    }
}
