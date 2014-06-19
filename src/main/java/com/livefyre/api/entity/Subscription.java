package com.livefyre.api.entity;

import java.util.Date;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Subscription {
    private String to;
    private String by;
    private Type type;
    private Integer createdAt;
    
    public Subscription() { }
    
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
    public Integer getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Integer createdAt) {
        this.createdAt = createdAt;
    }
    public Date getCreatedAtDate() {
        return new Date(createdAt.longValue()*1000);
    }

    @XmlEnum(String.class)
    public enum Type {
        personalStream(1);
        
        private int type;
        
        private Type(int type) {
            this.type = type;
        }
        
        public int getType() {
            return this.type;
        }
    }
}
