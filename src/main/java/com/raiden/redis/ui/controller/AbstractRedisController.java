package com.raiden.redis.ui.controller;

import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.ui.common.AlertText;
import com.raiden.redis.ui.context.BeanContext;
import com.raiden.redis.ui.dao.RecordDao;
import com.raiden.redis.ui.mode.Record;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.util.converter.IntegerStringConverter;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 15:32 2022/5/28
 * @Modified By:
 */
public abstract class AbstractRedisController implements Initializable {

    @FXML
    private TextField name;
    @FXML
    protected CheckBox isVerification;
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
    private TableColumn<Record, HBox> operation;

    private RecordDao recordDao;

    private RedisLoginController loginController;

    public AbstractRedisController(RecordDao recordDao){
        this.recordDao = recordDao;
    }

    protected boolean verification(RedisClient redisClient){
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

    protected abstract String getHost();

    protected abstract String getPort();

    protected abstract void setHost(String host);

    protected abstract void setPort(String port);

    protected final String getPassword(){
        return password.getText();
    }

    public void setLoginController(RedisLoginController loginController){
        this.loginController = loginController;
    }

    protected void showLoginView(){
        if (loginController != null){
            loginController.show();
        }
    }

    protected void closeLoginView(){
        if (loginController != null){
            loginController.close();
        }
    }

    public void addRecord(){
        String nameText = name.getText();
        String host = getHost();
        String port = getPort();
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
        recordItem.setOperation(getRecordActionButton(items, recordItem));
        items.add(recordItem);
        items.stream().sorted(Comparator.comparing(Record::getName));
        saveRecord();
    }

    private Button[] getRecordActionButton(ObservableList items ,Record recordItem){
        Button[] result = new Button[2];
        Button delete = new Button("删除");
        // 对data操作即对表格操作，会同步更新的
        delete.setOnAction((event) ->{
            // 删除元素
            items.remove(recordItem);
            //再触发刷新并保存机制
            refreshAndSaveRecords();
        });
        result[0] = delete;
        delete.setLayoutX(0);
        delete.setPrefWidth(48.0D);
        Button use = new Button("使用");
        // 对data操作即对表格操作，会同步更新的
        use.setOnAction((event) ->{
            name.setText(recordItem.getName());
            setHost(recordItem.getHost());
            setPort(String.valueOf(recordItem.getPort()));
            String password = recordItem.getPassword();
            if (StringUtils.isNotBlank(password)){
                isVerification.setSelected(true);
                this.password.setText(password);
            }else {
                isVerification.setSelected(false);
                this.password.setText(StringUtils.EMPTY);
            }
        });
        use.setLayoutX(50.0D);
        use.setPrefWidth(48.0);
        result[1] = use;
        return result;
    }

    public void clearInputField(){
        name.setText(StringUtils.EMPTY);
        setHost(StringUtils.EMPTY);
        setPort(StringUtils.EMPTY);
        isVerification.setSelected(false);
        password.setText(StringUtils.EMPTY);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isVerification.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            passwordText.setVisible(newValue);
            password.setVisible(newValue);
        });
        record.setEditable(true);
        recordName.setCellFactory(TextFieldTableCell.forTableColumn());
        recordHost.setCellFactory(TextFieldTableCell.forTableColumn());
        recordPort.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        recordPassword.setCellFactory(TextFieldTableCell.forTableColumn());
        recordName.setCellValueFactory(new PropertyValueFactory<>("name"));
        recordHost.setCellValueFactory(new PropertyValueFactory<>("host"));
        recordPort.setCellValueFactory(new PropertyValueFactory<>("port"));
        recordPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        operation.setCellValueFactory(new PropertyValueFactory<>("operation"));
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
        ObservableList items = record.getItems();
        List<Record> records = recordDao.getRecords();
        records.forEach(r -> {
            r.setOperation(getRecordActionButton(items, r));
            items.add(r);
        });
        BeanContext.setBean(this.getClass().getName(), this);
    }

    protected void refreshAndSaveRecords(){
        ObservableList<Record> items = record.getItems();
        List<Record> records = new ArrayList<>();
        //复制一遍防止 遍历的时候有 人修改
        synchronized (items){
            records.addAll(items);
        }
        recordDao.refreshAndSaveRecords(records);
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
