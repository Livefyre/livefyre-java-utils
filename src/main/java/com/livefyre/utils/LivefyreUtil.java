package com.livefyre.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.livefyre.core.Collection;
import com.livefyre.core.LfCore;
import com.livefyre.core.Network;
import com.livefyre.core.Site;

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

    public static Network getNetworkFromCore(LfCore core) {
        if (core.getClass().equals(Network.class)) {
            return (Network) core;
        } else if (core.getClass().equals(Site.class)) {
            return ((Site) core).getNetwork();
        } else {
            return ((Collection) core).getSite().getNetwork();
        }
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
