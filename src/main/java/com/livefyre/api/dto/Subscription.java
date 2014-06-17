package com.livefyre.api.dto;

import java.util.Date;

public class Subscription {
    private String to;
    private String by;
    private Type type;
    private Date createdAt;
    
    public Subscription(String to, String by, Type type) {
        this.to = to;
        this.by = by;
        this.type = type;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
    
    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public enum Type {
        PERSONAL_STREAM("personalStream", 1);
        
        private String name;
        private int type;
        
        private Type(String name, int type) {
            this.name = name;
            this.type = type;
        }
        
        public int getType() {
            return this.type;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
}
