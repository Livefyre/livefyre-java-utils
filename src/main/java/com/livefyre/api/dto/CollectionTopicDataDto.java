package com.livefyre.api.dto;

import java.util.List;

import com.google.common.collect.Lists;

public class CollectionTopicDataDto {
    private List<String> topicIds = Lists.newArrayList();
    private Integer added = 0;
    private Integer removed = 0;

    public List<String> getTopicIds() {
        return topicIds;
    }
    public void setTopicIds(List<String> topicIds) {
        this.topicIds = topicIds;
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
