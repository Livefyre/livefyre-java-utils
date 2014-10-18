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
    public void testBuildCollection() {
        Site site = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY).getSite(SITE_ID, SITE_KEY);
        try {
            site.buildLiveCommentsCollection("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456", "", "");
            fail("titles longer than 255 char are not allowed");
        } catch (IllegalArgumentException e) {}
        try {
            site.buildLiveCommentsCollection("", "", "");
            fail("url cannot be blank");
        } catch (IllegalArgumentException e) {}
        try {
            site.buildLiveCommentsCollection("", "", "tet.com");
            fail("url must start with valid url scheme (http:// or https://)");
        } catch (IllegalArgumentException e) {}
        try {
            site.buildLiveCommentsCollection("", "", "tet.com/");
            fail("url must be a valid domain");
        } catch (IllegalArgumentException e) {}

        Collection collection  = site.buildLiveCommentsCollection(TITLE, ARTICLE_ID, URL);
        assertNotNull(collection);
    }
    
    @Test
    @Category(UnitTest.class)
    public void testBuildCollectionTypes() {
        Site site = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY).getSite(SITE_ID, SITE_KEY);
        
        Collection collection = site.buildLiveCommentsCollection(TITLE, ARTICLE_ID, URL);
        assertEquals(CollectionType.LIVECOMMENTS, collection.getData().getType());
        
        collection = site.buildLiveBlogCollection(TITLE, ARTICLE_ID, URL);
        assertEquals(CollectionType.LIVEBLOG, collection.getData().getType());
        
        collection = site.buildLiveChatCollection(TITLE, ARTICLE_ID, URL);
        assertEquals(CollectionType.LIVECHAT, collection.getData().getType());
        
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
