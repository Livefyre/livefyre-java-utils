package com.livefyre.api.forms;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;

public class PatchTopicsForm {
    @FormParam("delete")
    @NotNull
    List<String> delete;
    
    public PatchTopicsForm() { }
    
    public PatchTopicsForm(List<String> delete) {
        this.delete = delete;
    }

    public List<String> getDelete() {
        return delete;
    }

    public void setDelete(List<String> delete) {
        this.delete = delete;
    }
}
