package com.livefyre.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.oauth.jsontoken.JsonToken;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.livefyre.api.client.PersonalizedStreamsClientImpl;
import com.livefyre.api.entity.Subscription;
import com.livefyre.api.entity.Topic;
import com.livefyre.exceptions.TokenException;
import com.livefyre.utils.LivefyreJwtUtil;

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
        
        Form form = new Form();
        form.param("actor_token", buildLivefyreToken());
        form.param("pull_profile_url", urlTemplate);
        
        Response response = ClientBuilder.newClient()
                .target(String.format("http://%s/", this.name))
                .request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        
        return response.getStatus() == 204;
    }
    
    public boolean syncUser(String userId) {
        checkNotNull(userId);
        
        Response response = ClientBuilder.newClient()
                .target(String.format("http://%s/api/v3_0/user/%s/refresh", this.name, userId))
                .queryParam("lftoken", buildLivefyreToken())
                .request()
                .post(null);

        return response.getStatus() == 200;
    }
    
    public String buildLivefyreToken() {
        return buildUserAuthToken(DEFAULT_USER, DEFAULT_USER, DEFAULT_EXPIRES);
    }
    
    public String buildUserAuthToken(String userId, String displayName, Double expires) {
        checkArgument(StringUtils.isAlphanumeric(checkNotNull(userId)), "userId is not alphanumeric.");
        checkNotNull(displayName);
        checkNotNull(expires);
        
        try {
            return LivefyreJwtUtil.getJwtUserAuthToken(this.name, this.key, userId, displayName, expires);
        } catch (InvalidKeyException e) {
            throw new TokenException("Failure creating token." +e);
        } catch (SignatureException e) {
            throw new TokenException("Failure creating token." +e);
        }
    }
    
    public boolean validateLivefyreToken(String lfToken) {
        checkNotNull(lfToken);

        try {
            JsonToken json = LivefyreJwtUtil.decodeJwt(this.key, lfToken);
            JsonObject jsonObj = json.getPayloadAsJsonObject();
            return jsonObj.get("domain").getAsString().compareTo(this.name) == 0
                && jsonObj.get("user_id").getAsString().compareTo("system") == 0
                && jsonObj.get("expires").getAsBigInteger().compareTo(
                        BigInteger.valueOf(Calendar.getInstance().getTimeInMillis()/1000L)) >= 0;
        } catch (InvalidKeyException e) {
            throw new TokenException("Failure decrypting token." +e);
        }
    }
    
    public Site getSite(String siteId, String siteKey) {
        return new Site(this, siteId, siteKey);
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
    
    public List<Topic> createOrUpdateTopics(Map<String, String> topicMap) {
        List<Topic> list = Lists.newArrayList();
        for (String key : topicMap.keySet()) {
            list.add(new Topic(this, key, topicMap.get(key)));
        }
        
        PersonalizedStreamsClientImpl.postTopics(this, list);
        return list;
    }
    
    public Integer deleteTopics(List<Topic> topics) {
        return PersonalizedStreamsClientImpl.deleteTopics(this, topics).getDeleted();
    }
    
    /* Subscription API */
    public List<Subscription> getSubscriptions(String user) {
        return PersonalizedStreamsClientImpl.getSubscriptions(this, user);
    }
    
    public Integer addSubscriptions(String user, List<Topic> topics) {
        return PersonalizedStreamsClientImpl.postSubscriptions(this, user, topics);
    }

    public boolean updateSubscriptions(String user, List<Topic> topics) {
        return PersonalizedStreamsClientImpl.putSubscriptions(this, user, topics);
    }

    public Integer removeSubscriptions(String user, List<Topic> topics) {
        return PersonalizedStreamsClientImpl.deleteSubscriptions(this, user, topics);
    }
    
    /* Subscriber API */
    public List<Subscription> getSubscribers(Topic topic) {
        return PersonalizedStreamsClientImpl.getSubscribers(this, topic, null, null);
    }
    
    public List<Subscription> getSubscribers(Topic topic, Integer limit, Integer offset) {
        return PersonalizedStreamsClientImpl.getSubscribers(this, topic, limit, offset);
    }
    
    /* Helper methods */
    public String getUrn() {
        return "urn:livefyre:"+name;
    }
    public String getUserUrn(String user) {
        return getUrn()+":user="+user;
    }
    
    /* Getters/Setters */
    public String getNetworkName() { return getName(); } // used for the interface
    public String getName() { return name; }
    protected void setName(String name) { this.name = name; }
    public String getKey() { return key; }
    protected void setKey(String key) { this.key = key;
    }
}
