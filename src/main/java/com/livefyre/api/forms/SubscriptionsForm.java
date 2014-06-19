package com.livefyre.api.forms;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;

import com.livefyre.api.entity.Subscription;

public class SubscriptionsForm {
    @FormParam("subscriptions")
    @NotNull
    List<Subscription> subscriptions;
    
    public SubscriptionsForm() { }
    
    public SubscriptionsForm(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }
}
