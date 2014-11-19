package com.livefyre.type;


public enum CollectionType {
    COUNTING("counting"),
    BLOG("liveblog"),
    CHAT("livechat"),
    COMMENTS("livecomments"),
    RATINGS("ratings"),
    REVIEWS("reviews"),
    SIDENOTES("sidenotes");
    
    private String type;
    
    private CollectionType(String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return type;
    }
}
