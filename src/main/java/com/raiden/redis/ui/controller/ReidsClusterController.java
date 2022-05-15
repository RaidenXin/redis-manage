package com.raiden.redis.ui.controller;

import com.raiden.redis.client.RedisClusterClient;
import com.raiden.redis.model.RedisClusterNodeInfo;
import com.raiden.redis.ui.common.AlertText;
import com.raiden.redis.ui.common.PathData;
import com.raiden.redis.ui.dao.RecordDao;
import com.raiden.redis.ui.mode.Record;
import com.raiden.redis.ui.mode.RedisClusterNode;
import com.raiden.redis.ui.mode.RedisNode;
import com.raiden.redis.ui.tab.ClusterRedisInfoTabPane;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 8:57 2022/5/14
 * @Modified By:
 */
public class ReidsClusterController implements Initializable {

    @FXML
    private TextField name;
    @FXML
    private TextField clusterHost;
    @FXML
    private TextField clusterPort;
    @FXML
    private CheckBox isVerification;
    @FXML
    private Label passwordText;
    @FXML
    private PasswordField password;
    @FXML
    private TableView record;
    @FXML
    private TableColumn<Record, String> recordName;
    @FXML
    private TableColumn<Record, String> recordHost;
    @FXML
    private TableColumn<Record, Integer> recordPort;
    @FXML
    private TableColumn<Record, String> recordPassword;
    @FXML
    private TableColumn<Record, Button> operation;

    private RecordDao recordDao;

    public ReidsClusterController(){
        this.recordDao = new RecordDao(PathData.REDIS_CLUSTER_HISTORICAL_RECORD_DATA_PATH);
    }

    public void connectionRedisCluster(){
        String host = clusterHost.getText();
        String port = clusterPort.getText();
        RedisController redisController = BeanContext.getBean(RedisController.class.getName());
        if (StringUtils.isNoneBlank(host, port)){
            try {
                RedisClusterClient redisClient = new RedisClusterClient(host.trim(),Integer.parseInt(port.trim()));
                if (isVerification.isSelected()){
                    //验证不成功停止执行
                    if (!verification(redisClient)){
                        return;
                    }
                }
                List<RedisClusterNodeInfo> redisClusterNodes = redisClient.clusterNodes();
                ClusterRedisInfoTabPane redisInfoTabPane = new ClusterRedisInfoTabPane();
                List<RedisNode> hosts = redisClusterNodes.stream()
                        .sorted()
                        .map(RedisClusterNode::build)
                        .collect(Collectors.toList());
                redisInfoTabPane.setRedisInfoTabPane(redisController.getRedisDataPage(), hosts);
                redisClient.close();
            }catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
            }
        }
    }

    private boolean verification(RedisClusterClient redisClient){
        String pd = password.getText();
        if (StringUtils.isNotBlank(pd)){
            boolean auth = redisClient.auth(pd.trim());
            if (!auth){
                Alert alert = new Alert(Alert.AlertType.ERROR, AlertText.PLEASE_FILL_IN_THE_CORRECT_PASSWORD);
                alert.showAndWait();
            }
            return auth;
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR, AlertText.PLEASE_FILL_IN_THE_CORRECT_PASSWORD);
            alert.showAndWait();
            return false;
        }
    }

    public void addRecord(){
        String nameText = name.getText();
        String host = clusterHost.getText();
        String port = clusterPort.getText();
        if (StringUtils.isAnyBlank(host, port)){
            return;
        }
        if (StringUtils.isBlank(nameText)){
            nameText = host;
        }
        Record recordItem = new Record();
        recordItem.setName(nameText);
        recordItem.setHost(host);
        recordItem.setPort(Integer.parseInt(port));
        recordItem.setPassword(password.getText());
        ObservableList items = record.getItems();
        items.add(recordItem);
        items.stream().sorted(Comparator.comparing(Record::getName));
        saveRecord();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isVerification.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            passwordText.setVisible(newValue);
            password.setVisible(newValue);
        });
        record.setEditable(true);
        record.setRowFactory( tv -> {
            TableRow<Record> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty()) ) {
                    Record rowData = row.getItem();
                    name.setText(rowData.getName());
                    clusterHost.setText(rowData.getHost());
                    clusterPort.setText(String.valueOf(rowData.getPort()));
                    String password = rowData.getPassword();
                    if (StringUtils.isNotBlank(password)){
                        isVerification.setSelected(true);
                        this.password.setText(password);
                    }else {
                        isVerification.setSelected(false);
                        this.password.setText(StringUtils.EMPTY);
                    }
                }
            });
            return row ;
        });
        recordName.setCellFactory(TextFieldTableCell.forTableColumn());
        recordHost.setCellFactory(TextFieldTableCell.forTableColumn());
        recordPort.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        recordPassword.setCellFactory(TextFieldTableCell.forTableColumn());
        recordName.setCellValueFactory(new PropertyValueFactory<>("name"));
        recordHost.setCellValueFactory(new PropertyValueFactory<>("host"));
        recordPort.setCellValueFactory(new PropertyValueFactory<>("port"));
        recordPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        recordName.setOnEditCommit(event -> {
            TableView tempTable = event.getTableView();
            Record item = (Record) tempTable.getItems().get(event.getTablePosition().getRow());
            item.setName(event.getNewValue());//放置新值
            saveRecord();
        });
        recordHost.setOnEditCommit(event -> {
            TableView tempTable = event.getTableView();
            Record item = (Record) tempTable.getItems().get(event.getTablePosition().getRow());
            item.setHost(event.getNewValue());//放置新值
            saveRecord();
        });
        recordPort.setOnEditCommit(event -> {
            TableView tempTable = event.getTableView();
            Record item = (Record) tempTable.getItems().get(event.getTablePosition().getRow());
            item.setPort(event.getNewValue());//放置新值
            saveRecord();
        });
        recordPassword.setOnEditCommit(event -> {
            TableView tempTable = event.getTableView();
            Record item = (Record) tempTable.getItems().get(event.getTablePosition().getRow());
            item.setPassword(event.getNewValue());//放置新值
            saveRecord();
        });
        record.getItems().addAll(recordDao.getRecords());
        BeanContext.setBean(this.getClass().getName(), this);
    }

    protected void saveRecord(){
        ObservableList<Record> items = record.getItems();
        List<Record> records = new ArrayList<>();
        //复制一遍防止 遍历的时候有 人修改
        synchronized (items){
            records.addAll(items);
        }
        recordDao.saveRecords(records);
    }
}
