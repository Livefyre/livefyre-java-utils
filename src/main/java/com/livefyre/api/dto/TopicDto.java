package com.livefyre.api.dto;

public class TopicDto {
    private TopicDataDto data;
    private int added;
    private int created;
    private int updated;

    public TopicDataDto getData() {
        return data;
    }
    public void setData(TopicDataDto data) {
        this.data = data;
    }
    public int getAdded() {
        return added;
    }
    public void setAdded(int added) {
        this.added = added;
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
    
}
