package com.livefyre.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.security.InvalidKeyException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.livefyre.api.client.PersonalizedStreamsClient;
import com.livefyre.api.entity.Subscription;
import com.livefyre.api.entity.TimelineCursor;
import com.livefyre.api.entity.Topic;
import com.livefyre.api.factory.CursorFactory;
import com.livefyre.exceptions.TokenException;
import com.livefyre.utils.LivefyreJwtUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class Network implements LfCore {
    public static final double DEFAULT_EXPIRES = 86400.0;
    private static final String DEFAULT_USER = "system";
    private static final String ID = "{id}";
    
    private String name = null;
    private String key = null;
    
    public Network(String name, String key) {
        this.name = checkNotNull(name);
        this.key = checkNotNull(key);
    }
    
    public boolean setUserSyncUrl(String urlTemplate) {
        checkArgument(checkNotNull(urlTemplate).contains(ID), "urlTemplate does not contain %s", ID);
        
        ClientResponse response = Client.create()
            .resource(String.format("http://%s/", name))
            .queryParam("actor_token", buildLivefyreToken())
            .queryParam("pull_profile_url", urlTemplate)
            .post(ClientResponse.class);
        return response.getStatus() == 204;
    }
    
    public boolean syncUser(String userId) {
        checkNotNull(userId);
        
        String url = String.format("http://%s/api/v3_0/user/%s/refresh", name, userId);
        ClientResponse response = Client.create()
            .resource(url)
            .queryParam("lftoken", buildLivefyreToken())
            .post(ClientResponse.class);
        return response.getStatus() == 200;
    }
    
    public String buildLivefyreToken() {
        return buildUserAuthToken(DEFAULT_USER, DEFAULT_USER, DEFAULT_EXPIRES);
    }
    
    public String buildUserAuthToken(String userId, String displayName, Double expires) {
        checkArgument(StringUtils.isAlphanumeric(checkNotNull(userId)), "userId is not alphanumeric.");
        checkNotNull(displayName);
        checkNotNull(expires);
        
        Map<String, Object> data = ImmutableMap.<String, Object>of(
            "domain", name,
            "user_id", userId,
            "display_name", displayName,
            "expires", getExpiryInSeconds(expires)
        );
        
        try {
            return LivefyreJwtUtil.encodeLivefyreJwt(key, data);
        } catch (InvalidKeyException e) {
            throw new TokenException("Failure creating token." +e);
        }
    }
    
    public boolean validateLivefyreToken(String lfToken) {
        checkNotNull(lfToken);

        try {
            JSONObject json = LivefyreJwtUtil.decodeLivefyreJwt(key, lfToken);
            return json.getString("domain").compareTo(name) == 0
                && json.getString("user_id").compareTo("system") == 0
                && json.getInt("expires") >= Calendar.getInstance().getTimeInMillis()/1000L;
        } catch (InvalidKeyException e) {
            throw new TokenException("Failure decrypting token." +e);
        }
    }
    
    public Site getSite(String siteId, String siteKey) {
        return new Site(this, siteId, siteKey);
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
    
    public List<Topic> createOrUpdateTopics(Map<String, String> topicMap) {
        List<Topic> list = Lists.newArrayList();
        for (String k : topicMap.keySet()) {
            list.add(new Topic(this, k, topicMap.get(k)));
        }
        
        PersonalizedStreamsClient.postTopics(this, list);
        return list;
    }
    
    public int deleteTopics(List<Topic> topics) {
        return PersonalizedStreamsClient.deleteTopics(this, topics);
    }
    
    /* Subscription API */
    public List<Subscription> getSubscriptions(String user) {
        return PersonalizedStreamsClient.getSubscriptions(this, user);
    }
    
    public Integer addSubscriptions(String user, List<Topic> topics) {
        return PersonalizedStreamsClient.postSubscriptions(this, user, topics);
    }

    public boolean updateSubscriptions(String user, List<Topic> topics) {
        return PersonalizedStreamsClient.putSubscriptions(this, user, topics).isChanged();
    }

    public Integer removeSubscriptions(String user, List<Topic> topics) {
        return PersonalizedStreamsClient.deleteSubscriptions(this, user, topics);
    }
    
    /* Subscriber API */
    public List<Subscription> getSubscribers(Topic topic) {
        return PersonalizedStreamsClient.getSubscribers(this, topic, null, null);
    }
    
    public List<Subscription> getSubscribers(Topic topic, Integer limit, Integer offset) {
        return PersonalizedStreamsClient.getSubscribers(this, topic, limit, offset);
    }
    
    /* Timeline Cursor */
    public TimelineCursor getTopicStreamCursor(Topic topic) {
        return getTopicStreamCursor(topic, 50, Calendar.getInstance().getTime());
    }
    
    public TimelineCursor getTopicStreamCursor(Topic topic, Integer limit, Date date) {
        return CursorFactory.getTopicStreamCursor(this, topic, limit, date);
    }
    
    public TimelineCursor getPersonalStreamCursor(String user) {
        return getPersonalStreamCursor(user, 50, Calendar.getInstance().getTime());
    }
    
    public TimelineCursor getPersonalStreamCursor(String user, Integer limit, Date date) {
        return CursorFactory.getPersonalStreamCursor(this, user, limit, date);
    }
    
    /* Helper methods */
    public String getUrn() {
        return "urn:livefyre:"+name;
    }
    public String getUserUrn(String user) {
        return getUrn()+":user="+user;
    }
    
    private long getExpiryInSeconds(double secTillExpire) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.add(Calendar.SECOND, (int) secTillExpire);
        return cal.getTimeInMillis() / 1000L;
    }
    
    /* Getters/Setters */
    public String getNetworkName() { return getName(); } // used for the interface
    public String getName() { return name; }
    protected void setName(String name) { this.name = name; }
    public String getKey() { return key; }
    protected void setKey(String key) { this.key = key;
    }
}
