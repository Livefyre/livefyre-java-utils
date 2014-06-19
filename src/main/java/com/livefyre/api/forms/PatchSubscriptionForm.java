package com.livefyre.api.forms;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;

import com.livefyre.api.entity.Subscription;

public class PatchSubscriptionForm {
    @FormParam("delete")
    @NotNull
    List<Subscription> delete;
    
    public PatchSubscriptionForm() { }
    
    public PatchSubscriptionForm(List<Subscription> delete) {
        this.delete = delete;
    }

    public List<Subscription> getDelete() {
        return delete;
    }

    public void setDelete(List<Subscription> delete) {
        this.delete = delete;
    }
}
