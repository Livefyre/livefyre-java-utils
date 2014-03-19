package com.livefyre.utils.client;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.security.InvalidKeyException;
import java.security.SignatureException;

import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;

import com.google.common.net.InternetDomainName;
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
    private static final String[] DOMAIN_SCHEME = {"http://", "https://"};
    
    private String networkName = null;
    private String siteId = null;
    private String siteKey = null;
    
    /**
     * Creates a new Site class with given siteId and key.
     * 
     * @param siteId site id for site. cannot be null
     * @param siteKey secret key for site. cannot be null
     * @throws NullPointerException if siteId or key is null
     */
    public Site(String networkName, String siteId, String siteKey) {
        this.networkName = checkNotNull(networkName);
        this.siteId = checkNotNull(siteId);
        this.siteKey = checkNotNull(siteKey);
    }
    
    /**
     * @see buildCollectionMetaToken(String title, String articleId, String url, String tags, StreamType stream). calls
     * that method with stream defaulted to NONE.
     */
    public String buildCollectionMetaToken(String title, String articleId, String url, String tags) {
        return buildCollectionMetaToken(title, articleId, url, tags, StreamType.NONE);
    }
    
    /**
     * Creates a Livefyre collection meta token. Pass this token to any page on your site that displays a Livefyre app 
     * (comment, blog, reviews, etc.). In particular, Livefyre uses the token to instantiate a new collection on your pages.
     * If the collection exists already, Livefyre will update the collection with the latest values in the token.
     * 
     * @param title title for collection. cannot be longer than 255 characters. should be html-encoded
     * @param articleId article id for collection
     * @param url url for collection. must be valid domain and start with a valid scheme (http:// or https://)
     * @param tags tags for collection
     * @param stream stream enum for collection (only NONE or reviews at this point)
     * @return String containing the collection meta token
     * @throws NullPointerException if title, articleId, url, tags, stream, or siteKey is null
     * @throws IllegalArgumentException if url is not a valid domain or if title is longer than 255 char.
     * @throws TokenException if there is an issue creating the token
     */
    public String buildCollectionMetaToken(String title, String articleId, String url, String tags, StreamType stream) {
        checkArgument(checkNotNull(title).length() <= 255, "title is longer than 255 characters.");
        checkNotNull(articleId);
        checkArgument(isValidFullUrl(checkNotNull(url)), "url is not a valid domain. see http://www.ietf.org/rfc/rfc3490.txt.");
        checkNotNull(tags);
        checkNotNull(stream);
        checkNotNull(this.siteKey);
        
        try {
            return LivefyreJwtUtil.getJwtCollectionMetaToken(this.siteKey, title, tags, url, articleId, stream);
        } catch (InvalidKeyException e) {
            throw new TokenException(TOKEN_FAILURE_MSG +e);
        } catch (SignatureException e) {
            throw new TokenException(TOKEN_FAILURE_MSG +e);
        }
    }

    /**
     * Helper method for getCollectionContent. Returns a JsonObject which is typically more manageable.
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
     * GET http://bootstrap.{network}/bs3/{network}/{siteId}/{b64articleId}/init
     * 
     * @param articleId articleId for the content to be retrieved
     * @return String containing collection content
     * @throws NullPointerException if articleId or siteId is null
     * @throws TokenException if there is an issue decrypting the token
     * @throws LivefyreException if there is an issue contacting Livefyre
     * @see <a href="http://docs.livefyre.com/developers/reference/http-reference/#section-22">documentation</a>
     */
    public String getCollectionContent(String articleId) {
        checkNotNull(articleId);
        checkNotNull(this.siteId);
        checkNotNull(this.networkName);
        
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
    
    private boolean isValidFullUrl(String url) {
        for (String prefix : DOMAIN_SCHEME) {
            if (url.startsWith(prefix)) {
                return InternetDomainName.isValid(url.replaceFirst(prefix, ""));
            }
        }
        return false;
    }
    
    /* Getters/Setters */
    public String getNetworkName() {
        return networkName;
    }

    protected void setNetworkName(String networkName) {
        this.networkName = networkName;
    }
    
    public String getSiteId() {
        return siteId;
    }

    protected void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSiteKey() {
        return siteKey;
    }

    protected void setSiteKey(String key) {
        this.siteKey = key;
    }
}
