package com.livefyre.api.dto;

import java.util.List;

import com.google.common.collect.Lists;

public class TopicDataDto {
    private Topic topic = null;
    private List<Topic> topics = Lists.newArrayList();
    private int created = 0;
    private int updated = 0;
    private int deleted = 0;

    public TopicDataDto() { }
    
    public Topic getTopic() {
        return topic;
    }
    public void setTopic(Topic topic) {
        this.topic = topic;
    }
    public List<Topic> getTopics() {
        return topics;
    }
    public void setTopics(List<Topic> topics) {
        this.topics = topics;
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
}
