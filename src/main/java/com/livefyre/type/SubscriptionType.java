package com.livefyre.type;

public enum SubscriptionType {
    PERSONAL_STREAM("personalStream");

    String type;
    
    private SubscriptionType(String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return type;
    }
}
