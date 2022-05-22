package com.raiden.redis.ui.controller;

import com.raiden.redis.net.model.RedisNodeInfo;
import com.raiden.redis.net.model.RedisStats;
import com.raiden.redis.ui.queue.CircularFifoQueue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 20:05 2022/5/20
 * @Modified By:
 */
public class RedisStatsInfoController implements Initializable {
    
    @FXML
    private GridPane gridPane;
    @FXML
    private AnchorPane totalConnectionsReceived;
    @FXML
    private AnchorPane totalCommandsProcessed;
    @FXML
    private AnchorPane totalNumberProcessedEvents;
    @FXML
    private AnchorPane keyspace;
    @FXML
    private AnchorPane totalNetworkTrafficViews;
    @FXML
    private AnchorPane expiredStalePercView;

    private CircularFifoQueue<Pair<String, RedisStats>> queue;
    private double prefHeight;
    private double prefWidth;

    public RedisStatsInfoController() {
        this.queue = new CircularFifoQueue(10);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.prefHeight =(gridPane.getPrefHeight() / 2) + 1;
        this.prefWidth = (gridPane.getPrefWidth() / 5) + 1;
    }

    public void refresh(Pair<String, RedisNodeInfo> info) {
        RedisNodeInfo redisNodeInfo = info.getValue();
        RedisStats stats = redisNodeInfo.getStats();
        String time = info.getKey();
        Pair<String, RedisStats> pair = new Pair<>(time, stats);
        queue.add(pair);
        List<Pair<String, RedisStats>> all = queue.getAll();
        totalConnectionsReceivedRefresh(all);
        totalCommandsProcessedRefresh(all);
        totalNumberProcessedEventsRefresh(all);
        keyspaceRefresh(all);
        totalNetworkTrafficViewsRefresh(all);
        expiredStalePercView(all);
    }

    /**
     * 处理 服务器接受的连接总数 和 由于maxclients限制而拒绝的连接数
     *
     * @param all
     */
    private void totalConnectionsReceivedRefresh(List<Pair<String, RedisStats>> all) {
        //服务器接受的连接总数
        final XYChart.Series<String, Number> totalConnectionsReceivedSeries = new XYChart.Series<>();
        totalConnectionsReceivedSeries.setName("服务器接受的连接总数");
        final XYChart.Series<String, Number> rejectedConnectionsSeries = new XYChart.Series<>();
        rejectedConnectionsSeries.setName("服务器拒绝的连接总数");
        ObservableList<XYChart.Data<String, Number>> totalConnectionsReceivedSeriesData = totalConnectionsReceivedSeries.getData();
        ObservableList<XYChart.Data<String, Number>> rejectedConnectionsSeriesData = rejectedConnectionsSeries.getData();
        for (Pair<String, RedisStats> p : all) {
            String key = p.getKey();
            RedisStats redisStats = p.getValue();
            totalConnectionsReceivedSeriesData.add(new XYChart.Data<>(key, redisStats.getTotalConnectionsReceived()));
            rejectedConnectionsSeriesData.add(new XYChart.Data<>(key, redisStats.getRejectedConnections()));
        }
        NumberAxis numberAxis = new NumberAxis();
        numberAxis.setLabel("连接数/个");
        LineChart<String, Number> lineChart = new LineChart<>(new CategoryAxis(), numberAxis);
        lineChart.setPrefHeight(prefHeight);
        lineChart.setPrefWidth(prefWidth);
        ObservableList<XYChart.Series<String, Number>> data = lineChart.getData();
        data.addAll(totalConnectionsReceivedSeries, rejectedConnectionsSeries);
        ObservableList<Node> children = this.totalConnectionsReceived.getChildren();
        children.clear();
        children.add(lineChart);
    }

    /**
     * 处理 服务器处理的命令总数
     *
     * @param all
     */
    private void totalCommandsProcessedRefresh(List<Pair<String, RedisStats>> all) {
        NumberAxis numberAxis = new NumberAxis();
        numberAxis.setLabel("命令数/个");
        //服务器处理的命令总数
        LineChart<String, Number> lineChart = new LineChart<>(new CategoryAxis(), numberAxis);
        lineChart.setPrefHeight(prefHeight);
        lineChart.setPrefWidth(prefWidth);
        //服务器处理的命令总数
        final XYChart.Series<String, Number> totalCommandsProcessedSeries = new XYChart.Series<>();
        totalCommandsProcessedSeries.setName("命令总数");
        ObservableList<XYChart.Data<String, Number>> totalConnectionsReceivedSeriesData = totalCommandsProcessedSeries.getData();
        for (Pair<String, RedisStats> p : all) {
            String key = p.getKey();
            RedisStats redisStats = p.getValue();
            totalConnectionsReceivedSeriesData.add(new XYChart.Data<>(key, redisStats.getTotalCommandsProcessed()));
        }
        ObservableList<XYChart.Series<String, Number>> data = lineChart.getData();
        data.add(totalCommandsProcessedSeries);
        ObservableList<Node> children = this.totalCommandsProcessed.getChildren();
        children.clear();
        children.add(lineChart);
    }

    /**
     * 服务器处理的事件总数
     *
     * @param all
     */
    private void totalNumberProcessedEventsRefresh(List<Pair<String, RedisStats>> all) {
        NumberAxis numberAxis = new NumberAxis();
        numberAxis.setLabel("处理事件数/个");
        //服务器处理的事件总数
        LineChart<String, Number> lineChart = new LineChart<>(new CategoryAxis(), numberAxis);
        lineChart.setPrefHeight(prefHeight);
        lineChart.setPrefWidth(prefWidth);
        //服务器处理的读取事件总数
        final XYChart.Series<String, Number> totalReadsProcessedSeries = new XYChart.Series<>();
        totalReadsProcessedSeries.setName("读取事件总数");
        //服务器处理的写入事件总数
        final XYChart.Series<String, Number> totalWritesProcessedSeries = new XYChart.Series<>();
        totalWritesProcessedSeries.setName("写入事件总数");
        ObservableList<XYChart.Data<String, Number>> totalReadsProcessedSeriesData = totalReadsProcessedSeries.getData();
        ObservableList<XYChart.Data<String, Number>> totalWritesProcessedSeriesData = totalWritesProcessedSeries.getData();
        for (Pair<String, RedisStats> p : all) {
            String key = p.getKey();
            RedisStats redisStats = p.getValue();
            totalReadsProcessedSeriesData.add(new XYChart.Data<>(key, redisStats.getTotalReadsProcessed()));
            totalWritesProcessedSeriesData.add(new XYChart.Data<>(key, redisStats.getTotalWritesProcessed()));
        }
        ObservableList<XYChart.Series<String, Number>> data = lineChart.getData();
        data.addAll(totalReadsProcessedSeries, totalWritesProcessedSeries);
        ObservableList<Node> children = this.totalNumberProcessedEvents.getChildren();
        children.clear();
        children.add(lineChart);
    }

    /**
     * 处理 Key查找 相关
     *
     * @param all
     */
    private void keyspaceRefresh(List<Pair<String, RedisStats>> all) {
        NumberAxis numberAxis = new NumberAxis();
        numberAxis.setLabel("查找Key的次数/次");
        //服务器查找Key 相关
        LineChart<String, Number> lineChart = new LineChart<>(new CategoryAxis(), numberAxis);
        lineChart.setPrefHeight(prefHeight);
        lineChart.setPrefWidth(prefWidth);
        //在主字典中成功查找到key的次数
        final XYChart.Series<String, Number> keyspaceHitsSeries = new XYChart.Series<>();
        keyspaceHitsSeries.setName("查找Key成功的次数");
        //在主字典中查找key失败的次数
        final XYChart.Series<String, Number> keyspaceMissesSeries = new XYChart.Series<>();
        keyspaceMissesSeries.setName("查找Key失败的次数");
        ObservableList<XYChart.Data<String, Number>> keyspaceHitsSeriesData = keyspaceHitsSeries.getData();
        ObservableList<XYChart.Data<String, Number>> keyspaceMissesSeriesData = keyspaceMissesSeries.getData();
        for (Pair<String, RedisStats> p : all) {
            String key = p.getKey();
            RedisStats redisStats = p.getValue();
            keyspaceHitsSeriesData.add(new XYChart.Data<>(key, redisStats.getKeyspaceHits()));
            keyspaceMissesSeriesData.add(new XYChart.Data<>(key, redisStats.getKeyspaceMisses()));
        }
        ObservableList<XYChart.Series<String, Number>> data = lineChart.getData();
        data.addAll(keyspaceHitsSeries, keyspaceMissesSeries);
        ObservableList<Node> children = this.keyspace.getChildren();
        children.clear();
        children.add(lineChart);
    }

    /**
     * 网络总流量视图
     *
     * @param all
     */
    private void totalNetworkTrafficViewsRefresh(List<Pair<String, RedisStats>> all) {
        NumberAxis numberAxis = new NumberAxis();
        numberAxis.setLabel("网络总流量/MB");
        //服务器查找Key 相关
        LineChart<String, Number> lineChart = new LineChart<>(new CategoryAxis(), numberAxis);
        lineChart.setPrefHeight(prefHeight);
        lineChart.setPrefWidth(prefWidth);
        //在主字典中成功查找到key的次数
        final XYChart.Series<String, Number> totalNetInputBytesSeries = new XYChart.Series<>();
        totalNetInputBytesSeries.setName("网络总入量");
        //在主字典中查找key失败的次数
        final XYChart.Series<String, Number> totalNetOutputBytesSeries = new XYChart.Series<>();
        totalNetOutputBytesSeries.setName("网络总出量");
        ObservableList<XYChart.Data<String, Number>> totalNetInputBytesSeriesData = totalNetInputBytesSeries.getData();
        ObservableList<XYChart.Data<String, Number>> totalNetOutputBytesSeriesData = totalNetOutputBytesSeries.getData();
        int unit = 1 << 20;
        for (Pair<String, RedisStats> p : all) {
            String key = p.getKey();
            RedisStats redisStats = p.getValue();
            totalNetInputBytesSeriesData.add(new XYChart.Data<>(key, redisStats.getTotalNetInputBytes() / unit));
            totalNetOutputBytesSeriesData.add(new XYChart.Data<>(key, redisStats.getTotalNetOutputBytes() / unit));
        }
        ObservableList<XYChart.Series<String, Number>> data = lineChart.getData();
        data.addAll(totalNetInputBytesSeries, totalNetOutputBytesSeries);
        ObservableList<Node> children = this.totalNetworkTrafficViews.getChildren();
        children.clear();
        children.add(lineChart);
    }

    /**
     * Key过期的比例
     * @param all
     */
    private void expiredStalePercView(List<Pair<String, RedisStats>> all){
        NumberAxis numberAxis = new NumberAxis();
        numberAxis.setLabel("百分比");
        //服务器查找Key 相关
        LineChart<String, Number> lineChart = new LineChart(new CategoryAxis(), numberAxis);
        lineChart.setPrefHeight(prefHeight);
        lineChart.setPrefWidth(prefWidth);
        //在主字典中成功查找到key的次数
        final XYChart.Series<String, Number> expiredStalePercSeries = new XYChart.Series<>();
        expiredStalePercSeries.setName("Key过期比例");
        ObservableList<XYChart.Data<String, Number>> instantaneousInputKbpsSeriesData = expiredStalePercSeries.getData();
        for (Pair<String, RedisStats> p : all) {
            String key = p.getKey();
            RedisStats redisStats = p.getValue();
            instantaneousInputKbpsSeriesData.add(new XYChart.Data<>(key, redisStats.getExpiredStalePerc() ));
        }
        ObservableList<XYChart.Series<String, Number>> data = lineChart.getData();
        data.addAll(expiredStalePercSeries);
        ObservableList<Node> children = this.expiredStalePercView.getChildren();
        children.clear();
        children.add(lineChart);
    }
}
