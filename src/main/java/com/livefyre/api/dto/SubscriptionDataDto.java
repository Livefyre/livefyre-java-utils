package com.livefyre.api.dto;

import java.util.List;

import com.google.common.collect.Lists;

public class SubscriptionDataDto {
    private List<Subscription> subscriptions = Lists.newArrayList();
    private Integer added = 0;
    private Integer removed = 0;

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }
    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }
    public Integer getAdded() {
        return added;
    }
    public void setAdded(Integer added) {
        this.added = added;
    }
    public Integer getRemoved() {
        return removed;
    }
    public void setRemoved(Integer removed) {
        this.removed = removed;
    }
}
