package com.livefyre.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.livefyre.api.client.PersonalizedStreamsClient;
import com.livefyre.api.entity.TimelineCursor;
import com.livefyre.api.entity.Topic;
import com.livefyre.api.factory.CursorFactory;
import com.livefyre.exceptions.LivefyreException;
import com.livefyre.exceptions.TokenException;
import com.livefyre.repackaged.apache.commons.Base64;
import com.livefyre.utils.LivefyreJwtUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

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
            return LivefyreJwtUtil.encodeLivefyreJwt(key, data);
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
            return printHexBinary(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new LivefyreException("Failure creating checksum." + e);
        }
    }
    
    public JSONObject getCollectionContentJson(String articleId) {
        return new JSONObject(articleId);
    }

    public String getCollectionContent(String articleId) {
        checkNotNull(articleId);

        String url = String.format("http://bootstrap.%1$s/bs3/%1$s/%2$s/%3$s/init", network.getName(), id,
                Base64.encodeBase64URLSafeString(articleId.getBytes()));

        ClientResponse response = Client.create()
            .resource(url)
            .accept(MediaType.APPLICATION_JSON)
            .get(ClientResponse.class);
        if (response.getStatus() != 200) {
            throw new LivefyreException("Error contacting Livefyre. Status code: " + response.getStatus());
        }
        return response.getEntity(String.class);
    }

    public String getCollectionId(String articleId) {
        JSONObject collection = getCollectionContentJson(articleId);
        return collection.getJSONObject("collectionSettings").getString("collectionId");
    }
    
    /* Topic API */
    public Topic getTopic(String topicId) {
        return PersonalizedStreamsClient.getTopic(this, topicId);
    }
    
    public Topic createOrUpdateTopic(String id, String label) {
        Topic topic = new Topic(this, id, label);
        PersonalizedStreamsClient.postTopic(this, topic);
        return topic;
    }
    
    public boolean deleteTopic(Topic topic) {
        return PersonalizedStreamsClient.deleteTopic(this, topic);
    }
    
    /* Multiple Topic API */
    public List<Topic> getTopics() {
        return PersonalizedStreamsClient.getTopics(this, null, null);
    }
    
    public List<Topic> getTopics(Integer limit, Integer offset) {
        return PersonalizedStreamsClient.getTopics(this, limit, offset);
    }
    
    public List<Topic> createOrUpdateTopics(Map<String, String> topics) {
        List<Topic> list = Lists.newArrayList();
        for (String k : topics.keySet()) {
            list.add(new Topic(this, k, topics.get(k)));
        }
        
        PersonalizedStreamsClient.postTopics(this, list);
        return list;
    }
    
    public int deleteTopics(List<Topic> topics) {
        return PersonalizedStreamsClient.deleteTopics(this, topics);
    }
    
    /* Collection Topic API */
    public List<String> getCollectionTopics(String collectionId) {
        return PersonalizedStreamsClient.getCollectionTopics(this, collectionId);
    }
    
    public Integer addCollectionTopics(String collectionId, List<Topic> topics) {
        return PersonalizedStreamsClient.postCollectionTopics(this, collectionId, topics);
    }
    
    public boolean updateCollectionTopics(String collectionId, List<Topic> topics) {
        return PersonalizedStreamsClient.putCollectionTopics(this, collectionId, topics).isChanged();
    }
    
    public Integer removeCollectionTopics(String collectionId, List<Topic> topics) {
        return PersonalizedStreamsClient.deleteCollectionTopics(this, collectionId, topics);
    }
    
    /* Timeline cursor */
    public TimelineCursor getTopicStreamCursor(Topic topic) {
        return getTopicStreamCursor(topic, 50, Calendar.getInstance().getTime());
    }
    
    public TimelineCursor getTopicStreamCursor(Topic topic, Integer limit, Date date) {
        return CursorFactory.getTopicStreamCursor(this, topic, limit, date);
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
    
    private static final char[] hexCode = "0123456789abcdef".toCharArray();
    
    private String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
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
