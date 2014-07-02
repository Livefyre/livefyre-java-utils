package com.livefyre.api.entity;

import java.util.Date;

import org.json.JSONObject;

import com.livefyre.core.LfCore;

public class Topic {
    private static final String TOPIC_IDEN = ":topic=";
    private String id;
    private String label;
    private Integer createdAt;
    private Integer modifiedAt;
    
    /**
     * Use the constructor with params to generate Topic objects. Otherwise id's (urns) will not be formed properly.
     */
    public Topic() {}
    
    public Topic(LfCore core, String id, String label) {
        this.id = Topic.generateUrn(core, id);
        this.label = label;
    }
    
    public Topic(String id, String label, int createdAt, int modifiedAt) {
        this.id = id;
        this.label = label;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
    
    public static String generateUrn(LfCore core, String id) {
        return core.getUrn() + TOPIC_IDEN + id;
    }

    public static Topic fromJson(JSONObject json) {
        return new Topic(json.getString("id"), json.getString("label"), json.getInt("createdAt"), json.getInt("modifiedAt"));
    }
    
    public String truncatedId() {
        return id.substring(id.indexOf(TOPIC_IDEN) + TOPIC_IDEN.length());
    }
    public Date getCreatedAtDate() {
        return new Date(createdAt.longValue()*1000);
    }
    public Date getModifiedAtDate() {
        return new Date(modifiedAt.longValue()*1000);
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public Integer getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Integer createdAt) {
        this.createdAt = createdAt;
    }
    public Integer getModifiedAt() {
        return modifiedAt;
    }
    public void setModifiedAt(Integer modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
