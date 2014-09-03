package com.livefyre.core;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.livefyre.api.client.Domain;
import com.livefyre.exceptions.LivefyreException;
import com.livefyre.repackaged.apache.commons.Base64;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class Site implements LfCore {
    private Network network = null;
    private String id = null;
    private String key = null;

    public Site(Network network, String id, String key) {
        this.network = checkNotNull(network);
        this.id = checkNotNull(id);
        this.key = checkNotNull(key);
    }
    
    /**
     * Creates and returns a Collection object. Be sure to call createOrUpdate() on it to notify Livefyre.
     */
    public Collection buildCollection(String articleId, String title, String url, Map<String, Object> options) {
        return new Collection(this, articleId, title, url, options);
    }
    
    public JSONObject getCollectionContentJson(String articleId) {
        return new JSONObject(getCollectionContent(articleId));
    }

    public String getCollectionContent(String articleId) {
        checkNotNull(articleId);

        String b64articleId = Base64.encodeBase64URLSafeString(articleId.getBytes());
        if (b64articleId.length() % 4 != 0) { 
            b64articleId = b64articleId + StringUtils.repeat("=", 4 - (b64articleId.length() % 4));
        }
        String url = String.format("%s/bs3/%s/%s/%s/init", Domain.bootstrap(this), network.getName(), id, b64articleId);

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
