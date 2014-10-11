package com.livefyre.type;


public enum CollectionType {
    COUNTING("counting"),
    LIVEBLOG("liveblog"),
    LIVECHAT("livechat"),
    LIVECOMMENTS("livecomments"),
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
    
    public static CollectionType fromString(String text) {
        if (text != null) {
          for (CollectionType c : CollectionType.values()) {
            if (text.equalsIgnoreCase(c.toString())) {
              return c;
            }
          }
        }
        return null;
    }
}
