package com.raiden.redis.ui.controller;

import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.net.client.RedisClusterClient;
import com.raiden.redis.ui.mode.RedisDataItem;
import com.raiden.redis.ui.mode.RedisDatas;
import com.raiden.redis.ui.mode.RedisNode;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 16:22 2022/5/15
 * @Modified By:
 */
public class RedisClusterDataTableController implements Initializable {

    private static final String MOVED = "MOVED";
    private static final String START_INDEX = "0";
    private static final String FUZZY_SEARCH_SUFFIX = "*";

    @FXML
    private Button searchButton;
    @FXML
    private TextField searchKey;
    @FXML
    private Pane bottomBar;
    @FXML
    private TableView<RedisDataItem> table;
    @FXML
    private TableColumn<RedisDataItem, String> key;
    @FXML
    private TableColumn<RedisDataItem, String> value;
    @FXML
    private CheckBox isFuzzySearch;
    @FXML
    private ComboBox<String> pageSize;
    @FXML
    private TextArea keyTextArea;
    @FXML
    private TextArea valueTextArea;

    private RedisNode redisNode;
    private AtomicReference<String> currentIndex;
    private AtomicReference<String> nextIndex;
    private Stack<String> stack;
    private AtomicBoolean isInitTab;

    public RedisClusterDataTableController(){
        this.isInitTab = new AtomicBoolean(false);
    }

    public void setRedisNode(RedisNode redisNode){
        this.redisNode = redisNode;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchButton.setGraphic(new ImageView("/icon/search.jpg"));
        table.setRowFactory( tv -> {
                    TableRow<RedisDataItem> row = new TableRow<>();
                    row.setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                            RedisDataItem rowData = row.getItem();
                            keyTextArea.setText(rowData.getKey());
                            valueTextArea.setText(rowData.getValue());
                        }
                    });
                    return row ;
                }
        );
        key.setCellFactory(TextFieldTableCell.forTableColumn());
        value.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    public void search(){
        String text = searchKey.getText();
        if (StringUtils.isBlank(text)){
            table.getItems().clear();
            initTableData();
        }else {
            String key = text.trim();
            RedisClient client = redisNode.getRedisClient();
            //是否模糊查找
            if (isFuzzySearch.isSelected()){
                RedisDatas datas = scanRedisDatas(client, START_INDEX, key);
                stack.clear();
                currentIndex.set(START_INDEX);
                nextIndex.set(START_INDEX);
                ObservableList items = table.getItems();
                items.clear();
                items.addAll(datas.getItems());
                setButtonEvent(client, START_INDEX, datas.getNextIndex());
            }else {
                //精确查找
                String value = client.get(key);
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
    }

    private RedisDatas scanRedisDatas(RedisClient client, String index, String pattern){
        boolean selected = isFuzzySearch.isSelected();
        String[] scan;
        if (selected){
            //如果不是以模糊搜索后缀结尾的 补上后缀
            if (!pattern.endsWith(FUZZY_SEARCH_SUFFIX)){
                pattern += FUZZY_SEARCH_SUFFIX;
            }
            scan = client.scanMatch(START_INDEX, pattern, pageSize.getValue());
        }else {
            scan = client.scan(index, pageSize.getValue());
        }
        String[] keys = new String[scan.length - 1];
        System.arraycopy(scan, 1, keys, 0, keys.length);
        String[] values = client.mGet(keys);
        List<RedisDataItem> items = new ArrayList<>(values.length);
        int i = 0;
        for (String k : keys){
            items.add(new RedisDataItem(k, values[i++]));
        }
        return RedisDatas.build(items, scan[0]);
    }

    public void initTable(){
        if (isInitTab.compareAndSet(false, true)){
            initTableData();
        }
    }

    private void initTableData(){
        RedisClusterClient client = (RedisClusterClient) redisNode.getRedisClient();
        String[] scan = client.scan(START_INDEX, pageSize.getValue());
        key.setCellValueFactory(new PropertyValueFactory<>("key"));
        value.setCellValueFactory(new PropertyValueFactory<>("value"));
        String[] keys = new String[scan.length - 1];
        System.arraycopy(scan, 1, keys, 0, keys.length);
        String[] values = client.mGet(keys);
        ObservableList items = table.getItems();
        int index = 0;
        for (String key : keys){
            items.add(new RedisDataItem(key, values[index++]));
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
                ObservableList items = table.getItems();
                items.clear();
                //刷新数据
                RedisDatas datas = scanRedisDatas(client, index, searchKey.getText());
                items.addAll(datas.getItems());
                //将上一页标记为当前页
                String currentPageIndex = currentIndex.get();
                currentIndex.compareAndSet(currentPageIndex, index);
                //给下一页赋值
                String nextPageIndex = nextIndex.get();
                nextIndex.compareAndSet(nextPageIndex, datas.getNextIndex());
            }
        });
        Button nextPage = (Button) bottomBar.getChildren().get(1);
        nextPage.setOnMouseClicked((event) -> {
            //获取下一页的下标
            String index = nextIndex.get();
            //如果为空或者 为 0 证明没有下一页
            if (index != null && !START_INDEX.equals(index)){
                //清理掉之前的数据
                ObservableList items = table.getItems();
                items.clear();
                //获取当前 的下标
                String currentValue = currentIndex.get();
                //将当前的下标 放入栈中存储 供上一页按钮使用
                stack.add(currentValue);
                //获取下一页key
                RedisDatas datas = scanRedisDatas(client, index, searchKey.getText());
                items.addAll(datas.getItems());
                //完成翻页后 将下一个设置为当前页
                currentIndex.compareAndSet(currentValue, index);
                //如果下一页 index 赋值
                nextIndex.compareAndSet(index, datas.getNextIndex());
            }
        });
    }

    public void updateValue(){
        String key = keyTextArea.getText();
        String value = valueTextArea.getText();
        if (StringUtils.isBlank(value)){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Value不能为空！");
            alert.showAndWait();
        }
        RedisClient redisClient = redisNode.getRedisClient();
        redisClient.set(key, value);
    }
}
