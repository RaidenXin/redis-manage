package com.raiden.redis.ui.util;

import com.raiden.redis.ui.mode.Record;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 10:51 2022/5/15
 * @Modified By:
 */
public final class RecordStorageUtils {

    private static final Charset UTF_8 = Charset.forName("utf-8");

    /**
     * 刷新并保存历史记录
     * @param records
     * @param path
     */
    public static final void refreshAndSaveRecords(List<Record> records, String path){
        try {
            if (records != null){
                StringBuilder data = new StringBuilder();
                records.stream()
                        .sorted(Comparator.comparing(Record::getName))
                        .forEach(r -> data.append(RecordSerializationUtils.encode(r)));
                URL resource = RecordStorageUtils.class.getResource(path);
                File file = new File(resource.getFile());
                boolean exists = file.exists();
                //如果存在就删除
                if (exists){
                    file.delete();
                }
                //在创建一个就可以清空文件内容了
                file.createNewFile();
                try {
                    Files.write(file.toPath(), data.toString().getBytes(UTF_8), StandardOpenOption.WRITE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * 保存历史记录
     * @param records
     * @param path
     */
    public static final void saveRecords(List<Record> records,String path){
        try {
            if (records != null){
                StringBuilder data = new StringBuilder();
                records.stream()
                        .sorted(Comparator.comparing(Record::getName))
                        .forEach(r -> data.append(RecordSerializationUtils.encode(r)));
                URL resource = RecordStorageUtils.class.getResource(path);
                File file = new File(resource.getFile());
                boolean exists = file.exists();
                //如果不存在就创建一个
                if (!exists){
                    file.createNewFile();
                }
                try {
                    Files.write(file.toPath(), data.toString().getBytes(UTF_8), StandardOpenOption.WRITE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }

    public static final List<Record> getRecords(String path){
        URL resource = RecordStorageUtils.class.getResource(path);
        try {
            File file = new File(resource.getFile());
            if (file.exists()){
                List<String> datas = Files.readAllLines(file.toPath(), UTF_8);
                if (datas != null){
                    return datas.stream().map(RecordSerializationUtils::decoder).sorted(Comparator.comparing(Record::getName)).collect(Collectors.toList());
                }
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
        return Collections.emptyList();
    }
}
