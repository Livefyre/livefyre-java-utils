package com.livefyre.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.livefyre.api.client.Domain;
import com.livefyre.exceptions.LivefyreException;
import com.livefyre.exceptions.TokenException;
import com.livefyre.utils.LivefyreJwtUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class Collection {
    private static final String TOKEN_FAILURE_MSG = "Failure creating token.";
    private static final ImmutableList<String> TYPE = ImmutableList.of(
            "reviews", "sidenotes", "ratings", "counting",
            "liveblog", "livechat", "livecomments", ""
        );
    
    private Site site;
    private String collectionId;
    private String articleId;
    private String title;
    private String url;
    private Map<String, Object> options;

    /**
     * Options accepts a map of key/value pairs for your Collection. Some examples are 'tags', 'type', 'extensions',
     * 'tags', etc.  Please refer to http://answers.livefyre.com/developers/getting-started/tokens/collectionmeta/ for more info.
     * 
     * @param options map of additional params to be included into the collection meta token.
     */
    public Collection(Site site, String articleId, String title, String url, Map<String, Object> options) {
        checkNotNull(articleId);
        checkArgument(checkNotNull(title).length() <= 255, "title is longer than 255 characters.");
        checkArgument(isValidFullUrl(checkNotNull(url)),
                "url is not a valid url. see http://www.ietf.org/rfc/rfc2396.txt");
        if (options != null && (options.containsKey("type") && !TYPE.contains(options.get("type")))) {
            throw new IllegalArgumentException("type is not a recognized type. should be one of these types: " + TYPE.toString());
        }
        
        this.site = site;
        this.articleId = articleId;
        this.title = title;
        this.url = url;
        this.options = options == null ? Maps.<String, Object> newHashMap() : options;
    }
    
    /**
     * Informs Livefyre to either create or update a collection based on the attributes of this Collection.
     * @returns this
     */
    public Collection createOrUpdate() {
        ClientResponse response = invokeCollectionApi("create");
        if (response.getStatus() == 200) {
            setCollectionId(new JSONObject(response.getEntity(String.class)).getJSONObject("data").getString(
                    "collectionId"));
            return this;
        } else if (response.getStatus() == 409) {
            response = invokeCollectionApi("update");
            if (response.getStatus() == 200) {
                return this;
            }
            throw new LivefyreException("Error updating Livefyre collection. Status code: " + response.getStatus());
        }
        throw new LivefyreException("Error creating Livefyre collection. Status code: " + response.getStatus());
    }
    
    public String buildCollectionMetaToken() {
        try {
            JSONObject json = getJson();
            return LivefyreJwtUtil.serializeAndSign(site.getKey(), json);
        } catch (InvalidKeyException e) {
            throw new TokenException(TOKEN_FAILURE_MSG + e);
        }
    }
    
    public String buildChecksum() {
        try {
            JSONObject json = getJson();
            byte[] digest = MessageDigest.getInstance("MD5").digest(json.toString().getBytes());
            return printHexBinary(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new LivefyreException("Failure creating checksum." + e);
        }
    }
    
    /**
     * @return a JSONObject that contains this collection's attributes.
     */
    public JSONObject getJson() {
        Map<String, Object> attr = Maps.newTreeMap();
        attr.putAll(options);
        attr.put("articleId", articleId);
        attr.put("url", url);
        attr.put("title", title);
        
        return new JSONObject(attr);
    }
    
    /** 
     * @return a JSONObject that contains the collection's article id, checksum, and encrypted token.
     */
    public JSONObject getPayload() {
        JSONObject json = new JSONObject();
//                ImmutableMap.<String, String>of("articleId", articleId, "collectionMeta", buildCollectionMetaToken(), "checksum", buildChecksum()));
        json.put("articleId", articleId);
        json.put("checksum", buildChecksum());
        json.put("collectionMeta", buildCollectionMetaToken());
        return json;
    }

    protected boolean isValidFullUrl(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }

    private ClientResponse invokeCollectionApi(String method) {
        String uri = String.format("%s/api/v3.0/site/%s/collection/%s/", Domain.quill(site), site.getId(), method);
        ClientResponse response = Client.create()
                .resource(uri)
                .queryParam("sync", "1")
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, getPayload().toString());
        return response;
    }

    private static final char[] hexCode = "0123456789abcdef".toCharArray();

    private String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }
    
    public Site getSite() { return site; }
    protected void setSite(Site site) { this.site = site; }
    public String getCollectionId() { return collectionId; }
    protected void setCollectionId(String collectionId) { this.collectionId = collectionId; }
    public String getArticleId() { return articleId; }
    public Collection setArticleId(String articleId) { this.articleId = articleId; return this; }
    public String getTitle() { return title; }
    public Collection setTitle(String title) { this.title = title; return this; }
    public String getUrl() { return url; }
    public Collection setUrl(String url) { this.url = url; return this; }
    public Map<String, Object> getOptions() { return options; }
    public Collection setOptions(Map<String, Object> options) { this.options = options; return this; }
}
