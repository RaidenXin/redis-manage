package com.raiden.redis.ui.controller;

import com.raiden.redis.core.common.TaskProcessingCenter;
import com.raiden.redis.core.task.Task;
import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.net.model.RedisCpuInfo;
import com.raiden.redis.net.model.RedisMemoryInfo;
import com.raiden.redis.net.model.RedisNodeInfo;
import com.raiden.redis.net.model.RedisStats;
import com.raiden.redis.ui.controller.memory.RedisMemoryDataViewController;
import com.raiden.redis.ui.controller.persistence.RedisPersistenceDataViewController;
import com.raiden.redis.ui.controller.server.RedisServerInfoController;
import com.raiden.redis.ui.controller.stats.RedisStatsInfoController;
import com.raiden.redis.ui.mode.RedisNode;
import com.raiden.redis.ui.queue.CircularFifoQueue;
import com.raiden.redis.ui.util.AlertUtil;
import com.raiden.redis.ui.util.FXMLLoaderUtils;
import com.raiden.redis.ui.view.View;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 19:20 2022/5/18
 * @Modified By: 监控数据核心类 主要负责监控模块的数据刷新
 */
public class RedisMonitoringInfoController implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger(RedisMonitoringInfoController.class);
    //标准高度间隔
    private static final double STANDARD_HEIGHT_INTERVAL = 45D;

    private static final String QPS = "qps";
    private static final String CPU = "cpu";
    private static final String TIME_SHARING_TRAFFIC = "timeSharingTraffic";
    private static final String SERVER = "server";
    private static final String STATS = "stats";
    private static final String MEMORY = "memory";
    private static final String MEMORY_USAGE = "memoryUsage";
    private static final String PERSISTENCE = "persistence";

    @FXML
    private TabPane tabPane;

    private RedisNode redisNode;

    private RedisServerInfoController redisServerInfoController;
    private RedisStatsInfoController redisStatsInfoController;
    private RedisMemoryDataViewController redisMemoryDataViewController;
    private RedisPersistenceDataViewController redisPersistenceDataViewController;

    private AtomicBoolean isInit;
    private AtomicBoolean isShutDown;

    private CircularFifoQueue<Pair<String, RedisNodeInfo>> queue;

    private double prefHeight;
    private double prefWidth;

    private Map<String, BiFunction<Tab, List<Pair<String, RedisNodeInfo>>, View>> biFunctionMapping;


    public RedisMonitoringInfoController(){
        this.isInit = new AtomicBoolean(false);
        this.isShutDown = new AtomicBoolean(false);
        this.queue = new CircularFifoQueue<>(600);
        this.biFunctionMapping = new HashMap<String, BiFunction<Tab, List<Pair<String, RedisNodeInfo>>, View>>(){{
            // QPS折线图
            put(QPS, RedisMonitoringInfoController.this::refreshRedisQPS);
            // CPU占用率折线图
            put(CPU, RedisMonitoringInfoController.this::refreshRedisCpuUsage);
            // 瞬时网络流量折线图
            put(TIME_SHARING_TRAFFIC, RedisMonitoringInfoController.this::refreshRedisTimeSharingTraffic);
            // 内存占用率折线图
            put(MEMORY_USAGE, RedisMonitoringInfoController.this::refreshRedisMemoryLineChart);
            // 持久化
            put(PERSISTENCE, RedisMonitoringInfoController.this::refreshRedisPersistenceInfo);
        }};
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.prefHeight = tabPane.getPrefHeight() - STANDARD_HEIGHT_INTERVAL;
        this.prefWidth = tabPane.getPrefWidth();
    }


    public void init(RedisNode redisNode){
        if (redisNode == null){
            return;
        }
        this.redisNode = redisNode;
        String hostAndPort = redisNode.getHostAndPort();
        try {
            //这个接口要获取数据要先执行
            refreshQpsAndCpu();
            refreshTabPane();
            isInit.compareAndSet(false, true);
            isShutDown.set(false);
            //设置一个一秒刷新的任务
            TaskProcessingCenter.submit(new RedisMonitoringInfoController.RefreshTask(() -> refreshQpsAndCpu(), hostAndPort, 1, TimeUnit.SECONDS));
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
            }, hostAndPort, 60, TimeUnit.SECONDS));
        }catch (Exception e){
            String error = e.getMessage();
            LOGGER.error(e);
            LOGGER.error(error, e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "加载Redis服务信息错误:" + error);
            alert.showAndWait();
        }
    }

    /**
     * 获取 数据并刷新 qps 和 CPU 因为这个1秒执行一次 所以其他的任务都获取缓存的数据
     * @throws IOException
     */
    public void refreshQpsAndCpu() {
        if (tabPane == null || redisNode == null){
            return;
        }
        List<Tab> tabs = tabPane.getTabs();
        if (tabs != null){
            RedisClient client = redisNode.getRedisClient();
            RedisNodeInfo info = client.info();
            if (info == null){
                return;
            }
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            String time = dtf.format(LocalTime.now());
            queue.add(new Pair<>(time, info));
            List<Pair<String, RedisNodeInfo>> desc = queue.getDesc(90);
            try {
                Stream<View> views = tabs.stream().map(tab -> {
                    BiFunction<Tab, List<Pair<String, RedisNodeInfo>>, View> tabListViewBiFunction = biFunctionMapping.get(tab.getId());
                    if (Objects.isNull(tabListViewBiFunction)) {
                        return null;
                    }
                    return tabListViewBiFunction.apply(tab, desc);
                });
                Platform.runLater(() -> views.filter(Objects::nonNull).forEach(View::show));
            }catch (Exception e){
                LOGGER.error(e.getMessage(), e);
                AlertUtil.error("加载Redis服务信息错误:", e);
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
                        refreshRedisMemoryInfo(tab, last);
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
    public View refreshRedisQPS(Tab tab, List<Pair<String, RedisNodeInfo>> redisNodeInfos) {

        //服务器接受的连接总数
        final XYChart.Series<String, Integer> instantaneousOpsPerSecSeries = new XYChart.Series<>();
        instantaneousOpsPerSecSeries.setName("QPS");
        ObservableList<XYChart.Data<String, Integer>> instantaneousOpsPerSecSeriesData = instantaneousOpsPerSecSeries.getData();
        //因为是倒序 所以要从后面开始遍历
        int size = redisNodeInfos.size();
        for (Pair<String, RedisNodeInfo> redisNodeInfo : redisNodeInfos){
            RedisNodeInfo info = redisNodeInfo.getValue();
            String time = redisNodeInfo.getKey();
            RedisStats stats = info.getStats();
            if (stats == null){
                return null;
            }
            instantaneousOpsPerSecSeriesData.add(new XYChart.Data<>(time, stats.getInstantaneousOpsPerSec()));
        }
        NumberAxis numberAxis = new NumberAxis();
        numberAxis.setLabel("处理命令数(个/秒)");
        LineChart lineChart = new LineChart(new CategoryAxis(), numberAxis);
        lineChart.setAnimated(false);
        lineChart.setPrefHeight(this.prefHeight);
        lineChart.setPrefWidth(this.prefWidth * ((size >> 4) + 1));
        ObservableList<XYChart.Series<String, Integer>> data = lineChart.getData();
        data.add(instantaneousOpsPerSecSeries);
        return () ->{
            ScrollPane content = (ScrollPane) tab.getContent();
            content.setContent(lineChart);
        };
    }

    /**
     * 刷新CPU使用率数据
     * @param tab
     * @param redisNodeInfos
     * @throws IOException
     */
    public View refreshRedisCpuUsage(Tab tab,List<Pair<String, RedisNodeInfo>> redisNodeInfos) {
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
        //第一个前面没有了无法比较 所以直接从第二个开始
        int size = redisNodeInfos.size();
        for (int i = 1; i < size; i++){
            Pair<String, RedisNodeInfo> info = redisNodeInfos.get(i);
            String time = info.getKey();
            RedisNodeInfo redisNodeInfo = info.getValue();
            Pair<String, RedisNodeInfo> beforeInfo = redisNodeInfos.get(i - 1);
            RedisNodeInfo beforeRedisNodeInfo = beforeInfo.getValue();
            long beforeTimeStamp = beforeRedisNodeInfo.getTimeStamp();
            long nowTimeStamp = redisNodeInfo.getTimeStamp();
            RedisCpuInfo beforeCpu = beforeRedisNodeInfo.getCpu();
            RedisCpuInfo cpu = redisNodeInfo.getCpu();
            if (beforeCpu == null || cpu == null){
                return null;
            }
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
        double prefWidth = this.prefWidth * ((size >> 4) + 1);
        double prefHeight = this.prefHeight / 3;
        String lebel = "CPU占用率(%)";
        LineChart usedCpu = createLineChart(lebel, prefHeight, prefWidth, usedCpuSysSeries, usedCpuUserSeries);
        LineChart mainThread = createLineChart(lebel, prefHeight, prefWidth, usedCpuSysMainThreadSeries, usedCpuUserMainThreadSeries);
        LineChart backgroundProcess = createLineChart(lebel, prefHeight, prefWidth, usedCpuSysChildrenSeries, usedCpuUserChildrenSeries);
        VBox vBox = new VBox();
        vBox.setPrefHeight(tabPane.getPrefHeight());
        vBox.setPrefWidth(prefWidth);
        ObservableList<Node> children = vBox.getChildren();
        children.addAll(usedCpu, backgroundProcess, mainThread);
        return () ->{
            ScrollPane content = (ScrollPane) tab.getContent();
            content.setContent(vBox);
        };
    }

    /**
     * 刷新 redis 内存数据折线图
     * @param tab
     * @param redisNodeInfos
     * @return
     */
    private View refreshRedisMemoryLineChart(Tab tab, List<Pair<String, RedisNodeInfo>> redisNodeInfos) {
        XYChart.Series<String,Number> usedMemoryPeakPercSeries = new XYChart.Series<>();
        ObservableList<XYChart.Data<String, Number>> usedMemoryPeakPercSeriesData = usedMemoryPeakPercSeries.getData();
        usedMemoryPeakPercSeries.setName("使用内存达到峰值内存的百分比");


        XYChart.Series<String,Number> usedMemoryDatasetPercSeries = new XYChart.Series<>();
        ObservableList<XYChart.Data<String, Number>> usedMemoryDatasetPercSeriesData = usedMemoryDatasetPercSeries.getData();
        usedMemoryDatasetPercSeries.setName("数据占用内存的百分比");


        XYChart.Series<String,Number> memFragmentationRatioSeries = new XYChart.Series<>();
        ObservableList<XYChart.Data<String, Number>> memFragmentationRatioSeriesData = memFragmentationRatioSeries.getData();
        memFragmentationRatioSeries.setName("内存碎片率");

        int size = redisNodeInfos.size();
        for (Pair<String, RedisNodeInfo> info : redisNodeInfos){
            String time = info.getKey();
            RedisNodeInfo redisNodeInfo = info.getValue();
            RedisMemoryInfo memory = redisNodeInfo.getMemory();
            if (memory == null){
                return null;
            }
            //使用内存与峰值内存的百分比(used_memory / used_memory_peak) *100%
            String usedMemoryPeakPerc = memory.getUsedMemoryPeakPerc();
            if (StringUtils.isNotBlank(usedMemoryPeakPerc)) {
                double value = Double.valueOf(usedMemoryPeakPerc.substring(0, usedMemoryPeakPerc.length() - 1));
                usedMemoryPeakPercSeriesData.add(new XYChart.Data<>(time, value));
            }

            //数据占用的内存大小百分比,(used_memory_dataset / (used_memory - used_memory_startup))*100%
            String usedMemoryDatasetPerc = memory.getUsedMemoryDatasetPerc();
            if (StringUtils.isNotBlank(usedMemoryDatasetPerc)) {
                double value = Double.valueOf(usedMemoryDatasetPerc.substring(0, usedMemoryDatasetPerc.length() - 1));
                usedMemoryDatasetPercSeriesData.add(new XYChart.Data<>(time, value));
            }

            memFragmentationRatioSeriesData.add(new XYChart.Data<>(time, memory.getMemFragmentationRatio()));

        }
        double prefWidth = this.prefWidth * ((size >> 4) + 1);
        double prefHeight = this.prefHeight / 3;
        String label = "占用率(%)";
        LineChart usedMemoryPeakPerc = createLineChart(label, prefHeight, prefWidth, usedMemoryPeakPercSeries);
        LineChart usedMemoryDatasetPerc = createLineChart(label, prefHeight, prefWidth, usedMemoryDatasetPercSeries);
        LineChart memFragmentationRatio = createLineChart(label, prefHeight, prefWidth, memFragmentationRatioSeries);
        VBox vBox = new VBox();
        vBox.setPrefHeight(tabPane.getPrefHeight());
        vBox.setPrefWidth(prefWidth);
        ObservableList<Node> children = vBox.getChildren();
        children.addAll(usedMemoryPeakPerc, usedMemoryDatasetPerc, memFragmentationRatio);
        return () ->{
            ScrollPane content = (ScrollPane) tab.getContent();
            content.setContent(vBox);
        };
    }

    /**
     * 创建一个折线图
     * @param label
     * @param prefHeight
     * @param prefWidth
     * @param series
     * @return
     */
    private LineChart createLineChart(String label, double prefHeight, double prefWidth, XYChart.Series<String, Number>... series) {
        NumberAxis numberAxis = new NumberAxis();
        numberAxis.setLabel(label);
        LineChart<String, Number> lineChart = new LineChart<>(new CategoryAxis(), numberAxis);
        lineChart.setAnimated(false);
        if (prefHeight != 0){
            lineChart.setPrefHeight(prefHeight);
        }
        if (prefWidth != 0){
            lineChart.setPrefWidth(prefWidth);
        }
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
    public View refreshRedisTimeSharingTraffic(Tab tab,List<Pair<String, RedisNodeInfo>> redisNodeInfos) {

        //每秒输出量，单位是kb/s
        final XYChart.Series<String, Number> instantaneousOutputKbpsSeries = new XYChart.Series<>();
        instantaneousOutputKbpsSeries.setName("输出量");
        //每秒输入量，单位是kb/s
        final XYChart.Series<String, Number> instantaneousInputKbpsSeries = new XYChart.Series<>();
        instantaneousInputKbpsSeries.setName("输入量");
        ObservableList<XYChart.Data<String, Number>> instantaneousOutputKbpsSeriesData = instantaneousOutputKbpsSeries.getData();
        ObservableList<XYChart.Data<String, Number>> instantaneousInputKbpsSeriesData = instantaneousInputKbpsSeries.getData();
        //因为是倒序 所以要从后面开始遍历
        int size = redisNodeInfos.size();
        for (Pair<String, RedisNodeInfo> info : redisNodeInfos){
            RedisNodeInfo redisNodeInfo = info.getValue();
            String time = info.getKey();
            RedisStats redisStats = redisNodeInfo.getStats();
            if (redisStats == null){
                return null;
            }
            instantaneousOutputKbpsSeriesData.add(new XYChart.Data<>(time, redisStats.getInstantaneousOutputKbps()));
            instantaneousInputKbpsSeriesData.add(new XYChart.Data<>(time, redisStats.getInstantaneousInputKbps()));
        }
        NumberAxis numberAxis = new NumberAxis();
        numberAxis.setLabel("IO流量(kb/s)");
        LineChart<String, Number> lineChart = new LineChart<>(new CategoryAxis(), numberAxis);
        lineChart.setAnimated(false);
        lineChart.setPrefHeight(this.prefHeight);
        lineChart.setPrefWidth(this.prefWidth * ((size >> 4) + 1));
        ObservableList<XYChart.Series<String, Number>> data = lineChart.getData();
        data.addAll(instantaneousOutputKbpsSeries, instantaneousInputKbpsSeries);
        return () ->{
            ScrollPane content = (ScrollPane) tab.getContent();
            content.setContent(lineChart);
        };
    }

    /**
     * 刷新服务信息
     * @param server
     * @param info
     * @throws IOException
     */
    public void refreshRedisServerInfo(Tab server,Pair<String, RedisNodeInfo> info) throws IOException {
        if (!isInit.get()){
            FXMLLoader loader = FXMLLoaderUtils.getFXMLLoader("redis_server_info_view.fxml");
            AnchorPane redisServerInfoView = loader.load();
            server.setContent(redisServerInfoView);
            this.redisServerInfoController = loader.getController();
        }
        if (this.redisServerInfoController != null && info != null){
            this.redisServerInfoController.refresh(info.getValue());
        }
    }

    /**
     * 刷新统计数据
     * @param stats
     * @param info
     * @throws IOException
     */
    public void refreshRedisStatsInfo(Tab stats, Pair<String, RedisNodeInfo> info)throws IOException{
        if (!isInit.get()){
            FXMLLoader loader = FXMLLoaderUtils.getFXMLLoader("redis_stats_info_view.fxml");
            AnchorPane redisStatsInfoView = loader.load();
            this.redisStatsInfoController = loader.getController();
            ScrollPane scrollPane = (ScrollPane) stats.getContent();
            scrollPane.setContent(redisStatsInfoView);
        }
        if ( this.redisStatsInfoController != null && info != null){
            this.redisStatsInfoController.refresh(info);
        }
    }

    /**
     * 刷新内存信息
     * @param memory
     * @param info
     * @throws IOException
     */
    public void refreshRedisMemoryInfo(Tab memory,Pair<String, RedisNodeInfo> info)throws IOException{
        if (!isInit.get() && redisMemoryDataViewController == null){
            FXMLLoader loader = FXMLLoaderUtils.getFXMLLoader("memory/redis_memory_data_view.fxml");
            AnchorPane view = loader.load();
            this.redisMemoryDataViewController = loader.getController();
            memory.setContent(view);
        }
        if ( this.redisMemoryDataViewController != null && info != null){
            this.redisMemoryDataViewController.refresh(info);
        }
    }


    /**
     * 刷新持久化信息
     * @param persistence
     * @param redisNodeInfos
     * @return
     */
    public View refreshRedisPersistenceInfo(Tab persistence, List<Pair<String, RedisNodeInfo>> redisNodeInfos) {
        if (!isInit.get() && redisPersistenceDataViewController == null) {
            try {
                FXMLLoader loader = FXMLLoaderUtils.getFXMLLoader("persistence/redis_persistence_data_view.fxml");
                AnchorPane view = loader.load();
                this.redisPersistenceDataViewController = loader.getController();
                persistence.setContent(view);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
                AlertUtil.error("加载Fxml错误:", e);
            }
        }
        return () -> {
            if (redisPersistenceDataViewController != null && redisNodeInfos != null && !redisNodeInfos.isEmpty()) {
                redisPersistenceDataViewController.refresh(redisNodeInfos.get(0));
            }
        };
    }

    /**
     * 关闭方法
     */
    public void shutDown(){
        if (isInit.compareAndSet(true, false)){
            //清理掉旧的数据
            this.queue.clear();
            //将控制器设置为已关闭
            isShutDown.set(true);
        }
    }


    public class RefreshTask implements Task {

        private String hostAndPort;
        private int delayTime;
        private TimeUnit unit;
        private Runnable task;

        public RefreshTask(Runnable task,String hostAndPort,int delayTime,TimeUnit unit){
            this.delayTime = delayTime;
            this.unit = unit;
            this.task = task;
            this.hostAndPort = hostAndPort;
        }

        @Override
        public void run() {
            //如果控制器已经停止 就停止数据刷新任务
            if (isShutDown.get()){
                return;
            }
            Platform.runLater(() -> {
                task.run();
            });
            TaskProcessingCenter.submit(new RefreshTask(task, hostAndPort, delayTime, unit));
        }

        @Override
        public long getDelayTime() {
            return unit.toMillis(delayTime);
        }
    }
}
