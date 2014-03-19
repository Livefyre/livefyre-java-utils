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
import com.livefyre.utils.core.LivefyreException;
import com.livefyre.utils.core.LivefyreJwtUtil;
import com.livefyre.utils.core.TokenException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class Network {
    private static final String DEFAULT_USER = "system";
    private static final String ID = "{id}";
    
    private String networkName = null;
    private String networkKey = null;
    
    /**
     * Creates a new Network class with name and key.
     * 
     * @param networkName name of network. cannot be null
     * @param networkKey secret key for network. cannot be null
     * @throws NullPointerException if networkName or networkKey is null
     */
    public Network(String networkName, String networkKey) {
        this.networkName = checkNotNull(networkName);
        this.networkKey = checkNotNull(networkKey);
    }
    
    /**
     * Set the URL that Livefyre will use to fetch user profile info from your user management
     * system. Be sure to set urlTemplate with a working endpoint (see Remote Profiles) before
     * making calls to updateRemoteUser().
     * The registered “urlTemplate” must contain the string "{id}" which will be replaced with
     * the ID of the user that’s being updated.
     * ex. urlTemplate = “http://example.com/users/get_remote_profile?id={id}”
     * 
     * @param urlTemplate template that Livefyre will use to fetch user profile info. must not be
     *            null
     * @returns this Network class
     * @throws TokenException if there is a failure creating the token
     * @throws IllegalArgumentException if urlTemplate does not contain {id}
     * @throws NullPointerException if urlTemplate, this.name, or this.key are null
     * @throws LivefyreException if there is an issue contacting Livefyre
     * @see <a href="http://docs.livefyre.com/developers/user-auth/remote-profiles/#ping-for-pull">documentation</a>
     */

    public Network setUserSyncUrl(String urlTemplate) {
        checkArgument(checkNotNull(urlTemplate).contains(ID), "urlTemplate does not contain %s", ID);
        checkNotNull(this.networkName);
        checkNotNull(this.networkKey);
        
        String token;
        try {
            token = LivefyreJwtUtil.getJwtUserAuthToken(this.networkName, this.networkKey, DEFAULT_USER, DEFAULT_USER, 86400);
        } catch (InvalidKeyException e) {
            throw new TokenException("Failure creating token." +e);
        } catch (SignatureException e) {
            throw new TokenException("Failure creating token." +e);
        }
        ClientResponse response = Client.create()
                .resource(String.format("http://%s/", this.networkName))
                .queryParam("actor_token", token)
                .queryParam("pull_profile_url", urlTemplate)
                .post(ClientResponse.class);
        if (response.getStatus() != 204) {
            throw new LivefyreException("Error contacting Livefyre. Status code: " +response.getStatus());
        }
        return this;
    }
    
    /**
     * Pings Livefyre with a user id stored in your user management system, prompting Livefyre to
     * pull the latest user profile data from the customer user management system. See the
     * setUserSyncUrl() method to add your pull URL to Livefyre.
     * 
     * @param userId user id for the user
     * @return true if Livefyre was successfully pinged. false otherwise
     * @throws NullPointerException if userId or networkName is null
     * @throws LivefyreException if there is an issue contacting Livefyre
     * @see <a href="http://docs.livefyre.com/developers/user-auth/remote-profiles/#ping-for-pull">documentation</a>
     */
    public Network syncUser(String userId) {
        checkNotNull(userId);
        checkNotNull(this.networkName);
        
        String token;
        try {
            token = LivefyreJwtUtil.getJwtUserAuthToken(this.networkName, this.networkKey, DEFAULT_USER, DEFAULT_USER, 86400);
        } catch (InvalidKeyException e) {
            throw new TokenException("Failure creating token." +e);
        } catch (SignatureException e) {
            throw new TokenException("Failure creating token." +e);
        }
        ClientResponse response = Client.create()
                .resource(String.format("http://%s/api/v3_0/user/%s/refresh", this.networkName, userId))
                .queryParam("lftoken", token)
                .post(ClientResponse.class);
        if (response.getStatus() != 200) {
            throw new LivefyreException("Error contacting Livefyre. Status code: " +response.getStatus());
        }
        return this;
    }
    
    /**
     * Creates a Livefyre user token. It is recommended that this is called after the user
     * is authenticated.
     * 
     * @param userId user id for the user. must be alphanumeric
     * @param displayName display name for the user
     * @param expires seconds until this token is to expire
     * @return String containing the user token
     * @throws NullPointerException if userId, displayName, expires, networkName, or networkKey is null
     * @throws IllegalArgumentException if userId is not alphanumeric
     * @throws TokenException if there is an issue creating the jwt
     */
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
    
    /**
     * Validates a Livefyre token as a valid token for this Network.
     * 
     * @param lfToken token to be validated
     * @return true if lfToken is a valid and current Livefyre token, false otherwise
     * @throws TokenException if there is an issue decrypting the token
     */
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

    /**
     * Returns an instance of a Livefyre site object.
     * 
     * @param siteId Livefyre-provided site id
     * @param siteKey The Livefyre-provided key for this particular site.
     * @return a Site object
     */
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
