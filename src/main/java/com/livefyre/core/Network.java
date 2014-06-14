package com.livefyre.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.List;

import javax.ws.rs.core.Response;

import net.oauth.jsontoken.JsonToken;

import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.google.gson.JsonObject;
import com.livefyre.api.client.PersonalizedStreamsClientImpl;
import com.livefyre.api.dto.CollectionTopicDto;
import com.livefyre.api.dto.Subscription;
import com.livefyre.api.dto.Topic;
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
        
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(String.format("http://%s/", this.networkName));
        
        // hack to get around resteasy complaining about brackets
        try {
            urlTemplate = URLEncoder.encode(urlTemplate, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return false;
        }
        Response response = target
                .queryParam("actor_token", buildLivefyreToken())
                .queryParam("pull_profile_url", urlTemplate)
                .request()
                .post(null);
        
        return response.getStatus() == 204;
    }
    
    public boolean syncUser(String userId) {
        checkNotNull(userId);
        
        String url = String.format("http://%s/api/v3_0/user/%s/refresh", this.networkName, userId);
        ResteasyClient client = new ResteasyClientBuilder().build();
        Response response = client.target(url)
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
    
    public boolean addOrUpdateTopic(Topic topic) {
        return PersonalizedStreamsClientImpl.postNetworkTopic(this, topic);
    }
    
    public boolean deleteTopic(Topic topic) {
        return PersonalizedStreamsClientImpl.deleteNetworkTopic(this, topic);
    }
    
    /* Multiple Topic API */
    public List<Topic> getTopics(Integer limit, Integer offset) {
        return PersonalizedStreamsClientImpl.getNetworkTopics(this, limit, offset);
    }
    
    public CollectionTopicDto postTopics(List<Topic> topics) {
        return PersonalizedStreamsClientImpl.postNetworkTopics(this, topics);
    }
    
    public CollectionTopicDto deleteTopics(List<Topic> topics) {
        return PersonalizedStreamsClientImpl.deleteNetworkTopics(this, topics);
    }
    
    /* Subscription API */
    public List<Subscription> getSubscriptions(String user) {
        return PersonalizedStreamsClientImpl.getSubscriptions(this, user);
    }
    
    public CollectionTopicDto postSubscriptions(String user, List<Topic> topics) {
        return PersonalizedStreamsClientImpl.postSubscriptions(this, user, topics);
    }
    
    public CollectionTopicDto putSubscriptions(String user, List<Topic> topics) {
        return PersonalizedStreamsClientImpl.putSubscriptions(this, user, topics);
    }

    public CollectionTopicDto deleteSubscriptions(String user, List<Topic> topics) {
        return PersonalizedStreamsClientImpl.deleteSubscriptions(this, user, topics);
    }
    
    /* Subscriber API */
    public List<String> getSubscribers(Topic topic) {
        return PersonalizedStreamsClientImpl.getSubscribers(this, topic);
    }
    
    public CollectionTopicDto postSubscribers(Topic topic, List<String> users) {
        return PersonalizedStreamsClientImpl.postSubscribers(this, topic, users);
    }
    
    public CollectionTopicDto putSubscribers(Topic topic, List<String> users) {
        return PersonalizedStreamsClientImpl.putSubscribers(this, topic, users);
    }

    public CollectionTopicDto deleteSubscribers(Topic topic, List<String> users) {
        return PersonalizedStreamsClientImpl.deleteSubscribers(this, topic, users);
    }
    
    /* Getters/Setters */
    public String getName() { return networkName; }
    protected void setNetworkName(String name) { this.networkName = name; }
    public String getNetworkKey() { return networkKey; }
    protected void setNetworkKey(String networkKey) { this.networkKey = networkKey;
    }
}
