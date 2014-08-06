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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.livefyre.exceptions.LivefyreException;
import com.livefyre.exceptions.TokenException;
import com.livefyre.repackaged.apache.commons.Base64;
import com.livefyre.utils.LivefyreJwtUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class Site implements LfCore {
    private static final String TOKEN_FAILURE_MSG = "Failure creating token.";
    private static final ImmutableList<String> TYPE = ImmutableList.of(
        "reviews", "sidenotes", "ratings", "counting",
        "liveblog", "livechat", "livecomments", ""
    );

    private Network network = null;
    private String id = null;
    private String key = null;

    public Site(Network network, String id, String key) {
        this.network = checkNotNull(network);
        this.id = checkNotNull(id);
        this.key = checkNotNull(key);
    }
    
    /**
     * This method allows a variety of parameters/options to be passed in as a map.  Some examples are 'tags', 'type', 'extensions',
     * 'tags', etc.  Please refer to http://answers.livefyre.com/developers/getting-started/tokens/collectionmeta/ for more info.
     * 
     * @param extras map of additional params to be included into the collection meta token.
     */
    public String buildCollectionMetaToken(String title, String articleId, String url, Map<String, Object> options) {
        checkArgument(checkNotNull(title).length() <= 255, "title is longer than 255 characters.");
        checkNotNull(articleId);
        checkArgument(isValidFullUrl(checkNotNull(url)), "url is not a valid url. see http://www.ietf.org/rfc/rfc2396.txt");
        
        Map<String, Object> o = options == null ? Maps.<String, Object>newHashMap() : options;

        if (o.containsKey("type") && !TYPE.contains(o.get("type"))) {
            throw new IllegalArgumentException("type is not a recognized type. should be one of these types: " +TYPE.toString());
        }
        
        Map<String, Object> data = Maps.newHashMap(o);
        data.put("url", url);
        data.put("title", title);
        data.put("articleId", articleId);
        
        try {
            return LivefyreJwtUtil.encodeLivefyreJwt(key, data);
        } catch (InvalidKeyException e) {
            throw new TokenException(TOKEN_FAILURE_MSG + e);
        }
    }

    public String buildChecksum(String title, String url, String tags) {
        checkArgument(checkNotNull(title).length() <= 255, "title is longer than 255 characters.");
        checkArgument(isValidFullUrl(checkNotNull(url)),
            "url is not a valid url. see http://www.ietf.org/rfc/rfc2396.txt");

        String t = tags == null ? "" : tags;

        try {
            JSONObject json = new JSONObject();
            json.put("url", url);
            json.put("tags", t);
            json.put("title", title);
            
            byte[] digest = MessageDigest.getInstance("MD5").digest(json.toString().getBytes());
            return printHexBinary(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new LivefyreException("Failure creating checksum." + e);
        }
    }
    
    public String createCollection(String title, String articleId, String url, Map<String, Object> options) {
        String token = buildCollectionMetaToken(title, articleId, url, options);
        String checksum =  buildChecksum(title, url, (options != null && options.containsKey("tags")) ? options.get("tags").toString() : null);
        String uri = String.format("https://%s.quill.fyre.co/api/v3.0/site/%s/collection/create/", getNetworkName(), id);
        String form = new JSONObject(ImmutableMap.<String, String>of("articleId", articleId, "collectionMeta", token, "checksum", checksum)).toString();
        
        ClientResponse response = Client.create()
                .resource(uri)
                .queryParam("sync", "1")
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, form);
        
        if (response.getStatus() != 200) {
            throw new LivefyreException("Error contacting Livefyre. Status code: " + response.getStatus());
        }
        return new JSONObject(response.getEntity(String.class)).getJSONObject("data").getString("collectionId");
    }
    
    public JSONObject getCollectionContentJson(String articleId) {
        return new JSONObject(getCollectionContent(articleId));
    }

    public String getCollectionContent(String articleId) {
        checkNotNull(articleId);

        String b64articleId = Base64.encodeBase64URLSafeString(articleId.getBytes());
        String url = String.format("https://bootstrap.livefyre.com/bs3/%s/%s/%s/init", network.getName(), id, b64articleId);

        ClientResponse response = Client.create()
                .resource(url)
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);
        if (response.getStatus() != 200) {
            throw new LivefyreException("Error contacting Livefyre. Status code: " + response.getStatus());
        }
        return response.getEntity(String.class);
    }

    public String getCollectionId(String articleId) {
        JSONObject collection = getCollectionContentJson(articleId);
        return collection.getJSONObject("collectionSettings").getString("collectionId");
    }
    
    /* Helper methods */
    public String getUrn() {
        return network.getUrn()+":site="+id;
    }
    
    protected boolean isValidFullUrl(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
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
    
    /* Getters/Setters */
    public String buildLivefyreToken() { return network.buildLivefyreToken(); }
    public String getNetworkName() { return network.getNetworkName(); }
    public Network getNetwork() { return this.network; }
    protected void setNetwork(Network network) { this.network = network; }
    public String getId() { return id; }
    protected void setId(String id) { this.id = id; }
    public String getKey() { return key; }
    protected void setKey(String key) { this.key = key; }
}
