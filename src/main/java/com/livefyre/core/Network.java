package com.livefyre.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.livefyre.api.Domain;
import com.livefyre.exceptions.ApiException;
import com.livefyre.model.NetworkData;
import com.livefyre.utils.LivefyreUtil;
import com.livefyre.validator.ReflectiveValidator;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class Network implements LfCore {
    private static final double DEFAULT_EXPIRES = 86400.0;
    private static final String DEFAULT_USER = "system";
    private static final String ID = "{id}";
    private static final String ALPHA_DASH_UNDER_DOT_REGEX = "^[a-zZA-Z0-9_\\.-]+$";
    
    private NetworkData data;
    private Boolean ssl = true;
    
    public Network(NetworkData data) {
        this.data = data;
    }
    
    public static Network init(String name, String key) {
        NetworkData data = new NetworkData(name, key);
        return new Network(ReflectiveValidator.validate(data));
    }
    
    /**
     * Updates the user sync url. Makes an external API call. 
     * http://answers.livefyre.com/developers/user-auth/remote-profiles/#ping-for-pull.
     * 
     * @param urlTemplate the url template to set.
     */
    public void setUserSyncUrl(String urlTemplate) {
        checkArgument(checkNotNull(urlTemplate).contains(ID), "urlTemplate does not contain %s", ID);
        
        ClientResponse response = Client.create()
                .resource(String.format("%s/", Domain.quill(this)))
                .queryParam("actor_token", buildLivefyreToken())
                .queryParam("pull_profile_url", urlTemplate)
                .post(ClientResponse.class);
        if (response.getStatus() >= 400) {
            throw new ApiException(response.getStatus());
        }
    }
    
    /**
     * Informs Livefyre to fetch user information based on the user sync url. Makes an external API call.
     * 
     * @param userId the userId for the user to sync
     * @return true if the sync was successful.
     */
    public Network syncUser(String userId) {
        checkNotNull(userId);
        
        String url = String.format("%s/api/v3_0/user/%s/refresh", Domain.quill(this), userId);
        ClientResponse response = Client.create()
                .resource(url)
                .queryParam("lftoken", buildLivefyreToken())
                .post(ClientResponse.class);
        if (response.getStatus() >= 400) {
            throw new ApiException(response.getStatus());
        }
        return this;
    }
    
    /**
     * Generates a user auth system token.
     * 
     * @return a default system token
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
        Pattern pattern = Pattern.compile(ALPHA_DASH_UNDER_DOT_REGEX);
        checkArgument(pattern.matcher(checkNotNull(userId)).find(),
                "userId is not valid. be sure the userId matches the following pattern: %s", ALPHA_DASH_UNDER_DOT_REGEX);
        checkNotNull(displayName);
        checkNotNull(expires);

        Map<String, Object> claims = ImmutableMap.<String, Object>of(
                "domain", data.getName(),
                "user_id", userId,
                "display_name", displayName,
                "expires", getExpiryInSeconds(expires)
            );

        return LivefyreUtil.serializeAndSign(claims, data.getKey());
    }

    /**
     * Checks to see if the passed in system token is still valid.
     * 
     * @param lfToken the system token to validate
     * 
     * @return true if the token is still valid.
     */
    public boolean validateLivefyreToken(String lfToken) {
        checkNotNull(lfToken);

        JsonObject json = LivefyreUtil.decodeJwt(lfToken, data.getKey());
        return json.get("domain").getAsString().compareTo(data.getName()) == 0
            && json.get("user_id").getAsString().compareTo("system") == 0
            && json.get("expires").getAsLong() >= Calendar.getInstance().getTimeInMillis()/1000L;
    }
    
    /**
     * Constructs a new Site object based on the parameters passed in.
     * 
     * @param siteId the id for the Site.
     * @param siteKey the secret key for the Site.
     * @return Site
     */
    public Site getSite(String siteId, String siteKey) {
        return Site.init(this, siteId, siteKey);
    }
    
    public String getUrn() {
        return "urn:livefyre:"+data.getName();
    }
    
    public String getUrnForUser(String user) {
        return getUrn()+":user="+user;
    }
    
    public String getNetworkName() {
        return data.getName().split("\\.")[0];
    }

    public Boolean isSsl() {
        return ssl;
    }

    public void setSsl(Boolean ssl) {
        this.ssl = ssl;
    }

    public NetworkData getData() {
        return data;
    }
    
    public void setData(NetworkData data) {
        this.data = data;
    }

    /* Protected/private methods */
    private long getExpiryInSeconds(double secTillExpire) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.add(Calendar.SECOND, (int) secTillExpire);
        return cal.getTimeInMillis() / 1000L;
    }
}
