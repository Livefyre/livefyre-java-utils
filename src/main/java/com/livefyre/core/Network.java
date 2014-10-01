package com.livefyre.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.security.InvalidKeyException;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.livefyre.api.Domain;
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
    private boolean ssl = true;
    private String networkName = null;
    
    public Network(String name, String key) {
        this.name = checkNotNull(name);
        this.key = checkNotNull(key);
        this.networkName = this.name.split("\\.")[0];
    }
    
    /**
     * Updates the user sync url. Makes an external API call. 
     * 
     * @see http://answers.livefyre.com/developers/user-auth/remote-profiles/#ping-for-pull.
     * @param urlTemplate the url template to set.
     * @return true if the update was successful.
     */
    public boolean setUserSyncUrl(String urlTemplate) {
        checkArgument(checkNotNull(urlTemplate).contains(ID), "urlTemplate does not contain %s", ID);
        
        ClientResponse response = Client.create()
                .resource(String.format("%s/", Domain.quill(this)))
                .queryParam("actor_token", buildLivefyreToken())
                .queryParam("pull_profile_url", urlTemplate)
                .post(ClientResponse.class);
        return response.getStatus() == 204;
    }
    
    /**
     * Informs Livefyre to fetch user information based on the user sync url. Makes an external API call.
     * 
     * @param userId 
     * @return true if the sync was successful.
     */
    public boolean syncUser(String userId) {
        checkNotNull(userId);
        
        String url = String.format("%s/api/v3_0/user/%s/refresh", Domain.quill(this), userId);
        ClientResponse response = Client.create()
                .resource(url)
                .queryParam("lftoken", buildLivefyreToken())
                .post(ClientResponse.class);
        return response.getStatus() == 200;
    }
    
    /**
     * Generates a user auth system token.
     */
    public String buildLivefyreToken() {
        return buildUserAuthToken(DEFAULT_USER, DEFAULT_USER, DEFAULT_EXPIRES);
    }
    
    /**
     * Generates a user auth token passed on the params passed in. This method serializes the params
     * and signs the String with the Network key.
     * 
     * @param userId the user id for this token.
     * @param displayName the display name for this token.
     * @param expires when the token should expire from the time of its creation in seconds.
     * @return String
     */
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
            return LivefyreJwtUtil.serializeAndSign(key, data);
        } catch (InvalidKeyException e) {
            throw new TokenException("Failure creating token." +e);
        }
    }
    
    /**
     * Checks to see if the passed in system token is still valid.
     * 
     * @return true if the token is still valid.
     */
    public boolean validateLivefyreToken(String lfToken) {
        checkNotNull(lfToken);

        try {
            JsonObject json = LivefyreJwtUtil.decodeLivefyreJwt(key, lfToken);
            return json.get("domain").getAsString().compareTo(name) == 0
                && json.get("user_id").getAsString().compareTo("system") == 0
                && json.get("expires").getAsLong() >= Calendar.getInstance().getTimeInMillis()/1000L;
        } catch (InvalidKeyException e) {
            throw new TokenException("Failure decrypting token." +e);
        }
    }
    
    /**
     * Constructs a new Site object based on the parameters passed in.
     * 
     * @param siteId the id for the Site.
     * @param siteKey the secret key for the Site.
     * @return Site
     */
    public Site getSite(String siteId, String siteKey) {
        return new Site(this, siteId, siteKey);
    }
    
    /* Protected/private methods */
    private long getExpiryInSeconds(double secTillExpire) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.add(Calendar.SECOND, (int) secTillExpire);
        return cal.getTimeInMillis() / 1000L;
    }
    
    /* Getters/Setters */
    public String getUrn() {
        return "urn:livefyre:"+name;
    }
    
    public String getUserUrn(String user) {
        return getUrn()+":user="+user;
    }

    public String getNetworkName() {
        return this.networkName;
    }

    /**
     * It is preferable to use the constructor to set the networkName.
     */
    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getName() {
        return name;
    }
    
    /**
     * It is preferable to use the constructor to set the name.
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    /**
     * It is preferable to use the constructor to set the key.
     */
    public void setKey(String key) {
        this.key = key;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl; }
}
