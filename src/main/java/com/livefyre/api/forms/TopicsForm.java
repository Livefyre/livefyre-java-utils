package com.livefyre.api.forms;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;

import com.livefyre.api.entity.Topic;

public class TopicsForm {
    @FormParam("topics")
    @NotNull
    List<Topic> topics;
    
    public TopicsForm() { }
    
    public TopicsForm(List<Topic> topics) {
        this.topics = topics;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }
}
