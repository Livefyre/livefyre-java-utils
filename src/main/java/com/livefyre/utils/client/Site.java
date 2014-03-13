package com.livefyre.utils.client;

import java.security.InvalidKeyException;
import java.security.SignatureException;

import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.livefyre.utils.core.LivefyreException;
import com.livefyre.utils.core.LivefyreJwtUtil;
import com.livefyre.utils.core.StreamType;
import com.livefyre.utils.core.TokenException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class Site {
    private static final String TOKEN_FAILURE_MSG = "Failure creating token.";
    
    private String networkName = null;
    private String siteId = null;
    private String siteKey = null;
    
    public Site() {}
    
    /**
     * Creates a new Site class with given siteId and key.
     * 
     * @param siteId site id for site. cannot be null
     * @param siteKey secret key for site. cannot be null
     * @throws NullPointerException if siteId or key is null
     */
    public Site(String networkName, String siteId, String siteKey) {
        Preconditions.checkNotNull(networkName);
        Preconditions.checkNotNull(siteId);
        Preconditions.checkNotNull(siteKey);
        this.networkName = networkName;
        this.siteId = siteId;
        this.siteKey = siteKey;
    }
    
    public String getCollectionMetaToken(String title, String articleId, String url, String tags) {
        return getCollectionMetaToken(title, articleId, url, tags, StreamType.NONE);
    }
    
    /**
     * Creates a Livefyre collection meta token. Pass this token to any page on your site that displays a Livefyre app 
     * (comment, blog, reviews, etc.). In particular, Livefyre uses the token to instantiate a new collection on your pages.
     * If the collection exists already, Livefyre will update the collection with the latest values in the token.
     * 
     * @param userId user id for the user
     * @param displayName display name for the user
     * @param expires seconds until this token is to expire
     * @return String containing the user token
     * @throws NullPointerException if title, articleId, url, tags, stream, or siteKey is null
     * @throws TokenException if there is an issue creating the token
     */
    public String getCollectionMetaToken(String title, String articleId, String url, String tags, StreamType stream) {
        Preconditions.checkNotNull(title);
        Preconditions.checkNotNull(articleId);
        Preconditions.checkNotNull(url);
        Preconditions.checkNotNull(tags);
        Preconditions.checkNotNull(stream);
        Preconditions.checkNotNull(this.siteKey);
        
        try {
            return LivefyreJwtUtil.getJwtCollectionMetaToken(this.siteKey, title, tags, url, articleId, stream);
        } catch (InvalidKeyException e) {
            throw new TokenException(TOKEN_FAILURE_MSG +e);
        } catch (SignatureException e) {
            throw new TokenException(TOKEN_FAILURE_MSG +e);
        }
    }
    
    /**
     * Helper method for getCollectionContent. Returns a JsonObject which is typically more managable.
     * @param articleId articleId for the content to be retrieved
     * @return JsonObject containing collection content
     * @see getCollectionContent(String articleId)
     */
    public JsonObject getCollectionContentJson(String articleId) {
        return (JsonObject) new JsonParser().parse(getCollectionContent(articleId));
    }
    
    /**
     * Gets collection content (SEO) for this Site. siteKey must be set before calling this method.
     * Get user generated content for a pre-existing collection. Returns user generated content (UGC) as an HTML fragment.
     * Customers can embed the UGC on the page that’s returned in the initial response so crawlers can index the UGC
     * content without executing javascript.
     * 
     * Note, only use getContent if you want to make UGC available to crawlers that don’t execute javascript.
     * livefyre.js handles displaying collection content on article pages otherwise.
     * 
     * GET http://bootstrap.{network}/bs3/{network}/{siteId}/{b64articleId}/init/
     * 
     * @param articleId articleId for the content to be retrieved
     * @return String containing collection content
     * @throws NullPointerException if articleId or siteId is null
     * @throws TokenException if there is an issue decrypting the token
     * @throws LivefyreException if there is an issue contacting Livefyre
     * @see <a href="http://docs.livefyre.com/developers/reference/http-reference/#section-22">documentation</a>
     */
    public String getCollectionContent(String articleId) {
        Preconditions.checkNotNull(articleId);
        Preconditions.checkNotNull(this.siteId);
        Preconditions.checkNotNull(this.networkName);
        
        ClientResponse response = Client.create()
                .resource(String.format("http://bootstrap.%1$s/bs3/%1$s/%2$s/%3$s/init",
                        this.networkName, this.siteId, Base64.encodeBase64URLSafeString(articleId.getBytes())))
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);
        if (response.getStatus() != 200) {
            throw new LivefyreException("Error contacting Livefyre. Status code: " +response.getStatus());
        }
        return response.getEntity(String.class);
    }
    
    public String getNetworkName() {
        return networkName;
    }

    public Site setNetworkName(String networkName) {
        this.networkName = networkName;
        return this;
    }
    
    public String getSiteId() {
        return siteId;
    }

    public Site setSiteId(String siteId) {
        this.siteId = siteId;
        return this;
    }

    public String getSiteKey() {
        return siteKey;
    }

    public Site setSiteKey(String key) {
        this.siteKey = key;
        return this;
    }
}
