package com.livefyre.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.ClientBuilder;

import org.apache.commons.codec.binary.Base64;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.livefyre.api.client.PersonalizedStreamsClientImpl;
import com.livefyre.api.dto.CollectionTopicDto;
import com.livefyre.api.dto.PostDto;
import com.livefyre.api.dto.Topic;
import com.livefyre.api.dto.TopicsDto;
import com.livefyre.exceptions.LivefyreException;
import com.livefyre.exceptions.TokenException;
import com.livefyre.utils.LivefyreJwtUtil;

public class Site {
    private static final String TOKEN_FAILURE_MSG = "Failure creating token.";
    private static final ImmutableList<String> TYPE = ImmutableList.of(
        "reviews",
        "sidenotes",
        "ratings",
        "counting",
        "liveblog",
        "livechat",
        "livecomments",
        ""
    );

    private Network network = null;
    private String siteId = null;
    private String siteKey = null;

    public Site(Network network, String siteId, String siteKey) {
        this.network = checkNotNull(network);
        this.siteId = checkNotNull(siteId);
        this.siteKey = checkNotNull(siteKey);
    }

    public String buildCollectionMetaToken(String title, String articleId, String url, String tags, String type) {
        checkArgument(checkNotNull(title).length() <= 255, "title is longer than 255 characters.");
        checkNotNull(articleId);
        checkArgument(isValidFullUrl(checkNotNull(url)), "url is not a valid url. see http://www.ietf.org/rfc/rfc2396.txt");

        String t = tags == null ? "" : tags;
        
        try {
            if (TYPE.contains(type) || type == null) {
                return LivefyreJwtUtil.getJwtCollectionMetaToken(this.siteKey, title, t, url, articleId, type);
            } else {
                throw new IllegalArgumentException("type is not a recognized type. should be liveblog, livechat, livecomments, reviews, sidenotes, or an empty String.");
            }
        } catch (InvalidKeyException e) {
            throw new TokenException(TOKEN_FAILURE_MSG + e);
        } catch (SignatureException e) {
            throw new TokenException(TOKEN_FAILURE_MSG + e);
        }
    }

    public String buildChecksum(String title, String url, String tags) {
        checkArgument(checkNotNull(title).length() <= 255, "title is longer than 255 characters.");
        checkArgument(isValidFullUrl(checkNotNull(url)),
            "url is not a valid url. see http://www.ietf.org/rfc/rfc2396.txt");

        String t = tags == null ? "" : tags;

        try {
            return LivefyreJwtUtil.getChecksum(title, url, t);
        } catch (NoSuchAlgorithmException e) {
            throw new LivefyreException("Failure creating checksum." + e);
        }
    }

    public JsonObject getCollectionContentJson(String articleId) {
        return (JsonObject) new JsonParser().parse(getCollectionContent(articleId));
    }

    public String getCollectionContent(String articleId) {
        checkNotNull(articleId);

        String url = String.format("http://bootstrap.%1$s/bs3/%1$s/%2$s/%3$s/init", this.network.getName(), this.siteId,
                Base64.encodeBase64URLSafeString(articleId.getBytes()));

        String response = ClientBuilder.newClient()
                .target(url)
                .request()
                .get(String.class);
        
        return response;
    }

    public String getCollectionId(String articleId) {
        JsonObject collection = getCollectionContentJson(articleId);
        return collection.get("collectionSettings").getAsJsonObject().get("collectionId").getAsString();
    }
    
    /* Topic API */
    public Topic getTopic(String topicId) {
        return PersonalizedStreamsClientImpl.getSiteTopic(this, topicId);
    }
    
    public Topic createOrUpdateTopic(String id, String label) {
        Topic topic = new Topic(this, id, label);
        if (PersonalizedStreamsClientImpl.postSiteTopic(this, topic)) {
            return topic;
        }
        return topic;
    }
    
    public boolean deleteTopic(Topic topic) {
        return PersonalizedStreamsClientImpl.deleteSiteTopic(this, topic);
    }
    
    /* Multiple Topic API */
    public List<Topic> getTopics() {
        return PersonalizedStreamsClientImpl.getSiteTopics(this, null, null);
    }
    
    public List<Topic> getTopics(Integer limit, Integer offset) {
        return PersonalizedStreamsClientImpl.getSiteTopics(this, limit, offset);
    }
    
    public List<Topic> createOrUpdateTopics(Map<String, String> topics) {
        List<Topic> list = Lists.newArrayList();
        for (String key : topics.keySet()) {
            list.add(new Topic(this, key, topics.get(key)));
        }
        
        TopicsDto result = PersonalizedStreamsClientImpl.postSiteTopics(this, list);
        if (result.getCreated() > 0 || result.getUpdated() > 0) {
            return list;
        }
        return null;
    }
    
    public boolean deleteTopics(List<Topic> topics) {
        return (PersonalizedStreamsClientImpl.deleteSiteTopics(this, topics).getDeleted() > 0);
    }
    
    /* Collection Topic API */
    public CollectionTopicDto getCollectionTopics(String collectionId) {
        return PersonalizedStreamsClientImpl.getCollectionTopics(this, collectionId);
    }
    
    public Integer createCollectionTopics(String collectionId, List<Topic> topics) {
        return PersonalizedStreamsClientImpl.postCollectionTopics(this, collectionId, topics);
    }
    
    public PostDto updateCollectionTopics(String collectionId, List<Topic> topics) {
        return PersonalizedStreamsClientImpl.putCollectionTopics(this, collectionId, topics);
    }
    
    public Integer deleteCollectionTopics(String collectionId, List<Topic> topics) {
        return PersonalizedStreamsClientImpl.deleteCollectionTopics(this, collectionId, topics);
    }
    
    protected boolean isValidFullUrl(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }
    
    /* Getters/Setters */
    public String getNetworkName() { return this.network.getName(); }
    public Network getNetwork() { return this.network; }
    protected void setNetwork(Network network) { this.network = network; }
    public String getId() { return siteId; }
    protected void setSiteId(String siteId) { this.siteId = siteId; }
    public String getSiteKey() { return siteKey; }
    protected void setSiteKey(String key) { this.siteKey = key; }
}
