package edu.scnu.lims.util;

import com.google.gson.Gson;

/**
 * @author ZhuangJieYing
 */
public class GsonUtils {
    private static Gson gson = new Gson();

    private GsonUtils() {

    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T fromJson(String str, Class<T> c) {
        return gson.fromJson(str, c);
    }
}
