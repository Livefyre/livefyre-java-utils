package com.livefyre.type;

public enum SubscriptionType {
    personalStream(1);

    int val;
    
    private SubscriptionType(int val) {
        this.val = val;
    }
    
    @Override
    public String toString() {
        return name();
    }
    
    public static SubscriptionType fromNum(Integer num) {
        if (num != null) {
            for (SubscriptionType e : SubscriptionType.values()) {
                if (num == e.val) {
                    return e;
                }
            }
        }
        throw new IllegalArgumentException("No constant with value " + num + " found!");
    }
}
