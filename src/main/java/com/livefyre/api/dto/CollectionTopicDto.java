package com.livefyre.api.dto;

import java.util.List;

public class CollectionTopicDto {
    private List<String> topicIds;

    public List<String> getTopicIds() {
        return topicIds;
    }
    public void setTopicIds(List<String> topicIds) {
        this.topicIds = topicIds;
    }
}
