package com.raiden.redis.ui.controller;

import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.net.client.RedisClusterClient;
import com.raiden.redis.net.exception.MovedException;
import com.raiden.redis.net.exception.RedisException;
import com.raiden.redis.net.model.ScanResult;
import com.raiden.redis.ui.mode.RedisDataItem;
import com.raiden.redis.ui.mode.RedisDatas;
import com.raiden.redis.ui.mode.RedisNode;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.raiden.redis.net.common.ScanCommonParams.*;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 20:04 2022/5/22
 * @Modified By:
 */
public class RedisHSetDataViewController implements Controller, Initializable {

    @FXML
    private TableView tableView;
    @FXML
    private TableColumn<Pair<String,String>, String> field;
    @FXML
    private TableColumn<Pair<String,String>, String> value;
    @FXML
    private TextArea keyTextArea;
    @FXML
    private TextArea valueTextArea;
    @FXML
    private Button searchButton;
    @FXML
    private TextField searchKey;
    @FXML
    private CheckBox isFuzzySearch;
    @FXML
    private Button pre;
    @FXML
    private Button next;

    private RedisNode redisNode;
    private AtomicReference<String> currentIndex;
    private AtomicReference<String> nextIndex;
    private Stack<String> stack;
    private String key;

    @Override
    public void setRedisNode(RedisNode redisNode) {
        this.redisNode = redisNode;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchButton.setGraphic(new ImageView("/icon/search.jpg"));
        tableView.setRowFactory( tv -> {
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
        field.setCellFactory(TextFieldTableCell.forTableColumn());
        value.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    @Override
    public void init(String key) {
        this.key = key;
        if (redisNode != null){
            RedisClient redisClient = redisNode.getRedisClient();
            ScanResult<Pair<String, String>> scanResult = redisClient.hScan(key, START_INDEX, STEP_LENGTH);
        }
    }

    public void search() {
        String text = searchKey.getText();
        if (StringUtils.isBlank(text)) {
            tableView.getItems().clear();
            initTableData();
        } else {
            String key = text.trim();
            RedisClient client = redisNode.getRedisClient();
            //是否模糊查找
            if (isFuzzySearch.isSelected()) {
                ScanResult<Pair<String, String>> datas = hscan(client, START_INDEX, key);
                stack.clear();
                currentIndex.set(START_INDEX);
                nextIndex.set(START_INDEX);
                ObservableList items = tableView.getItems();
                items.clear();
                items.addAll(datas.getResult());
                setButtonEvent(client, START_INDEX, datas.getCursor());
            } else {
                try {
                    //精确查找
                    String value = client.get(key);
                    ObservableList items = tableView.getItems();
                    if (StringUtils.isNotBlank(value)) {
                        stack.clear();
                        currentIndex.set(START_INDEX);
                        nextIndex.set(START_INDEX);
                        items.clear();
                        tableView.getItems().add(new RedisDataItem(key, value));
                    } else {
                        stack.clear();
                        currentIndex.set(START_INDEX);
                        nextIndex.set(START_INDEX);
                        items.clear();
                    }
                }catch (MovedException e){
                    String[] values = StringUtils.split(e.getMessage(), " ");
                    Alert alert = new Alert(Alert.AlertType.WARNING, "请跳转到HOST:{ " + values[2] + " }");
                    alert.showAndWait();
                }catch (RedisException e){
                    Alert alert = new Alert(Alert.AlertType.ERROR, "系统异常！" + e.getMessage());
                    alert.showAndWait();
                }
            }
        }
    }

    private ScanResult<Pair<String, String>> hscan(RedisClient client, String index, String pattern){
        boolean selected = isFuzzySearch.isSelected();
        ScanResult<Pair<String, String>> scan;
        if (selected){
            //如果不是以模糊搜索后缀结尾的 补上后缀
            if (!pattern.endsWith(FUZZY_SEARCH_SUFFIX)){
                pattern += FUZZY_SEARCH_SUFFIX;
            }
            scan = client.hScanMatch(this.key, START_INDEX, pattern, STEP_LENGTH);
        }else {
            scan = client.hScan(this.key, index, STEP_LENGTH);
        }
        return scan;
    }

    private void initTableData(){
        RedisClusterClient client = (RedisClusterClient) redisNode.getRedisClient();
        String[] scan = client.scan(START_INDEX, STEP_LENGTH);
        field.setCellValueFactory(new PropertyValueFactory<>("key"));
        value.setCellValueFactory(new PropertyValueFactory<>("value"));
        String[] keys = new String[scan.length - 1];
        System.arraycopy(scan, 1, keys, 0, keys.length);
        String[] values = client.mGet(keys);
        ObservableList items = tableView.getItems();
        int index = 0;
        for (String key : keys){
            items.add(new RedisDataItem(key, values[index++]));
        }
        setButtonEvent(client, START_INDEX, scan[0]);
    }


    private void setButtonEvent(RedisClient client, String current, String nextCursor){
        currentIndex = new AtomicReference<>(current);
        nextIndex = new AtomicReference<>(nextCursor);
        stack = new Stack<>();
        pre.setOnMouseClicked((event) -> {
            if (stack.size() == 0){
                return;
            }
            //从栈中弹出上一页
            String index = stack.pop();
            //如果没有证明当前是第一页
            if (index != null){
                //清理掉之前的数据
                ObservableList items = tableView.getItems();
                items.clear();
                //刷新数据
                ScanResult<Pair<String, String>> datas = hscan(client, index, searchKey.getText());
                items.addAll(datas.getResult());
                //将上一页标记为当前页
                String currentPageIndex = currentIndex.get();
                currentIndex.compareAndSet(currentPageIndex, index);
                //给下一页赋值
                String nextPageIndex = nextIndex.get();
                nextIndex.compareAndSet(nextPageIndex, datas.getCursor());
            }
        });
        next.setOnMouseClicked((event) -> {
            //获取下一页的下标
            String index = nextIndex.get();
            //如果为空或者 为 0 证明没有下一页
            if (index != null && !START_INDEX.equals(index)){
                //清理掉之前的数据
                ObservableList items = tableView.getItems();
                items.clear();
                //获取当前 的下标
                String currentValue = currentIndex.get();
                //将当前的下标 放入栈中存储 供上一页按钮使用
                stack.add(currentValue);
                //获取下一页key
                ScanResult<Pair<String, String>> datas = hscan(client, index, searchKey.getText());
                items.addAll(datas.getResult());
                //完成翻页后 将下一个设置为当前页
                currentIndex.compareAndSet(currentValue, index);
                //如果下一页 index 赋值
                nextIndex.compareAndSet(index, datas.getCursor());
            }
        });
    }
}
