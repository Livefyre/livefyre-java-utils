package com.livefyre.model;

import static com.google.common.base.Preconditions.checkNotNull;

import com.livefyre.core.Network;

public class SiteData {
    private Network network;
    private String id;
    private String key;

    public SiteData(Network network, String siteId, String siteKey) {
        this.network = checkNotNull(network);
        this.id = checkNotNull(siteId);
        this.key = checkNotNull(siteKey);
    }

    public Network getNetwork() {
        return this.network;
    }

    public void setNetwork(Network network) {
        this.network = network;
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
