package com.livefyre.utils.client;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.Calendar;

import net.oauth.jsontoken.JsonToken;

import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonObject;
import com.livefyre.utils.core.LivefyreJwtUtil;
import com.livefyre.utils.core.TokenException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

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
        checkNotNull(this.networkName);
        checkNotNull(this.networkKey);
        
        ClientResponse response = Client.create()
            .resource(String.format("http://%s/", this.networkName))
            .queryParam("actor_token", buildLivefyreToken())
            .queryParam("pull_profile_url", urlTemplate)
            .post(ClientResponse.class);
        return response.getStatus() == 204;
    }
    
    public boolean syncUser(String userId) {
        checkNotNull(userId);
        checkNotNull(this.networkName);
        
        ClientResponse response = Client.create()
            .resource(String.format("http://%s/api/v3_0/user/%s/refresh", this.networkName, userId))
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
        checkNotNull(this.networkName);
        checkNotNull(this.networkKey);
        
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
        checkNotNull(this.networkKey);
        
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
        return new Site(this.networkName, siteId, siteKey);
    }
    
    /* Getters/Setters */
    public String getName() {
        return networkName;
    }

    protected void setNetworkName(String name) {
        this.networkName = name;
    }

    public String getNetworkKey() {
        return networkKey;
    }

    protected void setNetworkKey(String networkKey) {
        this.networkKey = networkKey;
    }
}
