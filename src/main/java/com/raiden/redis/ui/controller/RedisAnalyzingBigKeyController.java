package com.raiden.redis.ui.controller;

import com.raiden.redis.core.model.DataItem;
import com.raiden.redis.ui.util.AlertUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.whitbeck.rdbparser.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.PriorityQueue;
import java.util.ResourceBundle;

import static net.whitbeck.rdbparser.EntryType.*;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 16:08 2022/6/11
 * @Modified By:
 */
public class RedisAnalyzingBigKeyController implements Initializable {


    private static final Logger LOGGER = LogManager.getLogger(RedisAnalyzingBigKeyController.class);

    @FXML
    private TextField path;
    @FXML
    private Button analyse;
    @FXML
    private TableView dataTable;
    @FXML
    private TableColumn<DataItem, String> keyTableColumn;
    @FXML
    private TableColumn<DataItem, String> sizeTableColumn;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        keyTableColumn.setCellValueFactory(new PropertyValueFactory<>("key"));
        sizeTableColumn.setCellValueFactory(new PropertyValueFactory<>("valueSize"));
    }


    public void selectFile(){
        FileChooser fileChooser = new FileChooser();
        ObservableList<FileChooser.ExtensionFilter> extensionFilters = fileChooser.getExtensionFilters();
        extensionFilters.add(new FileChooser.ExtensionFilter("RDB Files", "*.rdb"));
        File rdb = fileChooser.showOpenDialog(new Stage());
        //数据库编号
        long databaseNumber = 0;
        int maxSize = 20;
        if (rdb != null){
            path.setText(rdb.getPath());
            analyse.setOnAction(event -> {
                //创建最小堆
                PriorityQueue<DataItem> minHeap = new PriorityQueue<>(maxSize);
                try (RdbParser parser = new RdbParser(rdb)) {
                    Entry e;
                    //查找到对应的数据库
                    for (;(e = parser.readNext()) != null && !(e.getType() == SELECT_DB && ((SelectDb)e).getId() == databaseNumber););

                    for (;(e = parser.readNext()) != null ;){
                        EntryType type = e.getType();
                        if (type == KEY_VALUE_PAIR){
                            int size = minHeap.size();
                            DataItem dataItem = DataItem.build((KeyValuePair) e);
                            if (size == maxSize){
                                //查看最小的
                                DataItem peek = minHeap.peek();
                                if (dataItem.compareTo(peek) > 0){
                                    minHeap.poll();
                                    minHeap.add(dataItem);
                                }
                            }else{
                                minHeap.add(dataItem);
                            }
                        }else if (type == SELECT_DB || type == EOF){
                            break;
                        }
                    }
                    DataItem[] items = new DataItem[maxSize];
                    for (int i = maxSize - 1; i > -1 ; i--) {
                        items[i] = minHeap.poll();
                    }
                    ObservableList dataTableItems = dataTable.getItems();
                    dataTableItems.clear();
                    dataTableItems.addAll(items);
                }catch (Exception e){
                    LOGGER.error("解析RDB文件异常", e);
                    AlertUtil.error("解析RDB文件异常", e);
                }
            });
        }
    }

}
