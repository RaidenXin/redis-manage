package com.raiden.redis.ui.page.impl;

import com.raiden.redis.client.RedisClient;
import com.raiden.redis.ui.common.ProjectValues;
import com.raiden.redis.ui.mode.RedisNode;
import com.raiden.redis.ui.page.IPageService;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

/**
 * 用户个人中心页面
 * @author huhailong
 *
 */
public class TablePage implements IPageService {

    private static final String START_INDEX = "0";

	@Override
	public Node generatePage(RedisNode redisNode, HBox root, String warehouseIndex, double width) {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        TableView tableView = new TableView();
        tableView.setPrefHeight(root.getPrefHeight());
        TableColumn<Pair<String, String>, String> column1 = new TableColumn<>("Key");
        column1.setCellValueFactory(new PropertyValueFactory<>("key"));
        column1.setPrefWidth(width / 3.0D);

        TableColumn<Pair<String, String>, String> column2 = new TableColumn<>("Value");
        column2.setCellValueFactory(new PropertyValueFactory<>("value"));
        column2.setPrefWidth(width * 2.0D / 3.0D);

        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);
        RedisClient client = redisNode.getRedisClient();
        client.select(warehouseIndex);
        String[] scan = client.scan(START_INDEX);
        String[] keys = new String[scan.length - 1];
        System.arraycopy(scan, 1, keys, 0, keys.length);
        String[] values = client.mGet(keys);
        int index = 0;
        for (String key : keys){
            tableView.getItems().add(new Pair(key, values[index++]));
        }
        tableView.setPrefWidth(width);
        vbox.getChildren().add(tableView);
        return vbox;
	}
}
