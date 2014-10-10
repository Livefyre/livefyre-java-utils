package com.livefyre.core;

import com.livefyre.model.SiteData;
import com.livefyre.type.CollectionType;
import com.livefyre.validator.ReflectiveValidator;

public class Site implements LfCore {
    private Network network;
    private SiteData data;

    public Site(Network network, SiteData data) {
        this.network = network;
        this.data = data;
    }

    public static Site init(Network network, String siteId, String siteKey) {
        SiteData data = new SiteData(siteId, siteKey);
        return new Site(network, ReflectiveValidator.validate(data));
    }

    /* Default collection type */
    public Collection buildLiveCommentsCollection(String title, String articleId, String url) {
        return buildCollection(title, articleId, url, CollectionType.LIVECOMMENTS);
    }
    
    public Collection buildLiveBlogCollection(String title, String articleId, String url) {
        return buildCollection(title, articleId, url, CollectionType.LIVEBLOG);
    }
    
    public Collection buildLiveChatCollection(String title, String articleId, String url) {
        return buildCollection(title, articleId, url, CollectionType.LIVECHAT);
    }
    
    public Collection buildCountingCollection(String title, String articleId, String url) {
        return buildCollection(title, articleId, url, CollectionType.COUNTING);
    }
    
    public Collection buildRatingsCollection(String title, String articleId, String url) {
        return buildCollection(title, articleId, url, CollectionType.RATINGS);
    }
    
    public Collection buildReviewsCollection(String title, String articleId, String url) {
        return buildCollection(title, articleId, url, CollectionType.REVIEWS);
    }
    
    public Collection buildSidenotesCollection(String title, String articleId, String url) {
        return buildCollection(title, articleId, url, CollectionType.SIDENOTES);
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
     * @return Collection
     */
    public Collection buildCollection(String title, String articleId, String url, CollectionType type) {
        return Collection.init(this, type, title, articleId, url);
    }
    
    //build different collection types here

    /* Getters/Setters */
    public String getUrn() {
        return network.getUrn() + ":site=" + data.getId();
    }
    
    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public SiteData getData() {
        return data;
    }

    public void setData(SiteData data) {
        this.data = data;
    }
}
