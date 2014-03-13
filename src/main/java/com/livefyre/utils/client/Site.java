package com.livefyre.utils.client;

import java.security.InvalidKeyException;
import java.security.SignatureException;

import net.oauth.jsontoken.JsonToken;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.livefyre.utils.core.LivefyreJwtUtil;
import com.livefyre.utils.core.StreamType;
import com.livefyre.utils.core.TokenException;

public class Site {
    private static final String TOKEN_FAILURE_MSG = "Failure creating token.";
    
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
    public Site(String siteId, String siteKey) {
        Preconditions.checkNotNull(siteId);
        Preconditions.checkNotNull(siteKey);
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
     * Gets collection content (SEO) for this Site. siteKey must be set before calling this method.
     * Get user generated content for a pre-existing collection. Returns user generated content (UGC) as an HTML fragment.
     * Customers can embed the UGC on the page that’s returned in the initial response so crawlers can index the UGC
     * content without executing javascript.
     * 
     * Note, only use getContent if you want to make UGC available to crawlers that don’t execute javascript.
     * livefyre.js handles displaying collection content on article pages otherwise.
     * 
     * @param collectionMetaToken collection token to be decrypted
     * @return collection content
     * @throws NullPointerException if collectionMetaToken or siteKey is null
     * @throws TokenException if there is an issue decrypting the token
     */
    public String getCollectionContent(String collectionMetaToken) {
        Preconditions.checkNotNull(collectionMetaToken);
        Preconditions.checkNotNull(this.siteKey);
        try {
            JsonToken json = LivefyreJwtUtil.decodeJwt(this.siteKey, collectionMetaToken);
            JsonObject jsonObj = json.getPayloadAsJsonObject();
            
            return jsonObj.get("url").getAsString();
        } catch (InvalidKeyException e) {
            throw new TokenException("Failure decrypting token." +e);
        }
    }
    
    public String getSiteId() {
        return siteId;
    }

    public Site setSiteId(String siteId) {
        this.siteId = siteId;
        return this;
    }

    public String getKey() {
        return siteKey;
    }

    public Site setKey(String key) {
        this.siteKey = key;
        return this;
    }
}
