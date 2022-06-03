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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 20:05 2022/5/20
 * @Modified By:
 */
public class RedisStatsInfoController implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger(RedisStatsInfoController.class);


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
    @FXML
    private AnchorPane expiredKeysView;
    @FXML
    private AnchorPane expiredTimeCapReachedCountView;
    @FXML
    private AnchorPane ioThreadedProcessedView;
    @FXML
    private AnchorPane expireCycleCpuMillisecondsView;

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
        //排除为null的
        if (stats == null){
            return;
        }
        //除去秒
        String time = info.getKey().substring(0, 5);
        Pair<String, RedisStats> pair = new Pair<>(time, stats);
        queue.add(pair);
        List<Pair<String, RedisStats>> all = queue.getAll();
        try {
            totalConnectionsReceivedRefresh(all);
            totalCommandsProcessedRefresh(all);
            totalNumberProcessedEventsRefresh(all);
            keyspaceRefresh(all);
            totalNetworkTrafficViewsRefresh(all);
            expiredStalePercView(all);
            expiredKeysView(all);
            expiredTimeCapReachedCountView(all);
            expireCycleCpuMillisecondsView(all);
            ioThreadedProcessedView(all);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            LOGGER.error(e.getMessage(), e);
        }
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
        lineChart.setAnimated(false);
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
        lineChart.setAnimated(false);
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
        lineChart.setAnimated(false);
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
        lineChart.setAnimated(false);
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
        lineChart.setAnimated(false);
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
        view(all, RedisStats::getExpiredStalePerc, this.expiredStalePercView,"百分比", "Key过期比例");
    }

    /**
     * Key过期事件总数
     * @param all
     */
    private void expiredKeysView(List<Pair<String, RedisStats>> all){
        view(all, RedisStats::getExpiredKeys,  this.expiredKeysView,"总数/个", "Key过期事件总数");
    }

    /**
     * Key过期事件总数
     * @param all
     */
    private void expiredTimeCapReachedCountView(List<Pair<String, RedisStats>> all){
        view(all, RedisStats::getExpiredTimeCapReachedCount,  this.expiredTimeCapReachedCountView,"次", "处理Key过期事件超时的次数");
    }

    /**
     * Key过期事件总数
     * @param all
     */
    private void expireCycleCpuMillisecondsView(List<Pair<String, RedisStats>> all){
        view(all, RedisStats::getExpireCycleCpuMilliseconds,  this.expireCycleCpuMillisecondsView,"毫秒", "处理Key过期事件所花费的时间");
    }

    private <T> void view(List<Pair<String, RedisStats>> all, Function<RedisStats, T> function,AnchorPane anchorPane,String label,String name){
        NumberAxis numberAxis = new NumberAxis();
        numberAxis.setLabel(label);
        //服务器查找Key 相关
        LineChart<String, T> lineChart = new LineChart(new CategoryAxis(), numberAxis);
        lineChart.setPrefHeight(prefHeight);
        lineChart.setPrefWidth(prefWidth);
        //在主字典中成功查找到key的次数
        final XYChart.Series<String, T> expiredStalePercSeries = new XYChart.Series<>();
        expiredStalePercSeries.setName(name);
        ObservableList<XYChart.Data<String, T>> instantaneousInputKbpsSeriesData = expiredStalePercSeries.getData();
        for (Pair<String, RedisStats> p : all) {
            String key = p.getKey();
            RedisStats redisStats = p.getValue();
            instantaneousInputKbpsSeriesData.add(new XYChart.Data<>(key, function.apply(redisStats)));
        }
        ObservableList<XYChart.Series<String, T>> data = lineChart.getData();
        data.addAll(expiredStalePercSeries);
        ObservableList<Node> children = anchorPane.getChildren();
        children.clear();
        children.add(lineChart);
    }

    /**
     * IO线程处理事件数
     * @param all
     */
    private void ioThreadedProcessedView(List<Pair<String, RedisStats>> all){
        //服务器接受的连接总数
        final XYChart.Series<String, Number> ioThreadedReadsProcessedSeries = new XYChart.Series<>();
        ioThreadedReadsProcessedSeries.setName("IO线程处理的读取事件数");
        final XYChart.Series<String, Number> ioThreadedWritesProcessedSeries = new XYChart.Series<>();
        ioThreadedWritesProcessedSeries.setName("IO线程处理的写事件数");
        ObservableList<XYChart.Data<String, Number>> ioThreadedReadsProcessedSeriesSeriesData = ioThreadedReadsProcessedSeries.getData();
        ObservableList<XYChart.Data<String, Number>> ioThreadedWritesProcessedSeriesData = ioThreadedWritesProcessedSeries.getData();
        for (Pair<String, RedisStats> p : all) {
            String key = p.getKey();
            RedisStats redisStats = p.getValue();
            ioThreadedReadsProcessedSeriesSeriesData.add(new XYChart.Data<>(key, redisStats.getIoThreadedReadsProcessed()));
            ioThreadedWritesProcessedSeriesData.add(new XYChart.Data<>(key, redisStats.getIoThreadedWritesProcessed()));
        }
        NumberAxis numberAxis = new NumberAxis();
        numberAxis.setLabel("事件数/个");
        LineChart<String, Number> lineChart = new LineChart<>(new CategoryAxis(), numberAxis);
        lineChart.setAnimated(false);
        lineChart.setPrefHeight(prefHeight);
        lineChart.setPrefWidth(prefWidth);
        ObservableList<XYChart.Series<String, Number>> data = lineChart.getData();
        data.addAll(ioThreadedReadsProcessedSeries, ioThreadedWritesProcessedSeries);
        ObservableList<Node> children = this.ioThreadedProcessedView.getChildren();
        children.clear();
        children.add(lineChart);
    }
}
