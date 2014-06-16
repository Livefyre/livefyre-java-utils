package com.livefyre.api.dto;

import java.util.Date;

import com.livefyre.core.Network;
import com.livefyre.core.Site;

public class Topic {
    private String id;
    private String label;
    private Date createdAt;
    private Date modifiedAt;
    
    /**
     * Use the constructor with params to generate Topic objects. Otherwise id's (urns) will not be formed properly.
     */
    public Topic() {}
    
    public Topic(Network network, String id, String label) {
        this.id = Topic.generateUrn(id, network);
        this.label = label;
    }
    
    public Topic(Site site, String id, String label) {
        this.id = Topic.generateUrn(id, site);
        this.label = label;
    }
    
    public static String generateUrn(String id, Network network) {
        return generateUrn(id, network, null);
    }
    
    public static String generateUrn(String id, Site site) {
        return generateUrn(id, site.getNetwork(), site);
    }
    
    private static String generateUrn(String id, Network network, Site site) {
        return String.format("urn:livefyre:%s:", network.getName())
                + site == null ? "" : String.format("site=%s:", site.getId())
                + "topic=" + id;
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
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Date getModifiedAt() {
        return modifiedAt;
    }
    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
    
    public void update(Date date) {
        if (this.createdAt == null) {
            this.createdAt = date;
        }
        this.modifiedAt = date;
    }
}
