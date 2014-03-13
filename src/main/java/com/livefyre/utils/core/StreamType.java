package com.livefyre.utils.core;

public enum StreamType {
    NONE (""),
    REVIEWS ("reviews");
    
    private String type;

    private StreamType(String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return this.type;
    }
}
