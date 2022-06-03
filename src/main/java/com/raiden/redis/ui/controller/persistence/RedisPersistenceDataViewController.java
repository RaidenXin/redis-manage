package com.raiden.redis.ui.controller.persistence;

import com.raiden.redis.net.model.RedisNodeInfo;
import com.raiden.redis.net.model.RedisPersistence;
import com.raiden.redis.ui.util.PropertyValueUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Pair;


import java.net.URL;
import java.util.ResourceBundle;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 13:05 2022/6/3
 * @Modified By:
 */
public class RedisPersistenceDataViewController implements Initializable {


    @FXML
    private TableView rdbTable;
    @FXML
    private TableColumn<Pair<String, String>, String> rdbTableKey;
    @FXML
    private TableColumn<Pair<String, String>, String> rdbTableValue;
    @FXML
    private TableView aofTable;
    @FXML
    private TableColumn<Pair<String, String>, String> aofTableKey;
    @FXML
    private TableColumn<Pair<String, String>, String> aofTableValue;
    @FXML
    private TableView otherTable;
    @FXML
    private TableColumn<Pair<String, String>, String> otherTableKey;
    @FXML
    private TableColumn<Pair<String, String>, String> otherTableValue;
    @FXML
    private ProgressBar progressBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //RDB
        rdbTableKey.setCellValueFactory(PropertyValueUtil.getPairKeyPropertyValueFactory());
        rdbTableValue.setCellValueFactory(PropertyValueUtil.getPairValuePropertyValueFactory());
        //AOF
        aofTableKey.setCellValueFactory(PropertyValueUtil.getPairKeyPropertyValueFactory());
        aofTableValue.setCellValueFactory(PropertyValueUtil.getPairValuePropertyValueFactory());
        //其他信息
        otherTableKey.setCellValueFactory(PropertyValueUtil.getPairKeyPropertyValueFactory());
        otherTableValue.setCellValueFactory(PropertyValueUtil.getPairValuePropertyValueFactory());
    }

    public void refresh(Pair<String, RedisNodeInfo> info){
        RedisNodeInfo redisNodeInfo = info.getValue();
        RedisPersistence persistence = redisNodeInfo.getPersistence();
        if (persistence != null){
            //RDB
            {
                ObservableList items = rdbTable.getItems();
                items.clear();
                items.add(new Pair<>("rdb_changes_since_last_save(自上次转储以来的更改次数)", String.valueOf(persistence.getRdbChangesSinceLastSave())));
                items.add(new Pair<>("rdb_bgsave_in_progress(指示RDB文件是否正在保存的标志)", String.valueOf(persistence.getRdbBgsaveInProgress())));
                items.add(new Pair<>("rdb_last_save_time(上次成功保存RDB的时间戳)", String.valueOf(persistence.getRdbLastSaveTime())));
                items.add(new Pair<>("rdb_last_bgsave_status(上次RDB保存操作的状态)", String.valueOf(persistence.getRdbLastBgsaveStatus())));
                items.add(new Pair<>("rdb_last_bgsave_time_sec(上次RDB保存操作的持续时间/秒)", String.valueOf(persistence.getRdbLastBgsaveTimeSec())));
                items.add(new Pair<>("rdb_current_bgsave_time_sec(正在进行的RDB保存操作的持续时间)", String.valueOf(persistence.getRdbCurrentBgsaveTimeSec())));
                items.add(new Pair<>("rdb_last_cow_size(上次RDB保存操作期间copy-on-write分配的字节大小)", String.valueOf(persistence.getRdbLastCowSize())));
            }
            //AOF
            {
                ObservableList items = aofTable.getItems();
                items.clear();
                items.add(new Pair<>("aof_enabled(表示AOF记录已激活的标志)", String.valueOf(persistence.getAofEnabled())));
                items.add(new Pair<>("aof_rewrite_in_progress(表示AOF重写操作正在进行的标志)", String.valueOf(persistence.getAofRewriteInProgress())));
                items.add(new Pair<>("aof_rewrite_scheduled(表示进行中的RDB完成,就会安排进行AOF重写的标志)", String.valueOf(persistence.getAofRewriteScheduled())));
                items.add(new Pair<>("aof_last_rewrite_time_sec(上次AOF重写操作的持续时间/秒)", String.valueOf(persistence.getAofLastRewriteTimeSec())));
                items.add(new Pair<>("aof_current_rewrite_time_sec(正在进行的AOF重写操作的持续时间)", String.valueOf(persistence.getAofCurrentRewriteTimeSec())));
                items.add(new Pair<>("aof_last_bgrewrite_status(上次AOF重写操作的状态)", persistence.getAofLastBgrewriteStatus()));
                items.add(new Pair<>("aof_last_write_status(上一次AOF写入操作的状态)", persistence.getAofLastWriteStatus()));
                items.add(new Pair<>("aof_last_cow_size(上次AOF重写操作期间CopyOnWrite分配的字节大小)", String.valueOf(persistence.getAofLastCowSize())));
                items.add(new Pair<>("aof_current_size(当前的AOF文件大小)", String.valueOf(persistence.getAofCurrentSize())));
                items.add(new Pair<>("aof_base_size(指示AOF重写是否会在当前RDB操作完成后立即执行的标志)", String.valueOf(persistence.getAofBaseSize())));
                items.add(new Pair<>("aof_buffer_length(AOF缓冲区大小)", String.valueOf(persistence.getAofBufferLength())));
                items.add(new Pair<>("aof_rewrite_buffer_length(AOF重写缓冲区大小)", String.valueOf(persistence.getAofRewriteBufferLength())));
                items.add(new Pair<>("aof_pending_bio_fsync(在后台IO队列中等待fsync处理的任务数)", String.valueOf(persistence.getAofPendingBioFsync())));
                items.add(new Pair<>("aof_delayed_fsync(延迟fsync计数器)", String.valueOf(persistence.getAofDelayedFsync())));
            }
            //其他信息
            {
                ObservableList items = otherTable.getItems();
                items.clear();
                items.add(new Pair<>("current_cow_size(Fork时写拷贝的复制内存的大小/byte)", String.valueOf(persistence.getCurrentCowSize())));
                items.add(new Pair<>("current_save_keys_processed(当前保存操作处理的键数)", String.valueOf(persistence.getCurrentSaveKeysProcessed())));
                items.add(new Pair<>("current_save_keys_total(当前保存操作开始时的键数)", String.valueOf(persistence.getCurrentSaveKeysTotal())));
                items.add(new Pair<>("module_fork_in_progress(标识模块正处于Fork中的标识)", String.valueOf(persistence.getModuleForkInProgress())));
                items.add(new Pair<>("module_fork_last_cow_size(上一次Fork时,写拷贝所复制的内存大小/byte)", String.valueOf(persistence.getModuleForkLastCowSize())));
            }
            //当前Fork进度百分比
            {
                progressBar.setProgress(persistence.getCurrentForkPerc());
            }
        }
    }
}
