package com.raiden.redis.ui.dao;

import com.raiden.redis.ui.mode.Record;
import com.raiden.redis.ui.util.RecordStorageUtils;

import java.util.List;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 11:04 2022/5/15
 * @Modified By:
 */
public class RecordDao {

    private String path;

    public RecordDao(String path){
        this.path = path;
    }

    /**
     * 刷新并保存记录
     * @param records
     */
    public void refreshAndSaveRecords(List<Record> records){
        RecordStorageUtils.refreshAndSaveRecords(records, path);
    }

    /**
     * 保存记录
     * @param records
     */
    public void saveRecords(List<Record> records){
        RecordStorageUtils.saveRecords(records, path);
    }

    /**
     * 获取记录
     * @return
     */
    public List<Record> getRecords(){
        return RecordStorageUtils.getRecords(path);
    }
}
