package com.raiden.redis.ui.util;

import javafx.util.Pair;

public class DataUtil {

    public static int getIndexForArray(Pair[] array, String item) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].getKey().equals(item)) {
                return i;
            }
        }
        return -1;
    }
}
