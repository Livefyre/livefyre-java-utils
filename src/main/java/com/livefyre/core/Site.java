package com.livefyre.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.livefyre.api.client.PersonalizedStreamsClientImpl;
import com.livefyre.api.dto.CollectionTopicDto;
import com.livefyre.api.dto.Topic;
import com.livefyre.exceptions.LivefyreException;
import com.livefyre.exceptions.TokenException;
import com.livefyre.utils.LivefyreJwtUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

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

        ClientResponse response = Client
            .create()
            .resource(
                String.format("http://bootstrap.%1$s/bs3/%1$s/%2$s/%3$s/init", this.network.getName(), this.siteId,
                    Base64.encodeBase64URLSafeString(articleId.getBytes())))
            .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        if (response.getStatus() != 200) {
            throw new LivefyreException("Error contacting Livefyre. Status code: " + response.getStatus());
        }
        return response.getEntity(String.class);
    }

    public String getCollectionId(String articleId) {
        JsonObject collection = getCollectionContentJson(articleId);
        return collection.get("collectionSettings").getAsJsonObject().get("collectionId").getAsString();
    }
    
    /* Topic API */
    public Topic getTopic(String topicId) {
        return PersonalizedStreamsClientImpl.getSiteTopic(this, topicId);
    }
    
    public boolean addOrUpdateTopic(Topic topic) {
        return PersonalizedStreamsClientImpl.postSiteTopic(this, topic);
    }
    
    public boolean deleteTopic(Topic topic) {
        return PersonalizedStreamsClientImpl.deleteSiteTopic(this, topic);
    }
    
    /* Multiple Topic API */
    public List<Topic> getTopics(Integer limit, Integer offset) {
        return PersonalizedStreamsClientImpl.getSiteTopics(this, limit, offset);
    }
    
    public CollectionTopicDto postTopics(List<Topic> topics) {
        return PersonalizedStreamsClientImpl.postSiteTopics(this, topics);
    }
    
    public CollectionTopicDto deleteTopics(List<Topic> topics) {
        return PersonalizedStreamsClientImpl.deleteSiteTopics(this, topics);
    }
    
    /* Collection Topic API */
    public CollectionTopicDto getCollectionTopics(String collectionId) {
        return PersonalizedStreamsClientImpl.getCollectionTopics(this, collectionId);
    }
    
    public CollectionTopicDto postCollectionTopics(String collectionId, List<Topic> topics) {
        return PersonalizedStreamsClientImpl.postCollectionTopics(this, collectionId, topics);
    }
    
    public CollectionTopicDto putCollectionTopics(String collectionId, List<Topic> topics) {
        return PersonalizedStreamsClientImpl.putCollectionTopics(this, collectionId, topics);
    }
    
    public CollectionTopicDto deleteCollectionTopics(String collectionId, List<Topic> topics) {
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
