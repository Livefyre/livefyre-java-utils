package com.livefyre.api.dto;

public class Subscription {
    private String to;

    public Subscription(String to) {
        this.to = to;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
