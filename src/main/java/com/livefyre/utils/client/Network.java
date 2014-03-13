package com.livefyre.utils.client;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.Calendar;

import net.oauth.jsontoken.JsonToken;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.livefyre.utils.core.LivefyreException;
import com.livefyre.utils.core.LivefyreJwtUtil;
import com.livefyre.utils.core.TokenException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class Network {
    private static final String DEFAULT_USER = "system";
    
    private String networkName = null;
    private String networkKey = null;
    
    public Network() {}
    
    /**
     * Creates a new Network class with name and key.
     * 
     * @param networkName name of network. cannot be null
     * @param networkKey secret key for network. cannot be null
     * @throws NullPointerException if networkName or networkKey is null
     */
    public Network(String networkName, String networkKey) {
        Preconditions.checkNotNull(networkName);
        Preconditions.checkNotNull(networkKey);
        this.networkName = networkName;
        this.networkKey = networkKey;
    }
    
    /**
     * Set the URL that Livefyre will use to fetch user profile info from your user management
     * system. Be sure to set url_template with a working endpoint (see Remote Profiles) before
     * making calls to updateRemoteUser().
     * The registered “url_template” must contain the string "{id}" which will be replaced with
     * the ID of the user that’s being updated.
     * ex. url_template = “http://example.com/users/get_remote_profile?id={id}”
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
        Preconditions.checkNotNull(urlTemplate);
        Preconditions.checkNotNull(this.networkName);
        Preconditions.checkNotNull(this.networkKey);
        if (!urlTemplate.contains("{id}")) {
            throw new IllegalArgumentException("urlTemplate does not contain {id}.");
        }
        
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
        Preconditions.checkNotNull(userId);
        Preconditions.checkNotNull(this.networkName);
        ClientResponse response = Client.create()
                .resource(String.format("http://%s/api/v3_0/user/%s/refresh", this.networkName, userId))
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
     * @param userId user id for the user
     * @param displayName display name for the user
     * @param expires seconds until this token is to expire
     * @return String containing the user token
     * @throws NullPointerException if userId, displayName, expires, networkName, or networkKey is null
     * @throws TokenException if there is an issue creating the jwt
     */
    public String getUserAuthToken(String userId, String displayName, Double expires) {
        Preconditions.checkNotNull(userId);
        Preconditions.checkNotNull(displayName);
        Preconditions.checkNotNull(expires);
        Preconditions.checkNotNull(this.networkName);
        Preconditions.checkNotNull(this.networkKey);
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
        Preconditions.checkNotNull(lfToken);
        Preconditions.checkNotNull(this.networkKey);
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
    
    public String getName() {
        return networkName;
    }

    public Network setNetworkName(String name) {
        this.networkName = name;
        return this;
    }

    public String getKey() {
        return networkKey;
    }

    public Network setKey(String key) {
        this.networkKey = key;
        return this;
    }
}
