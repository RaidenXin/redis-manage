package com.raiden.redis.ui.util;

import javafx.scene.control.cell.PropertyValueFactory;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 13:43 2022/6/3
 * @Modified By:
 */
public final class PropertyValueUtil {

    private static final String KEY = "key";

    private static final String VALUE = "value";

    private static volatile PropertyValueFactory<String, String> pairKeyPropertyValueFactory;
    private static volatile PropertyValueFactory<String, String> pairValuePropertyValueFactory;

    public static PropertyValueFactory getPairKeyPropertyValueFactory(){
        if (pairKeyPropertyValueFactory == null){
            synchronized (PropertyValueUtil.class){
                pairKeyPropertyValueFactory = new PropertyValueFactory<>(KEY);
            }
        }
        return pairKeyPropertyValueFactory;
    }

    public static PropertyValueFactory getPairValuePropertyValueFactory(){
        if (pairValuePropertyValueFactory == null){
            synchronized (PropertyValueUtil.class){
                pairValuePropertyValueFactory = new PropertyValueFactory<>(VALUE);
            }
        }
        return pairValuePropertyValueFactory;
    }
}
