package com.livefyre.model;


public enum CollectionType {
    REVIEWS("reviews"),
    SIDENOTES("sidenotes"),
    RATINGS("ratings"),
    COUNTING("counting"),
    LIVEBLOG("liveblog"),
    LIVECHAT("livechat"),
    LIVECOMMENTS("livecomments");
    
    private String type;
    
    private CollectionType(String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return type;
    }
}
