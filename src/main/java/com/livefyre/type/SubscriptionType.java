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
    
    public static SubscriptionType fromString(String text) {
        if (text != null) {
            for (SubscriptionType e : SubscriptionType.values()) {
                if (text.equalsIgnoreCase(e.toString())) {
                    return e;
                }
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found!");
    }
}
