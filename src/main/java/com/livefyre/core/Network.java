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
import com.livefyre.api.dto.PostDto;
import com.livefyre.api.dto.Subscription;
import com.livefyre.api.dto.Topic;
import com.livefyre.api.dto.TopicsDto;
import com.livefyre.exceptions.TokenException;
import com.livefyre.utils.LivefyreJwtUtil;

public class Network {
    private static final double DEFAULT_EXPIRES = 86400.0;
    private static final String DEFAULT_USER = "system";
    private static final String ID = "{id}";
    
    private String networkName = null;
    private String networkKey = null;
    
    public Network(String networkName, String networkKey) {
        this.networkName = checkNotNull(networkName);
        this.networkKey = checkNotNull(networkKey);
    }
    
    public boolean setUserSyncUrl(String urlTemplate) {
        checkArgument(checkNotNull(urlTemplate).contains(ID), "urlTemplate does not contain %s", ID);
        
        Form form = new Form();
        form.param("actor_token", buildLivefyreToken());
        form.param("pull_profile_url", urlTemplate);
        
        Response response = ClientBuilder.newClient()
                .target(String.format("http://%s/", this.networkName))
                .request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        
        return response.getStatus() == 204;
    }
    
    public boolean syncUser(String userId) {
        checkNotNull(userId);
        
        Response response = ClientBuilder.newClient()
                .target(String.format("http://%s/api/v3_0/user/%s/refresh", this.networkName, userId))
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
            return LivefyreJwtUtil.getJwtUserAuthToken(this.networkName, this.networkKey, userId, displayName, expires);
        } catch (InvalidKeyException e) {
            throw new TokenException("Failure creating token." +e);
        } catch (SignatureException e) {
            throw new TokenException("Failure creating token." +e);
        }
    }
    
    public boolean validateLivefyreToken(String lfToken) {
        checkNotNull(lfToken);

        try {
            JsonToken json = LivefyreJwtUtil.decodeJwt(this.networkKey, lfToken);
            JsonObject jsonObj = json.getPayloadAsJsonObject();
            return jsonObj.get("domain").getAsString().compareTo(this.networkName) == 0
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
        return PersonalizedStreamsClientImpl.getNetworkTopic(this, topicId);
    }
    
    public Topic createOrUpdateTopic(String id, String label) {
        Topic topic = new Topic(this, id, label);
        if (PersonalizedStreamsClientImpl.postNetworkTopic(this, topic)) {
            return topic;
        }
        return null;
    }
    
    public boolean deleteTopic(Topic topic) {
        return PersonalizedStreamsClientImpl.deleteNetworkTopic(this, topic);
    }
    
    /* Multiple Topic API */
    public List<Topic> getTopics() {
        return PersonalizedStreamsClientImpl.getNetworkTopics(this, null, null);
    }
    
    public List<Topic> getTopics(Integer limit, Integer offset) {
        return PersonalizedStreamsClientImpl.getNetworkTopics(this, limit, offset);
    }
    
    public List<Topic> createOrUpdateTopics(Map<String, String> topics) {
        List<Topic> list = Lists.newArrayList();
        for (String key : topics.keySet()) {
            list.add(new Topic(this, key, topics.get(key)));
        }
        
        TopicsDto result = PersonalizedStreamsClientImpl.postNetworkTopics(this, list);
        if (result.getCreated() > 0 || result.getUpdated() > 0) {
            return list;
        }
        return null;
    }
    
    public TopicsDto deleteTopics(List<Topic> topics) {
        return PersonalizedStreamsClientImpl.deleteNetworkTopics(this, topics);
    }
    
    /* Subscription API */
    public List<Subscription> getSubscriptions(String user) {
        return PersonalizedStreamsClientImpl.getSubscriptions(this, getUserUrn(user));
    }
    
    public Integer createSubscriptions(String user, List<Topic> topics) {
        return PersonalizedStreamsClientImpl.postSubscriptions(this, getUserUrn(user), topics);
    }

    public PostDto updateSubscriptions(String user, List<Topic> topics) {
        return PersonalizedStreamsClientImpl.putSubscriptions(this, getUserUrn(user), topics);
    }

    public Integer deleteSubscriptions(String user, List<Topic> topics) {
        return PersonalizedStreamsClientImpl.deleteSubscriptions(this, getUserUrn(user), topics);
    }
    
    /* Subscriber API */
    public List<String> getSubscribers(Topic topic) {
        return PersonalizedStreamsClientImpl.getSubscribers(this, topic, null, null);
    }
    
    public List<String> getSubscribers(Topic topic, Integer limit, Integer offset) {
        return PersonalizedStreamsClientImpl.getSubscribers(this, topic, limit, offset);
    }
    
    public String getUserUrn(String user) {
        return String.format("urn:livefyre:%s:user=%s", this.networkName, user);
    }
    
    /* Getters/Setters */
    public String getName() { return networkName; }
    protected void setNetworkName(String name) { this.networkName = name; }
    public String getNetworkKey() { return networkKey; }
    protected void setNetworkKey(String networkKey) { this.networkKey = networkKey;
    }
}
