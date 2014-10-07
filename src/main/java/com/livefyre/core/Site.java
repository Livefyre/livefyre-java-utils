package com.livefyre.core;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.collect.Maps;

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
     * Creates and returns a Collection object. Be sure to call createOrUpdate() on it to inform Livefyre to
     * complete creation and any updates.
     * 
     * Options accepts a map of key/value pairs for your Collection. Some examples are 'tags',
     * 'type', 'extensions', 'tags', etc. Please refer to
     * http://answers.livefyre.com/developers/getting-started/tokens/collectionmeta/ for more info.
     *
     * @param articleId the articleId for the collection
     * @param title title for the collection.
     * @param url url for the collection.
     * @param options map of additional params to be included with the collection.
     * @return Collection
     */
    public Collection buildCollection(String title, String articleId, String url, Map<String, Object> options) {
        return new Collection(this, title, articleId, url, options == null ? Maps.<String, Object>newHashMap() : options);
    }

    /* Getters/Setters */
    public String buildLivefyreToken() {
        return network.buildLivefyreToken();
    }
    
    public String getUrn() {
        return network.getUrn() + ":site=" + id;
    }

    public Network getNetwork() {
        return this.network;
    }

    /**
     * It is preferable to use the constructor to set network.
     */
    public void setNetwork(Network network) {
        this.network = network;
    }

    public String getId() {
        return id;
    }

    /**
     * It is preferable to use the constructor to set id.
     */
    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    /**
     * It is preferable to use the constructor to set key.
     */
    public void setKey(String key) {
        this.key = key;
    }
}
