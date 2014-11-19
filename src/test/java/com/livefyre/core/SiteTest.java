package com.livefyre.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.livefyre.Livefyre;
import com.livefyre.config.PojoTest;
import com.livefyre.config.UnitTest;
import com.livefyre.type.CollectionType;

public class SiteTest extends PojoTest<Site> {
    @Test
    @Category(UnitTest.class)
    public void testGetSite() {
        Network network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
        try {
            Site.init(network, SITE_ID, null);
            fail("siteKey cannot be null");
        } catch(IllegalArgumentException e) {}
        try {
            Site.init(network, null, SITE_KEY);
            fail("siteId cannot be null");
        } catch(IllegalArgumentException e) {}
        Site site = network.getSite(SITE_ID, SITE_KEY);
        assertNotNull(site);
        assertEquals(SITE_ID, site.getData().getId());
        assertEquals(SITE_KEY, site.getData().getKey());
    }
    
    @Test
    @Category(UnitTest.class)
    public void testBuildCollectionTypes() {
        Site site = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY).getSite(SITE_ID, SITE_KEY);
        
        Collection collection = site.buildCommentsCollection(TITLE, ARTICLE_ID, URL);
        assertEquals(CollectionType.COMMENTS, collection.getData().getType());
        
        collection = site.buildBlogCollection(TITLE, ARTICLE_ID, URL);
        assertEquals(CollectionType.BLOG, collection.getData().getType());
        
        collection = site.buildChatCollection(TITLE, ARTICLE_ID, URL);
        assertEquals(CollectionType.CHAT, collection.getData().getType());
        
        collection = site.buildCountingCollection(TITLE, ARTICLE_ID, URL);
        assertEquals(CollectionType.COUNTING, collection.getData().getType());
        
        collection = site.buildRatingsCollection(TITLE, ARTICLE_ID, URL);
        assertEquals(CollectionType.RATINGS, collection.getData().getType());
        
        collection = site.buildReviewsCollection(TITLE, ARTICLE_ID, URL);
        assertEquals(CollectionType.REVIEWS, collection.getData().getType());
        
        collection = site.buildSidenotesCollection(TITLE, ARTICLE_ID, URL);
        assertEquals(CollectionType.SIDENOTES, collection.getData().getType());
        
        collection = site.buildCollection(CollectionType.COUNTING, TITLE, ARTICLE_ID, URL);
        assertEquals(CollectionType.COUNTING, collection.getData().getType());
    }
    
    @Test
    @Category(UnitTest.class)
    public void testUrn() {
        Site site = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY).getSite(SITE_ID, SITE_KEY);
        assertEquals(site.getNetwork().getUrn()+":site="+SITE_ID, site.getUrn());
    }
}
