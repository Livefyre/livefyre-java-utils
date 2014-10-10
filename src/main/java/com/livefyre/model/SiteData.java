package com.livefyre.model;

import static com.google.common.base.Preconditions.checkNotNull;

public class SiteData {
    private String id;
    private String key;

    public SiteData(String siteId, String siteKey) {
        this.id = checkNotNull(siteId);
        this.key = checkNotNull(siteKey);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
