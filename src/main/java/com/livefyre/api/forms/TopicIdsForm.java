package com.livefyre.api.forms;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;

public class TopicIdsForm {
    @FormParam("topicIds")
    @NotNull
    List<String> topicIds;
    
    public TopicIdsForm() { }
    
    public TopicIdsForm(List<String> topicIds) {
        this.topicIds = topicIds;
    }

    public List<String> getTopicIds() {
        return topicIds;
    }

    public void setTopicIds(List<String> topicIds) {
        this.topicIds = topicIds;
    }
}
