package com.raiden.redis.ui.controller;

import com.raiden.redis.core.common.TaskProcessingCenter;
import com.raiden.redis.core.task.Task;
import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.net.model.RedisCpuInfo;
import com.raiden.redis.net.model.RedisNodeInfo;
import com.raiden.redis.net.model.RedisStats;
import com.raiden.redis.ui.Window;
import com.raiden.redis.ui.mode.RedisNode;
import com.raiden.redis.ui.queue.CircularFifoQueue;
import com.raiden.redis.ui.util.FXMLLoaderUtils;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 19:20 2022/5/18
 * @Modified By:
 */
public class RedisMonitoringInfoController implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger(Window.class);
    //标准高度间隔
    private static final double STANDARD_HEIGHT_INTERVAL = 45D;

    private static final String QPS = "qps";
    private static final String CPU = "cpu";
    private static final String TIME_SHARING_TRAFFIC = "timeSharingTraffic";
    private static final String SERVER = "server";
    private static final String STATS = "stats";
    private static final String MEMORY = "memory";
    private static final String PERSISTENCE = "persistence";

    @FXML
    private TabPane tabPane;

    private RedisNode redisNode;

    private RedisServerInfoController redisServerInfoController;
    private RedisStatsInfoController redisStatsInfoController;

    private AtomicBoolean isInit;

    private CircularFifoQueue<Pair<String, RedisNodeInfo>> queue;

    private double prefHeight;
    private double prefWidth;


    public RedisMonitoringInfoController(){
        this.isInit = new AtomicBoolean(false);
        this.queue = new CircularFifoQueue<>(600);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.prefHeight = tabPane.getPrefHeight();
        this.prefWidth = tabPane.getPrefWidth();
    }


    public void init(RedisNode redisNode){
        if (redisNode == null){
            return;
        }
        this.redisNode = redisNode;
        try {
            //这个接口要获取数据要先执行
            refreshQpsAndCpu();
            refreshTabPane();
            isInit.compareAndSet(false, true);
            //设置一个一秒刷新的任务
            TaskProcessingCenter.submit(new RedisMonitoringInfoController.RefreshTask(() -> {
                try {
                    refreshQpsAndCpu();
                } catch (IOException e) {
                    String error = e.getMessage();
                    LOGGER.error(error, e);
                    Alert alert = new Alert(Alert.AlertType.ERROR, "加载Redis服务信息错误:" + error);
                    alert.showAndWait();
                }
            }, 1, TimeUnit.SECONDS));
            //设置一个60秒刷新的任务
            TaskProcessingCenter.submit(new RedisMonitoringInfoController.RefreshTask(() -> {
                try {
                    refreshTabPane();
                } catch (IOException e) {
                    String error = e.getMessage();
                    LOGGER.error(error, e);
                    Alert alert = new Alert(Alert.AlertType.ERROR, "加载Redis服务信息错误:" + error);
                    alert.showAndWait();
                }
            }, 60, TimeUnit.SECONDS));
        }catch (Exception e){
            String error = e.getMessage();
            LOGGER.error(error, e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "加载Redis服务信息错误:" + error);
            alert.showAndWait();
        }
    }

    /**
     * 获取 数据并刷新 qps 和 CPU 因为这个1秒执行一次 所以其他的任务都获取缓存的数据
     * @throws IOException
     */
    public void refreshQpsAndCpu() throws IOException {
        if (tabPane == null || redisNode == null){
            return;
        }
        List<Tab> tabs = tabPane.getTabs();
        if (tabs != null){
            RedisClient client = redisNode.getRedisClient();
            RedisNodeInfo info = client.info();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            String time = dtf.format(LocalTime.now());
            queue.add(new Pair<>(time, info));
            List<Pair<String, RedisNodeInfo>> desc = queue.getDesc(35);
            for (Tab tab : tabs){
                switch (tab.getId()){
                    case QPS://服务
                        refreshRedisQPS(tab, desc);
                        break;
                    case CPU://服务
                        refreshRedisCpuUsage(tab, desc);
                        break;
                    case TIME_SHARING_TRAFFIC://服务
                        refreshRedisTimeSharingTraffic(tab, desc);
                        break;
                }
            }
        }
    }

    public void refreshTabPane() throws IOException {
        if (tabPane == null || redisNode == null){
            return;
        }
        List<Tab> tabs = tabPane.getTabs();
        if (tabs != null){
            Pair<String, RedisNodeInfo> last = queue.getLast();
            for (Tab tab : tabs){
                switch (tab.getId()){
                    case SERVER://服务
                        refreshRedisServerInfo(tab, last);
                        break;
                    case STATS://统计数据
                        refreshRedisStatsInfo(tab, last);
                        break;
                    case MEMORY://内存
//                        refreshRedisMemoryInfo(tab, info);
                        break;
                    case PERSISTENCE://持久化
//                        refreshRedisPersistenceInfo(tab, info);
                        break;
                }
            }
        }
    }

    /**
     * 刷新QPS数据
     * @param tab
     * @param redisNodeInfos
     * @throws IOException
     */
    public void refreshRedisQPS(Tab tab,List<Pair<String, RedisNodeInfo>> redisNodeInfos) throws IOException {

        //服务器接受的连接总数
        final XYChart.Series<String, Integer> instantaneousOpsPerSecSeries = new XYChart.Series<>();
        instantaneousOpsPerSecSeries.setName("QPS");
        ObservableList<XYChart.Data<String, Integer>> instantaneousOpsPerSecSeriesData = instantaneousOpsPerSecSeries.getData();
        //因为是倒序 所以要从后面开始遍历
        for (int i = redisNodeInfos.size() - 1; i > -1; i--){
            Pair<String, RedisNodeInfo> redisNodeInfo = redisNodeInfos.get(i);
            RedisNodeInfo info = redisNodeInfo.getValue();
            String time = redisNodeInfo.getKey();
            RedisStats stats = info.getStats();
            instantaneousOpsPerSecSeriesData.add(new XYChart.Data<>(time, stats.getInstantaneousOpsPerSec()));
        }
        NumberAxis numberAxis = new NumberAxis();
        numberAxis.setLabel("处理命令数(个/秒)");
        LineChart lineChart = new LineChart(new CategoryAxis(), numberAxis);
        lineChart.setPrefHeight(prefHeight - STANDARD_HEIGHT_INTERVAL);
        lineChart.setPrefWidth(this.prefWidth * 2);
        ObservableList<XYChart.Series<String, Integer>> data = lineChart.getData();
        data.add(instantaneousOpsPerSecSeries);
        ScrollPane content = (ScrollPane) tab.getContent();
        content.setContent(lineChart);
    }

    /**
     * 刷新CPU使用率数据
     * @param tab
     * @param redisNodeInfos
     * @throws IOException
     */
    public void refreshRedisCpuUsage(Tab tab,List<Pair<String, RedisNodeInfo>> redisNodeInfos) throws IOException {
        /**
         * redis进程单cpu的消耗率可以通过如下公式计算:
         * ((used_cpu_sys_now-used_cpu_sys_before)/(now-before))*100
         * used_cpu_sys_now : now时间点的used_cpu_sys值
         * used_cpu_sys_before : before时间点的used_cpu_sys值
         */
        XYChart.Series<String,Number> usedCpuSysSeries = new XYChart.Series<>();
        ObservableList<XYChart.Data<String, Number>> usedCpuSysSeriesData = usedCpuSysSeries.getData();
        usedCpuSysSeries.setName("主进程系统CPU使用率");

        XYChart.Series<String,Number> usedCpuUserSeries = new XYChart.Series<>();
        ObservableList<XYChart.Data<String, Number>> usedCpuUserSeriesData = usedCpuUserSeries.getData();
        usedCpuUserSeries.setName("主进程用户CPU使用率");

        XYChart.Series<String,Number> usedCpuSysMainThreadSeries = new XYChart.Series<>();
        ObservableList<XYChart.Data<String, Number>> usedCpuSysMainThreadSeriesData = usedCpuSysMainThreadSeries.getData();
        usedCpuSysMainThreadSeries.setName("Redis主线程系统CPU使用率");

        XYChart.Series<String,Number> usedCpuUserMainThreadSeries = new XYChart.Series<>();
        ObservableList<XYChart.Data<String, Number>> usedCpuUserMainThreadSeriesData = usedCpuUserMainThreadSeries.getData();
        usedCpuUserMainThreadSeries.setName("Redis主线程用户CPU使用率");

        XYChart.Series<String,Number> usedCpuSysChildrenSeries = new XYChart.Series<>();
        ObservableList<XYChart.Data<String, Number>> usedCpuSysChildrenSeriesData = usedCpuSysChildrenSeries.getData();
        usedCpuSysChildrenSeries.setName("后台进程系统CPU使用率");

        XYChart.Series<String,Number> usedCpuUserChildrenSeries = new XYChart.Series<>();
        ObservableList<XYChart.Data<String, Number>> usedCpuUserChildrenSeriesData = usedCpuUserChildrenSeries.getData();
        usedCpuUserChildrenSeries.setName("后台进程用户CPU使用率");
        //因为是倒序 所以要从后面开始遍历 第一个前面没有了无法比较 所以直接从第二个开始
        for (int i = redisNodeInfos.size() - 2; i > -1; i--){
            Pair<String, RedisNodeInfo> info = redisNodeInfos.get(i);
            String time = info.getKey();
            RedisNodeInfo redisNodeInfo = info.getValue();
            Pair<String, RedisNodeInfo> beforeInfo = redisNodeInfos.get(i + 1);
            RedisNodeInfo beforeRedisNodeInfo = beforeInfo.getValue();
            long beforeTimeStamp = beforeRedisNodeInfo.getTimeStamp();
            long nowTimeStamp = redisNodeInfo.getTimeStamp();
            RedisCpuInfo beforeCpu = beforeRedisNodeInfo.getCpu();
            RedisCpuInfo cpu = redisNodeInfo.getCpu();
            double usedCpuSys = usageRate(cpu.getUsedCpuSys(), beforeCpu.getUsedCpuSys(), nowTimeStamp, beforeTimeStamp);
            double usedCpuUser = usageRate(cpu.getUsedCpuUser(), beforeCpu.getUsedCpuUser(), nowTimeStamp, beforeTimeStamp);
            usedCpuSysSeriesData.add(new XYChart.Data<>(time, usedCpuSys));
            usedCpuUserSeriesData.add(new XYChart.Data<>(time, usedCpuUser));

            //Redis主线程
            double usedCpuSysMainThread = usageRate(cpu.getUsedCpuSysMainThread(), beforeCpu.getUsedCpuSysMainThread(), nowTimeStamp, beforeTimeStamp);
            double usedCpuUserMainThread = usageRate(cpu.getUsedCpuUserMainThread(), beforeCpu.getUsedCpuUserMainThread(), nowTimeStamp, beforeTimeStamp);
            usedCpuSysMainThreadSeriesData.add(new XYChart.Data<>(time, usedCpuSysMainThread));
            usedCpuUserMainThreadSeriesData.add(new XYChart.Data<>(time, usedCpuUserMainThread));

            double usedCpuSysChildren = usageRate(cpu.getUsedCpuSysChildren(), beforeCpu.getUsedCpuSysChildren(), nowTimeStamp, beforeTimeStamp);
            double usedCpuUserChildren = usageRate(cpu.getUsedCpuUserChildren(), beforeCpu.getUsedCpuUserChildren(), nowTimeStamp, beforeTimeStamp);
            usedCpuSysChildrenSeriesData.add(new XYChart.Data<>(time, usedCpuSysChildren));
            usedCpuUserChildrenSeriesData.add(new XYChart.Data<>(time, usedCpuUserChildren));

        }
        double prefHeight = this.prefHeight / 3 - 15;
        LineChart usedCpu = createLineChart(prefHeight, usedCpuSysSeries, usedCpuUserSeries);
        LineChart mainThread = createLineChart(prefHeight, usedCpuSysMainThreadSeries, usedCpuUserMainThreadSeries);
        LineChart backgroundProcess = createLineChart(prefHeight, usedCpuSysChildrenSeries, usedCpuUserChildrenSeries);
        VBox vBox = new VBox();
        vBox.setPrefWidth(this.prefWidth * 2);
        vBox.setPrefHeight(this.prefHeight);
        ObservableList<Node> children = vBox.getChildren();
        children.addAll(usedCpu, backgroundProcess, mainThread);
        ScrollPane content = (ScrollPane) tab.getContent();
        content.setContent(vBox);
    }

    private LineChart createLineChart(double prefHeight,XYChart.Series<String,Number>... series){
        NumberAxis numberAxis = new NumberAxis();
        numberAxis.setLabel("CPU占用率(%)");
        LineChart<String, Number> lineChart = new LineChart<>(new CategoryAxis(), numberAxis);
        lineChart.setPrefHeight(prefHeight);
        lineChart.setPrefWidth(this.prefWidth * 2);
        ObservableList<XYChart.Series<String, Number>> data = lineChart.getData();
        data.addAll(series);
        return lineChart;
    }

    private double usageRate(double now,double before,long nowTimeStamp,long beforeTimeStamp){
        return (now - before) / (nowTimeStamp - beforeTimeStamp) * 100D;
    }

    /**
     * 刷新 Redis 网络Kps 数据
     * @param tab
     * @param redisNodeInfos
     * @throws IOException
     */
    public void refreshRedisTimeSharingTraffic(Tab tab,List<Pair<String, RedisNodeInfo>> redisNodeInfos) throws IOException {

        //每秒输出量，单位是kb/s
        final XYChart.Series<String, Number> instantaneousOutputKbpsSeries = new XYChart.Series<>();
        instantaneousOutputKbpsSeries.setName("输出量");
        //每秒输入量，单位是kb/s
        final XYChart.Series<String, Number> instantaneousInputKbpsSeries = new XYChart.Series<>();
        instantaneousInputKbpsSeries.setName("输入量");
        ObservableList<XYChart.Data<String, Number>> instantaneousOutputKbpsSeriesData = instantaneousOutputKbpsSeries.getData();
        ObservableList<XYChart.Data<String, Number>> instantaneousInputKbpsSeriesData = instantaneousInputKbpsSeries.getData();
        //因为是倒序 所以要从后面开始遍历
        for (int i = redisNodeInfos.size() - 1; i > -1; i--){
            Pair<String, RedisNodeInfo> info = redisNodeInfos.get(i);
            RedisNodeInfo redisNodeInfo = info.getValue();
            String time = info.getKey();
            RedisStats redisStats = redisNodeInfo.getStats();
            instantaneousOutputKbpsSeriesData.add(new XYChart.Data<>(time, redisStats.getInstantaneousOutputKbps()));
            instantaneousInputKbpsSeriesData.add(new XYChart.Data<>(time, redisStats.getInstantaneousInputKbps()));
        }
        NumberAxis numberAxis = new NumberAxis();
        numberAxis.setLabel("IO流量(kb/s)");
        LineChart<String, Number> lineChart = new LineChart<>(new CategoryAxis(), numberAxis);
        lineChart.setPrefHeight(this.prefHeight - STANDARD_HEIGHT_INTERVAL);
        lineChart.setPrefWidth(prefWidth * 2);
        ObservableList<XYChart.Series<String, Number>> data = lineChart.getData();
        data.addAll(instantaneousOutputKbpsSeries, instantaneousInputKbpsSeries);
        ScrollPane content = (ScrollPane) tab.getContent();
        content.setContent(lineChart);
    }

    public void refreshRedisServerInfo(Tab server,Pair<String, RedisNodeInfo> info) throws IOException {
        if (!isInit.get()){
            FXMLLoader loader = new FXMLLoader(Window.class.getResource("redis_server_info_view.fxml"));
            AnchorPane redisServerInfoView = loader.load();
            server.setContent(redisServerInfoView);
            this.redisServerInfoController = loader.getController();
        }
        if (redisServerInfoController != null && info != null){
            redisServerInfoController.refresh(info.getValue());
        }
    }

    public void refreshRedisStatsInfo(Tab stats, Pair<String, RedisNodeInfo> info)throws IOException{
        if (!isInit.get()){
            FXMLLoader loader = new FXMLLoader(Window.class.getResource("redis_stats_info_view.fxml"));
            AnchorPane redisStatsInfoView = loader.load();
            this.redisStatsInfoController = loader.getController();
            ScrollPane scrollPane = (ScrollPane) stats.getContent();
            scrollPane.setContent(redisStatsInfoView);
        }
        if (redisStatsInfoController != null && info != null){
            redisStatsInfoController.refresh(info);
        }
    }
    public void refreshRedisMemoryInfo(Tab memory,RedisNodeInfo info){
        if (redisNode == null || redisServerInfoController == null){
            return;
        }
        redisServerInfoController.refresh(info);
    }
    public void refreshRedisPersistenceInfo(Tab persistence,RedisNodeInfo info){
        if (redisNode == null || redisServerInfoController == null){
            return;
        }
        redisServerInfoController.refresh(info);
    }


    public class RefreshTask implements Task {

        private int delayTime;
        private TimeUnit unit;
        private Runnable task;

        public RefreshTask(Runnable task,int delayTime,TimeUnit unit){
            this.delayTime = delayTime;
            this.unit = unit;
            this.task = task;
        }

        @Override
        public void run() {
            Platform.runLater(() -> {
                task.run();
            });
            TaskProcessingCenter.submit(new RefreshTask(task, delayTime, unit));
        }

        @Override
        public long getDelayTime() {
            return unit.toMillis(delayTime);
        }
    }
}
