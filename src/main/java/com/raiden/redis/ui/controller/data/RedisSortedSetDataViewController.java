package com.raiden.redis.ui.controller.data;

import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.net.client.RedisClusterClient;
import com.raiden.redis.net.exception.MovedException;
import com.raiden.redis.net.exception.RedisException;
import com.raiden.redis.net.model.ScanResult;
import com.raiden.redis.ui.controller.Controller;
import com.raiden.redis.ui.controller.add.AddElementsController;
import com.raiden.redis.ui.mode.RedisNode;
import com.raiden.redis.ui.util.FXMLLoaderUtils;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.raiden.redis.net.common.ScanCommonParams.*;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 20:04 2022/5/22
 * @Modified By:
 */
public class RedisSortedSetDataViewController implements Controller, Initializable {

    @FXML
    private TableView<Pair<String, String>> tableView;
    @FXML
    private TableColumn<Pair<String,String>, String> score;
    @FXML
    private TableColumn<Pair<String,String>, String> value;
    @FXML
    private Label searchTitle;
    @FXML
    private TextField minScore;
    @FXML
    private Label scoreSeparator;
    @FXML
    private TextField maxScore;
    @FXML
    private Button deleteButtonByScore;
    @FXML
    private RadioButton valueQueryPatterns;
    @FXML
    private RadioButton scoreQueryPatterns;
    @FXML
    private TextArea scoreTextArea;
    @FXML
    private TextArea valueTextArea;
    @FXML
    private TextField searchValue;
    @FXML
    private CheckBox isFuzzySearch;
    @FXML
    private Button pre;
    @FXML
    private Button next;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;

    private RedisNode redisNode;
    private AtomicReference<String> currentIndex;
    private AtomicReference<String> nextIndex;
    private Stack<String> stack;
    private String key;
    private AtomicInteger pageNo;
    private AtomicInteger pageTotal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setRowFactory( tv -> {
                    TableRow<Pair<String, String>> row = new TableRow<>();
                    row.setOnMouseClicked(event -> {
                        int clickCount = event.getClickCount();
                        if ((clickCount == 1 || clickCount == 2) && !row.isEmpty()){
                            Pair<String, String> rowData = row.getItem();
                            deleteButton.setOnAction((actionEvent) -> {
                                RedisClient redisClient = redisNode.getRedisClient();
                                redisClient.zRem(this.key, rowData.getValue());
                                refreshTableData();
                            });
                            if (clickCount == 2 ) {
                                scoreTextArea.setText(rowData.getKey());
                                valueTextArea.setText(rowData.getValue());
                            }
                        }
                    });
                    return row ;
                }
        );
        score.setCellFactory(TextFieldTableCell.forTableColumn());
        value.setCellFactory(TextFieldTableCell.forTableColumn());
        score.setCellValueFactory(new PropertyValueFactory<>("key"));
        value.setCellValueFactory(new PropertyValueFactory<>("value"));
        addButton.setGraphic(new ImageView("/icon/add.png"));
        deleteButton.setGraphic(new ImageView("icon/delete.png"));
        ToggleGroup group = new ToggleGroup();
        valueQueryPatterns.setToggleGroup(group);
        scoreQueryPatterns.setToggleGroup(group);
        valueQueryPatterns.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            //默认设置隐藏
            boolean isVisible = false;
            if (newValue){
                searchTitle.setText("Value:");
                //设置显现
                isVisible = true;
            }
            isFuzzySearch.setVisible(isVisible);
            searchValue.setText(StringUtils.EMPTY);
            searchValue.setVisible(isVisible);
        });
        scoreQueryPatterns.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            //默认设置隐藏
            boolean isVisible = false;
            if (newValue){
                searchTitle.setText("Score:");
                //设置显现
                isVisible = true;
            }
            minScore.setVisible(isVisible);
            minScore.setText(StringUtils.EMPTY);
            scoreSeparator.setVisible(isVisible);
            maxScore.setVisible(isVisible);
            maxScore.setText(StringUtils.EMPTY);
            deleteButtonByScore.setVisible(isVisible);
        });
    }

    @Override
    public void init(RedisNode redisNode,String key) {
        this.key = key;
        this.redisNode = redisNode;
        if (redisNode != null){
            refreshTableData();
        }
    }

    public void search() {
        boolean selected = valueQueryPatterns.isSelected();
        if (selected){
            searchByValue();
        }else {
            searchByScore();
        }
    }

    private void searchByValue() {
        String text = searchValue.getText();
        if (StringUtils.isBlank(text)) {
            tableView.getItems().clear();
            refreshTableData();
        } else {
            String field = text.trim();
            RedisClient client = redisNode.getRedisClient();
            //是否模糊查找
            if (isFuzzySearch.isSelected()) {
                ScanResult<Pair<String, String>> data = zScan(client, START_INDEX, field);
                stack.clear();
                currentIndex.set(START_INDEX);
                nextIndex.set(START_INDEX);
                ObservableList items = tableView.getItems();
                items.clear();
                items.addAll(data.getResult());
                setButtonEvent(client, START_INDEX, data.getCursor());
            } else {
                try {
                    //精确查找
                    String score = client.zScore(this.key, field);
                    ObservableList items = tableView.getItems();
                    if (StringUtils.isNotBlank(score)) {
                        stack.clear();
                        currentIndex.set(START_INDEX);
                        nextIndex.set(START_INDEX);
                        items.clear();
                        items.add(new Pair<>(score, value));
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


    private void searchByScore() {
        String max = maxScore.getText();
        String min = minScore.getText();
        ObservableList<Pair<String, String>> items = tableView.getItems();
        items.clear();
        if (StringUtils.isAnyBlank(max, min)){
            refreshTableData();
        }else {
            RedisClient client = redisNode.getRedisClient();
            int count = client.zCount(this.key, min, max);
            pageNo = new AtomicInteger(1);
            pageTotal = new AtomicInteger( count / STEP_LENGTH_NUMBER + (count % STEP_LENGTH_NUMBER == 0 ? 0 : 1));
            Pair<String, String>[] pairs = client.zRangeByScore(this.key, min, max, START_INDEX, STEP_LENGTH);
            items.addAll(pairs);
            pre.setOnMouseClicked((event) -> {
                int oldPageNo = pageNo.get();
                //如果没有证明当前是第一页
                if (oldPageNo > 1){
                    //清理掉之前的数据
                    items.clear();
                    //刷新数据
                    int newPageNo = pageNo.addAndGet(-1) - 1;
                    Pair<String, String>[] data = client.zRangeByScore(this.key, min, max, String.valueOf(STEP_LENGTH_NUMBER * newPageNo), STEP_LENGTH);
                    items.addAll(data);
                }
            });
            next.setOnMouseClicked((event) -> {
                //获取下一页的下标
                int oldPageNo = pageNo.get();
                //如果为空或者 为 0 证明没有下一页
                if (oldPageNo < pageTotal.get()){
                    //清理掉之前的数据
                    items.clear();
                    //刷新数据
                    int newPageNo = pageNo.addAndGet(1) - 1;
                    Pair<String, String>[] data = client.zRangeByScore(this.key, min, max, String.valueOf(STEP_LENGTH_NUMBER * newPageNo), STEP_LENGTH);
                    items.addAll(data);
                }
            });
        }
    }


    private ScanResult<Pair<String, String>> zScan(RedisClient client, String index, String pattern){
        boolean selected = isFuzzySearch.isSelected();
        ScanResult<Pair<String, String>> scan;
        if (selected){
            //如果不是以模糊搜索后缀结尾的 补上后缀
            if (!pattern.endsWith(FUZZY_SEARCH_SUFFIX)){
                pattern += FUZZY_SEARCH_SUFFIX;
            }
            scan = client.zScanMatch(this.key, START_INDEX, pattern, STEP_LENGTH);
        }else {
            scan = client.zScan(this.key, index, STEP_LENGTH);
        }
        return scan;
    }

    private void refreshTableData(){
        RedisClusterClient client = (RedisClusterClient) redisNode.getRedisClient();
        ScanResult<Pair<String, String>> scan = client.zScan(this.key, START_INDEX, STEP_LENGTH);
        ObservableList<Pair<String, String>> items = tableView.getItems();
        items.clear();
        items.addAll(scan.getResult());
        setButtonEvent(client, START_INDEX, scan.getCursor());
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
                ScanResult<Pair<String, String>> data = zScan(client, index, searchValue.getText());
                items.addAll(data.getResult());
                //将上一页标记为当前页
                String currentPageIndex = currentIndex.get();
                currentIndex.compareAndSet(currentPageIndex, index);
                //给下一页赋值
                String nextPageIndex = nextIndex.get();
                nextIndex.compareAndSet(nextPageIndex, data.getCursor());
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
                ScanResult<Pair<String, String>> data = zScan(client, index, searchValue.getText());
                items.addAll(data.getResult());
                //完成翻页后 将下一个设置为当前页
                currentIndex.compareAndSet(currentValue, index);
                //如果下一页 index 赋值
                nextIndex.compareAndSet(index, data.getCursor());
            }
        });
    }

    public void deleteByScore(){
        String max = maxScore.getText();
        if (StringUtils.isBlank(max)){
            Alert alert = new Alert(Alert.AlertType.ERROR, "最大值不能为空!");
            alert.showAndWait();
        }
        String min = minScore.getText();
        if (StringUtils.isBlank(max)){
            Alert alert = new Alert(Alert.AlertType.ERROR, "最小值不能为空!");
            alert.showAndWait();
        }
        DialogPane dialog = new DialogPane();
        dialog.setContentText("你是否确认要删除当前Score(分度值):{" + min + "-" + max + "}范围内所有的值？");
        ObservableList<ButtonType> buttonTypes = dialog.getButtonTypes();
        buttonTypes.addAll(ButtonType.YES, ButtonType.NO);
        Stage dialogStage = new Stage();
        Scene dialogScene = new Scene(dialog);
        Button yes = (Button) dialog.lookupButton(ButtonType.YES);
        yes.setOnAction(event -> {
            RedisClient redisClient = redisNode.getRedisClient();
            redisClient.zRemRangeByScore(this.key, min, max);
            dialogStage.close();
            refreshTableData();
        });
        Button no = (Button) dialog.lookupButton(ButtonType.NO);
        no.setOnAction(event -> {
            dialogStage.close();
        });
        dialogStage.setScene(dialogScene);
        dialogStage.setTitle("删除提醒");
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setResizable(false);
        dialogStage.show();
    }


    public void openAddView(){
        Stage window = new Stage();
        window.setTitle("添加");
        //modality要使用Modality.APPLICATION_MODEL
        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinWidth(300);
        window.setMinHeight(150);

        FXMLLoader fxmlLoader = FXMLLoaderUtils.getFXMLLoader("add/add_elements_view.fxml");
        try {
            TitledPane load = fxmlLoader.load();
            AddElementsController controller = fxmlLoader.getController();
            controller.zAdd(redisNode, window, this.key);
            Scene scene = new Scene(load);
            window.setScene(scene);
            window.showAndWait();
            refreshTableData();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "系统发生错误!" + e.getMessage());
            alert.showAndWait();
        }
    }
}
