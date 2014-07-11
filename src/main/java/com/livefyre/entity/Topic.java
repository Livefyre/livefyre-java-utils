package com.livefyre.entity;

import java.util.Date;

import org.json.JSONObject;

import com.livefyre.core.LfCore;

public class Topic {
    private static final String TOPIC_IDENTIFIER = ":topic=";
    private String id;
    private String label;
    private Integer createdAt;
    private Integer modifiedAt;
    
    public Topic() {}
    
    public Topic(String id, String label, Integer createdAt, Integer modifiedAt) {
        this.id = id;
        this.label = label;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
    
    /**
     * Use this method to generate Topic objects. Otherwise id's (urns) will not be formed properly.
     */
    public static Topic create(LfCore core, String id, String label) {
        return new Topic(generateUrn(core, id), label, null, null);
    }
    
    public static String generateUrn(LfCore core, String id) {
        return core.getUrn() + TOPIC_IDENTIFIER + id;
    }

    public static Topic serializeFromJson(JSONObject json) {
        return new Topic(json.getString("id"), json.getString("label"), json.getInt("createdAt"), json.getInt("modifiedAt"));
    }
    
    public String truncatedId() {
        return id.substring(id.indexOf(TOPIC_IDENTIFIER) + TOPIC_IDENTIFIER.length());
    }
    public Date getCreatedAtDate() {
        return new Date(createdAt.longValue()*1000);
    }
    public Date getModifiedAtDate() {
        return new Date(modifiedAt.longValue()*1000);
    }
    
    /* Getters/Setters */
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
