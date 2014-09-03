package com.livefyre.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.livefyre.Livefyre;
import com.livefyre.config.IntegrationTest;
import com.livefyre.config.LfTest;
import com.livefyre.config.UnitTest;

public class SiteTest extends LfTest {
    @Test
    @Category(IntegrationTest.class)
    public void testGetCollectionInfo() {
        Site site = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY).getSite(SITE_ID, SITE_KEY);
        String collectionContent = site.getCollectionContent(ARTICLE_ID);
        assertNotNull(collectionContent);
        
        JSONObject collectionJson = site.getCollectionContentJson(ARTICLE_ID);
        assertNotNull(collectionJson);
        
        String collectionId = site.getCollectionId(ARTICLE_ID);
        assertEquals(COLLECTION_ID, collectionId);
    }
    
    @Test
    @Category(UnitTest.class)
    public void testBuildCollection() {
        Site site = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY).getSite(SITE_ID, SITE_KEY);
        Collection collection  = site.buildCollection(ARTICLE_ID, "", URL, null);
        assertNotNull(collection);
    }
    
    @Test
    @Category(UnitTest.class)
    public void testSiteCreation() {
        Network network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
        try {
            network.getSite(SITE_ID, null);
            fail("siteKey cannot be null");
        } catch(NullPointerException e) {}
        try {
            network.getSite(null, SITE_KEY);
            fail("siteId cannot be null");
        } catch(NullPointerException e) {}
        Site site = network.getSite(SITE_ID, SITE_KEY);
        assertNotNull(site);
    }
}
