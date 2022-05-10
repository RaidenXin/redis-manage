package com.raiden.redis.ui.page.impl;

import com.raiden.redis.ui.common.ProjectValues;
import com.raiden.redis.ui.mode.RedisNode;
import com.raiden.redis.ui.page.IPageService;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
/**
 * 用户个人中心页面
 * @author huhailong
 *
 */
public class TablePage implements IPageService {

	@Override
	public Node generatePage(RedisNode redisNode, HBox root) {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        TableView tableView = new TableView();
        tableView.setPrefHeight(root.getPrefHeight());
        tableView.setPrefWidth(root.getPrefWidth() - ProjectValues.LEFT_MENU_WIDTH);
        vbox.getChildren().add(tableView);
        return vbox;
	}
}
