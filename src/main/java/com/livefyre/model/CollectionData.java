package com.livefyre.model;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.livefyre.core.Site;
import com.livefyre.entity.Topic;
import com.livefyre.exceptions.LivefyreException;

public class CollectionData {
    private Site site;
    private CollectionType type;
    private String collectionId;
    private String articleId;
    private String title;
    private String url;
    private Boolean networkIssued;
    private List<Topic> topics = Lists.newArrayList();
    private String extensions = "";
    
    public CollectionData(Site site, CollectionType type, String title, String articleId, String url) {
        this.site = site;
        this.type = type;
        this.articleId = articleId;
        this.title = title;
        this.url = url;
    }

    public Map<String, Object> asMap() {
        Map<String, Object> attr = Maps.newTreeMap();
        attr.put("articleId", articleId);
        attr.put("title", title);
        attr.put("type", type.toString());
        attr.put("url", url);
        
        if (topics.size() > 0) {
            attr.put("topics", topics);
        }
        if (extensions.length() > 0) {
            attr.put("extensions", extensions);
        }
        return attr;
    }

    public Site getSite() {
        return site;
    }

    public CollectionData setSite(Site site) {
        this.site = site;
        return this;
    }
    
    public CollectionType getType() {
        return type;
    }
    
    public CollectionData setType(CollectionType type) {
        this.type = type;
        return this;
    }
    
    public String getCollectionId() {
        if (collectionId == null) {
            throw new LivefyreException("Call createOrUpdate() on the collection to set the collection id.");
        }
        return collectionId;
    }
    
    public CollectionData setCollectionId(String collectionId) {
        this.collectionId = collectionId;
        return this;
    }

    public String getArticleId() {
        return articleId;
    }

    public CollectionData setArticleId(String articleId) {
        this.articleId = articleId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public CollectionData setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public CollectionData setUrl(String url) {
        this.url = url;
        return this;
    }

    public boolean isNetworkIssued() {
        if (networkIssued == null) {
            networkIssued = checkTopics();
        }
        return networkIssued;
    }

    public CollectionData setNetworkIssued(boolean networkIssued) {
        this.networkIssued = networkIssued;
        return this;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public CollectionData setTopics(List<Topic> topics) {
        this.topics = topics;
        this.networkIssued = null;
        return this;
    }

    public String getExtensions() {
        return extensions;
    }

    public CollectionData setExtensions(String extensions) {
        this.extensions = extensions;
        return this;
    }

    private boolean checkTopics() {
        if (topics.isEmpty()) {
            return false;
        }

        for (Topic topic : topics) {
            String topicId = topic.getId();
            String networkUrn = site.getData().getNetwork().getUrn();
            if (topicId.startsWith(networkUrn) && !topicId.replace(networkUrn, "").startsWith(":site=")) {
                return true;
            }
        }
        return false;
    }
}
