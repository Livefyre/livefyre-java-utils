package com.livefyre.core;

import com.livefyre.model.CollectionType;
import com.livefyre.model.SiteData;
import com.livefyre.validators.Validator;

public class Site implements LfCore {
    private SiteData data;

    public Site(SiteData data) {
        this.data = data;
    }

    public static Site init(Network network, String siteId, String siteKey) {
        SiteData data = new SiteData(network, siteId, siteKey);
        return new Site(Validator.validate(data));
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
    public Collection buildCollection(String title, String articleId, String url) {
        return Collection.init(this, CollectionType.LIVECOMMENTS, title, articleId, url);
    }
    
    //build different collection types here

    /* Getters/Setters */
    public String getUrn() {
        return data.getNetwork().getUrn() + ":site=" + data.getId();
    }
    
    public SiteData getData() {
        return data;
    }

    public void setData(SiteData data) {
        this.data = data;
    }
}
