package com.livefyre.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.ClientBuilder;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.livefyre.api.client.PersonalizedStreamsClientImpl;
import com.livefyre.api.entity.Topic;
import com.livefyre.exceptions.LivefyreException;
import com.livefyre.exceptions.TokenException;
import com.livefyre.repackaged.apache.commons.Base64;
import com.livefyre.utils.LivefyreJwtUtil;

public class Site implements LfCore {
    private static final String TOKEN_FAILURE_MSG = "Failure creating token.";
    private static final ImmutableList<String> TYPE = ImmutableList.of(
        "reviews", "sidenotes", "ratings", "counting",
        "liveblog", "livechat", "livecomments", ""
    );

    private Network network = null;
    private String id = null;
    private String key = null;

    public Site(Network network, String id, String key) {
        this.network = checkNotNull(network);
        this.id = checkNotNull(id);
        this.key = checkNotNull(key);
    }
    
    /* Kept for backwards compatibility/usability */
    public String buildCollectionMetaToken(String title, String articleId, String url, String tags, String type) {
        return buildCollectionMetaToken(title, articleId, url, 
                ImmutableMap.<String, Object>of("tags", tags == null ? "" : tags, "type", type == null ? "" : type));
    }
    
    /**
     * This method allows a variety of parameters/options to be passed in as a map.  Some examples are 'tags', 'type', 'extensions',
     * 'tags', etc.  Please refer to http://answers.livefyre.com/developers/getting-started/tokens/collectionmeta/ for more info.
     * 
     * @param extras map of additional params to be included into the collection meta token.
     */
    public String buildCollectionMetaToken(String title, String articleId, String url, Map<String, Object> extras) {
        checkArgument(checkNotNull(title).length() <= 255, "title is longer than 255 characters.");
        checkNotNull(articleId);
        checkArgument(isValidFullUrl(checkNotNull(url)), "url is not a valid url. see http://www.ietf.org/rfc/rfc2396.txt");

        if (extras.containsKey("type") && !TYPE.contains(extras.get("type"))) {
            throw new IllegalArgumentException("type is not a recognized type. should be one of these types: " +TYPE.toString());
        }
        
        Map<String, Object> data = Maps.newHashMap(extras);
        data.put("url", url);
        data.put("title", title);
        data.put("articleId", articleId);
        
        try {
            return LivefyreJwtUtil.encodeLivefyreJwt(this.key, data);
        } catch (InvalidKeyException e) {
            throw new TokenException(TOKEN_FAILURE_MSG + e);
        }
    }

    public String buildChecksum(String title, String url, String tags) {
        checkArgument(checkNotNull(title).length() <= 255, "title is longer than 255 characters.");
        checkArgument(isValidFullUrl(checkNotNull(url)),
            "url is not a valid url. see http://www.ietf.org/rfc/rfc2396.txt");

        String t = tags == null ? "" : tags;

        try {
            JSONObject json = new JSONObject();
            json.put("url", url);
            json.put("tags", t);
            json.put("title", title);
            byte[] digest = MessageDigest.getInstance("MD5").digest(json.toString().getBytes());
            return DatatypeConverter.printHexBinary(digest).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            throw new LivefyreException("Failure creating checksum." + e);
        }
    }

    public JSONObject getCollectionContentJson(String articleId) {
        return new JSONObject(articleId);
    }

    public String getCollectionContent(String articleId) {
        checkNotNull(articleId);

        String url = String.format("http://bootstrap.%1$s/bs3/%1$s/%2$s/%3$s/init", this.network.getName(), this.id,
                Base64.encodeBase64URLSafeString(articleId.getBytes()));

        String response = ClientBuilder.newClient()
                .target(url)
                .request()
                .get(String.class);
        
        return response;
    }

    public String getCollectionId(String articleId) {
        JSONObject collection = getCollectionContentJson(articleId);
        return collection.getJSONObject("collectionSettings").getString("collectionId");
    }
    
    /* Topic API */
    public Topic getTopic(String topicId) {
        return PersonalizedStreamsClientImpl.getTopic(this, topicId);
    }
    
    public Topic createOrUpdateTopic(String id, String label) {
        Topic topic = new Topic(this, id, label);
        PersonalizedStreamsClientImpl.postTopic(this, topic);
        return topic;
    }
    
    public boolean deleteTopic(Topic topic) {
        return PersonalizedStreamsClientImpl.deleteTopic(this, topic);
    }
    
    /* Multiple Topic API */
    public List<Topic> getTopics() {
        return PersonalizedStreamsClientImpl.getTopics(this, null, null);
    }
    
    public List<Topic> getTopics(Integer limit, Integer offset) {
        return PersonalizedStreamsClientImpl.getTopics(this, limit, offset);
    }
    
    public List<Topic> createOrUpdateTopics(Map<String, String> topics) {
        List<Topic> list = Lists.newArrayList();
        for (String key : topics.keySet()) {
            list.add(new Topic(this, key, topics.get(key)));
        }
        
        PersonalizedStreamsClientImpl.postTopics(this, list);
        return list;
    }
    
    public boolean deleteTopics(List<Topic> topics) {
        return (PersonalizedStreamsClientImpl.deleteTopics(this, topics).getDeleted() > 0);
    }
    
    /* Collection Topic API */
    public List<String> getCollectionTopics(String collectionId) {
        return PersonalizedStreamsClientImpl.getCollectionTopics(this, collectionId);
    }
    
    public Integer addCollectionTopics(String collectionId, List<Topic> topics) {
        return PersonalizedStreamsClientImpl.postCollectionTopics(this, collectionId, topics);
    }
    
    public boolean updateCollectionTopics(String collectionId, List<Topic> topics) {
        return PersonalizedStreamsClientImpl.putCollectionTopics(this, collectionId, topics);
    }
    
    public Integer removeCollectionTopics(String collectionId, List<Topic> topics) {
        return PersonalizedStreamsClientImpl.deleteCollectionTopics(this, collectionId, topics);
    }
    
    /* Helper methods */
    public String getUrn() {
        return network.getUrn()+":site="+id;
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
    public String buildLivefyreToken() { return network.buildLivefyreToken(); }
    public String getNetworkName() { return network.getName(); }
    public Network getNetwork() { return this.network; }
    protected void setNetwork(Network network) { this.network = network; }
    public String getId() { return id; }
    protected void setId(String id) { this.id = id; }
    public String getKey() { return key; }
    protected void setKey(String key) { this.key = key; }
}
