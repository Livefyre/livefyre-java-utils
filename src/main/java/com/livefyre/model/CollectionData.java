package com.livefyre.model;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Maps;
import com.livefyre.dto.Topic;
import com.livefyre.exceptions.LivefyreException;
import com.livefyre.type.CollectionType;

public class CollectionData {
    private CollectionType type;
    private String id;
    private String articleId;
    private String title;
    private String url;
    
    //optional params
    private String tags;
    private List<Topic> topics;
    private String extensions;
    
    public CollectionData(CollectionType type, String title, String articleId, String url) {
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
        
        if (StringUtils.isNotBlank(tags)) {
            attr.put("tags", tags);
        }
        if (topics != null && topics.size() > 0) {
            attr.put("topics", topics);
        }
        if (StringUtils.isNotBlank(extensions)) {
            attr.put("extensions", extensions);
        }
        return attr;
    }

    public CollectionType getType() {
        return type;
    }
    
    public CollectionData setType(CollectionType type) {
        this.type = type;
        return this;
    }
    
    public String getId() {
        if (id == null) {
            throw new LivefyreException("Id not set. Call createOrUpdate() on the collection to set the id, or manually set it by calling setId(id) on this object.");
        }
        return id;
    }
    
    public CollectionData setId(String id) {
        this.id = id;
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

    public String getTags() {
        return tags;
    }

    public CollectionData setTags(String tags) {
        this.tags = tags;
        return this;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public CollectionData setTopics(List<Topic> topics) {
        this.topics = topics;
        return this;
    }

    public String getExtensions() {
        return extensions;
    }

    public CollectionData setExtensions(String extensions) {
        this.extensions = extensions;
        return this;
    }
}
