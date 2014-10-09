package com.livefyre.model;

public class NetworkData {
    private String name = null;
    private String key = null;

    public NetworkData(String name, String key) {
      this.name = name;
      this.key = key;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
