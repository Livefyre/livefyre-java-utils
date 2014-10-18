package com.livefyre.model;

public class SiteData {
    private String id;
    private String key;

    public SiteData(String id, String key) {
        this.id = id;
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public SiteData setId(String id) {
        this.id = id;
        return this;
    }

    public String getKey() {
        return key;
    }

    public SiteData setKey(String key) {
        this.key = key;
        return this;
    }
}
