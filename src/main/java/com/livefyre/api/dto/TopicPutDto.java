package com.livefyre.api.dto;

import org.json.JSONObject;

public class TopicPutDto {
    private int added = 0;
    private int removed = 0;

    public TopicPutDto() {}
    public TopicPutDto(int added, int removed) {
        this.added = added;
        this.removed = removed;
    }
    public TopicPutDto update(JSONObject json) {
        this.added = json.has("added") ? json.getInt("added") : 0;
        this.removed = json.has("removed") ? json.getInt("removed") : 0;
        return this;
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
    public boolean isChanged() {
        return added > 0 || removed > 0;
    }
}
