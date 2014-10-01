package com.livefyre.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class LivefyreUtil {

    private LivefyreUtil() { }
    
    public static JsonObject stringToJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, JsonObject.class);
    }
    
    public static String mapToJsonString(Map<String, Object> map) {
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    public static boolean isValidFullUrl(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }
}
