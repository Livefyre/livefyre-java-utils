package com.livefyre.dto;

import java.util.Date;

import com.google.gson.JsonObject;
import com.livefyre.type.SubscriptionType;

public class Subscription {
    private String to;
    private String by;
    private String type;
    private Integer createdAt;

    public Subscription() { }

    public Subscription(String to, String by, SubscriptionType type, Integer createdAt) {
        this.to = to;
        this.by = by;
        this.type = type.toString();
        this.createdAt = createdAt;
    }

    public static Subscription serializeFromJson(JsonObject json) {
        SubscriptionType type;
        try {
            type = SubscriptionType.valueOf(json.get("type").getAsString());
        } catch (IllegalArgumentException e) {
            type = SubscriptionType.fromNum(json.get("type").getAsInt());
        }
        
        return new Subscription(
            json.get("to").getAsString(),
            json.get("by").getAsString(),
            type,
            json.get("createdAt").getAsInt());
    }

    public Date createdAtDate() {
        return new Date(createdAt.longValue() * 1000);
    }

    /* Getters/Setters */
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Integer createdAt) {
        this.createdAt = createdAt;
    }
}
