package com.raiden.redis.ui.controller.memory;

import com.raiden.redis.net.model.RedisMemoryInfo;
import com.raiden.redis.net.model.RedisNodeInfo;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 11:14 2022/6/2
 * @Modified By:
 */
public class MemoryDataViewController implements Initializable {


    //标准高度间隔
    private static final double STANDARD_HEIGHT_INTERVAL = 30D;

    @FXML
    private TableView memoryTable;
    @FXML
    private TableColumn memoryTableKey;
    @FXML
    private TableColumn memoryTableValue;
    @FXML
    private ScrollPane scrollPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //memory
        memoryTableKey.setCellValueFactory(new PropertyValueFactory<>("key"));
        memoryTableValue.setCellValueFactory(new PropertyValueFactory<>("value"));
    }


    public void refresh(Pair<String, RedisNodeInfo> info){
        //memory
        {
            RedisNodeInfo redisNodeInfo = info.getValue();
            RedisMemoryInfo memory = redisNodeInfo.getMemory();
            if (memory != null){
                ObservableList items = memoryTable.getItems();
                items.clear();
                items.add(new Pair<>("used_memory(Redis分配的内存总量/byte)", String.valueOf(memory.getUsedMemory())));
                items.add(new Pair<>("used_memory_human(Redis分配的内存总量/MB)", memory.getUsedMemoryHuman()));
                items.add(new Pair<>("used_memory_rss(向操作系统申请的内存大小/byte)", String.valueOf(memory.getUsedMemoryRss())));
                items.add(new Pair<>("used_memory_rss_human(向操作系统申请的内存大小/MB)", memory.getUsedMemoryRssHuman()));
                items.add(new Pair<>("used_memory_peak(redis的内存消耗峰值/byte)", String.valueOf(memory.getUsedMemoryPeak())));
                items.add(new Pair<>("used_memory_peak_human(Redis的内存消耗峰值/MB)", memory.getUsedMemoryPeakHuman()));
                items.add(new Pair<>("used_memory_peak_perc(使用内存达到峰值内存的百分比)", memory.getUsedMemoryPeakPerc()));
                items.add(new Pair<>("used_memory_overhead(Redis服务内存开销,除数据以外/byte)", String.valueOf(memory.getUsedMemoryOverhead())));
                items.add(new Pair<>("used_memory_startup(服务启动时消耗的内存/byte)", String.valueOf(memory.getUsedMemoryStartup())));
                items.add(new Pair<>("used_memory_dataset(数据占用内存/byte)", String.valueOf(memory.getUsedMemoryDataset())));
                items.add(new Pair<>("used_memory_dataset_perc(数据占用内存的百分比)", memory.getUsedMemoryDatasetPerc()));
                items.add(new Pair<>("allocator_allocated(分配器分配的内存/byte)", String.valueOf(memory.getAllocatorAllocated())));
                items.add(new Pair<>("allocator_active(分配器活跃的内存/byte)", String.valueOf(memory.getAllocatorActive())));
                items.add(new Pair<>("allocator_resident(分配器常驻的内存/MB)", String.valueOf(memory.getAllocatorResident())));
                items.add(new Pair<>("total_system_memory(主机内存总量/byte)", String.valueOf(memory.getTotalSystemMemory())));
                items.add(new Pair<>("total_system_memory_human(主机内存总量)", memory.getTotalSystemMemoryHuman()));
                items.add(new Pair<>("used_memory_lua(Lua脚本占用的内存/byte)", String.valueOf(memory.getUsedMemoryLua())));
                items.add(new Pair<>("used_memory_lua_human(Lua引擎存储占用的内存)", memory.getUsedMemoryLuaHuman()));
                items.add(new Pair<>("used_memory_scripts(Lua引擎存储占用的内存/byte)", String.valueOf(memory.getUsedMemoryScripts())));
                items.add(new Pair<>("used_memory_scripts_human(Lua脚本所占用的内存)", memory.getUsedMemoryScriptsHuman()));
                items.add(new Pair<>("number_of_cached_scripts(主机内存总量)", String.valueOf(memory.getNumberOfCachedScripts())));
                items.add(new Pair<>("maxmemory(配置中设置的最大可使用内存值)", String.valueOf(memory.getMaxmemory())));
                items.add(new Pair<>("maxmemory_human(配置中设置的最大可使用内存值)", memory.getMaxmemoryHuman()));
                items.add(new Pair<>("maxmemory_policy(内存淘汰策略)", memory.getMaxmemoryPolicy()));
                items.add(new Pair<>("allocator_frag_ratio(分配器的碎片率)", String.valueOf(memory.getAllocatorFragRatio())));
                items.add(new Pair<>("allocator_frag_bytes(分配器的碎片大小)", String.valueOf(memory.getAllocatorFragBytes())));
                items.add(new Pair<>("allocator_rss_ratio(分配器的碎片大小)", String.valueOf(memory.getAllocatorRssRatio())));
                items.add(new Pair<>("allocator_rss_bytes(分配器的常驻内存大小)", String.valueOf(memory.getAllocatorRssBytes())));
                items.add(new Pair<>("rss_overhead_ratio(常驻内存开销比例)", String.valueOf(memory.getRssOverheadRatio())));
                items.add(new Pair<>("rss_overhead_bytes(分配器的碎片大小)", String.valueOf(memory.getRssOverheadBytes())));
                items.add(new Pair<>("mem_fragmentation_bytes(内存碎片大小)", String.valueOf(memory.getMemFragmentationBytes())));
                items.add(new Pair<>("mem_not_counted_for_evict(被驱逐的内存)", String.valueOf(memory.getMemNotCountedForEvict())));
                items.add(new Pair<>("mem_replication_backlog(Redis复制积压缓冲区内存)", String.valueOf(memory.getMemReplicationBacklog())));
                items.add(new Pair<>("mem_clients_slaves(Redis节点客户端消耗内存/byte)", String.valueOf(memory.getMemClientsSlaves())));
                items.add(new Pair<>("mem_clients_normal(Redis所有常规客户端消耗内存/byte)", String.valueOf(memory.getMemClientsNormal())));
                items.add(new Pair<>("mem_aof_buffer(AOF使用内存)", String.valueOf(memory.getMemAofBuffer())));
                items.add(new Pair<>("active_defrag_running(活动碎片整理是否处于活动状态:0没有,1正在运行)", String.valueOf(memory.getActiveDefragRunning())));
                items.add(new Pair<>("lazyfree_pending_objects(迟释放的挂起对象:0不存在延迟释放的挂起对象)", String.valueOf(memory.getLazyfreePendingObjects())));
            }
        }
    }


    public void refreshMemoryLineChart(LineChart usedMemoryPeakPerc, LineChart usedMemoryDatasetPerc, LineChart memFragmentationRatio,double prefWidth) {
        double prefHeight = scrollPane.getPrefHeight();
        VBox vBox = new VBox();
        vBox.setPrefHeight(prefHeight- STANDARD_HEIGHT_INTERVAL);
        vBox.setPrefWidth(prefWidth);
        ObservableList<Node> children = vBox.getChildren();
        double lineChartPrefHeight = (prefHeight - STANDARD_HEIGHT_INTERVAL) / 3;
        if (usedMemoryPeakPerc != null){
            TitledPane usedMemoryPeakPercTitledPane = new TitledPane();
            usedMemoryPeakPercTitledPane.setPrefWidth(prefWidth);
            usedMemoryPeakPercTitledPane.setPrefHeight(lineChartPrefHeight);
            usedMemoryPeakPercTitledPane.setCollapsible(false);
            //设置高度
            usedMemoryPeakPerc.setPrefHeight(lineChartPrefHeight);
            usedMemoryPeakPercTitledPane.setContent(usedMemoryPeakPerc);
            children.add(usedMemoryPeakPercTitledPane);
        }
        if (usedMemoryDatasetPerc != null){
            TitledPane usedMemoryDatasetPercTitledPane = new TitledPane();
            usedMemoryDatasetPercTitledPane.setPrefWidth(prefWidth);
            usedMemoryDatasetPercTitledPane.setPrefHeight(lineChartPrefHeight);
            usedMemoryDatasetPercTitledPane.setCollapsible(false);

            usedMemoryDatasetPerc.setPrefHeight(lineChartPrefHeight);
            usedMemoryDatasetPercTitledPane.setContent(usedMemoryDatasetPerc);
            children.add(usedMemoryDatasetPerc);
        }
        if (memFragmentationRatio != null){
            TitledPane memFragmentationRatioTitledPane = new TitledPane();
            memFragmentationRatioTitledPane.setPrefWidth(prefWidth);
            memFragmentationRatioTitledPane.setPrefHeight(lineChartPrefHeight);
            memFragmentationRatioTitledPane.setCollapsible(false);

            memFragmentationRatio.setPrefHeight(lineChartPrefHeight);
            memFragmentationRatioTitledPane.setContent(memFragmentationRatio);
            children.add(memFragmentationRatioTitledPane);
        }
        scrollPane.setContent(vBox);
    }
}
