package com.raiden.redis.ui.controller;

import com.raiden.redis.client.RedisClient;
import com.raiden.redis.ui.cell.EditingCell;
import com.raiden.redis.ui.mode.RedisDataItem;
import com.raiden.redis.ui.mode.RedisNode;
import com.sun.javafx.css.converters.StringConverter;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 0:14 2022/5/14
 * @Modified By:
 */
public class RedisClusterTabController implements Initializable {

    private static final String MOVED = "MOVED";
    private static final String START_INDEX = "0";

    @FXML
    private Tab tab;
    @FXML
    private Button searchButton;
    @FXML
    private TextField searchKey;
    @FXML
    private Pane bottomBar;
    @FXML
    private TableView table;
    @FXML
    private TableColumn<RedisDataItem, String> key;
    @FXML
    private TableColumn<RedisDataItem, String> value;

    private RedisNode redisNode;

    private AtomicReference<String> currentIndex;
    private AtomicReference<String> nextIndex;
    private Stack<String> stack;
    private AtomicReference<Boolean> isInitTab;

    public RedisClusterTabController(){
        this.isInitTab = new AtomicReference<>(false);
    }


    public void setRedisNode(RedisNode redisNode){
        this.redisNode = redisNode;
    }

    public boolean isInitTab(){
        return isInitTab.get();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tab.setGraphic(new ImageView("/icon/redis2.jpg"));
        searchButton.setGraphic(new ImageView("/icon/search.jpg"));
        Callback<TableColumn<RedisDataItem, String>, TableCell<RedisDataItem, String>> cellFactory = (TableColumn<RedisDataItem, String> p) -> new TextFieldTableCell<>();
        value.setCellFactory(cellFactory);
    }

    public void search(){
        String text = searchKey.getText();
        if (StringUtils.isBlank(text)){
            table.getItems().clear();
            initTableData();
        }else {
            String key = text.trim();
            RedisClient redisClusterClient = redisNode.getRedisClient();
            String value = redisClusterClient.get(key);
            ObservableList items = table.getItems();
            if (StringUtils.isNotBlank(value)){
                if (value.startsWith(MOVED)){
                    String[] values = StringUtils.split(value, " ");
                    if (values.length != 3){
                        Alert alert = new Alert(Alert.AlertType.ERROR,"系统异常！");
                        alert.showAndWait();
                    }else {
                        Alert alert = new Alert(Alert.AlertType.WARNING,"请跳转到HOST:{ " + values[2] + " }");
                        alert.showAndWait();
                    }
                    return;
                }else {
                    stack.clear();
                    currentIndex.set(START_INDEX);
                    nextIndex.set(START_INDEX);
                    items.clear();
                    table.getItems().add(new RedisDataItem(key, value));
                }
            }else {
                stack.clear();
                currentIndex.set(START_INDEX);
                nextIndex.set(START_INDEX);
                items.clear();
            }
        }
    }

    public void initTable(){
        if (isInitTab.compareAndSet(false, true)){
            initTableData();
        }
    }

    private void initTableData(){
        RedisClient client = redisNode.getRedisClient();
        String[] scan = client.scan(START_INDEX);
        key.setCellValueFactory(new PropertyValueFactory<>("key"));
        value.setCellValueFactory(new PropertyValueFactory<>("value"));
        String[] keys = new String[scan.length - 1];
        System.arraycopy(scan, 1, keys, 0, keys.length);
        String[] values = client.mGet(keys);
        int index = 0;
        for (String key : keys){
            table.getItems().add(new RedisDataItem(key, values[index++]));
        }
        setButtonEvent(client, START_INDEX, scan[0]);
    }


    private void setButtonEvent(RedisClient client, String current, String next){
        Button prePage = (Button) bottomBar.getChildren().get(0);
        currentIndex = new AtomicReference<>(current);
        nextIndex = new AtomicReference<>(next);
        stack = new Stack<>();
        prePage.setOnMouseClicked((event) -> {
            if (stack.size() == 0){
                return;
            }
            //从栈中弹出上一页
            String index = stack.pop();
            //如果没有证明当前是第一页
            if (index != null){
                //清理掉之前的数据
                table.getItems().clear();
                //刷新数据
                String[] scan = client.scan(index);
                String[] keys = new String[scan.length - 1];
                System.arraycopy(scan, 1, keys, 0, keys.length);
                String[] values = client.mGet(keys);
                int i = 0;
                for (String key : keys){
                    table.getItems().add(new RedisDataItem(key, values[i++]));
                }
                //将上一页标记为当前页
                String currentPageIndex = currentIndex.get();
                currentIndex.compareAndSet(currentPageIndex, index);
                //给下一页赋值
                String nextPageIndex = nextIndex.get();
                nextIndex.compareAndSet(nextPageIndex, scan[0]);
            }
        });
        Button nextPage = (Button) bottomBar.getChildren().get(1);
        nextPage.setOnMouseClicked((event) -> {
            //获取下一页的下标
            String index = nextIndex.get();
            //如果为空或者 为 0 证明没有下一页
            if (index != null && !START_INDEX.equals(index)){
                //清理掉之前的数据
                table.getItems().clear();
                //获取当前 的下标
                String currentValue = currentIndex.get();
                //将当前的下标 放入栈中存储 供上一页按钮使用
                stack.add(currentValue);
                //获取下一页key
                String[] scan = client.scan(index);
                String[] keys = new String[scan.length - 1];
                System.arraycopy(scan, 1, keys, 0, keys.length);
                String[] values = client.mGet(keys);
                int i = 0;
                for (String key : keys){
                    table.getItems().add(new RedisDataItem(key, values[i++]));
                }
                //完成翻页后 将下一个设置为当前页
                currentIndex.compareAndSet(currentValue, index);
                //如果下一页 index 赋值
                nextIndex.compareAndSet(index, scan[0]);
            }
        });
    }
}
