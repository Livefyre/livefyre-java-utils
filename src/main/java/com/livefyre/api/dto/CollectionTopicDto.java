package com.livefyre.api.dto;

import java.util.List;

public class CollectionTopicDto {
    private List<String> topicIds;
    private int created;
    private int updated;
    private int deleted;
    private int added;
    private int removed;

    public List<String> getTopicIds() {
        return topicIds;
    }
    public void setTopicIds(List<String> topicIds) {
        this.topicIds = topicIds;
    }
    public int getCreated() {
        return created;
    }
    public void setCreated(int created) {
        this.created = created;
    }
    public int getUpdated() {
        return updated;
    }
    public void setUpdated(int updated) {
        this.updated = updated;
    }
    public int getDeleted() {
        return deleted;
    }
    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }
    public int getAdded() {
        return added;
    }
    public void setAdded(int added) {
        this.added = added;
    }
    public int getRemoved() {
        return removed;
    }
    public void setRemoved(int removed) {
        this.removed = removed;
    }
}
