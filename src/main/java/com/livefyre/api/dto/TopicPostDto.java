package com.livefyre.api.dto;

import org.json.JSONObject;

public class TopicPostDto {
    private int created = 0;
    private int updated = 0;

    public TopicPostDto() {}
    public TopicPostDto(int created, int updated) {
        this.created = created;
        this.updated = updated;
    }
    public TopicPostDto update(JSONObject json) {
        this.created = json.has("created") ? json.getInt("created") : 0;
        this.updated = json.has("updated") ? json.getInt("updated") : 0;
        return this;
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
    public boolean isChanged() {
        return created > 0 || updated > 0;
    }
}
