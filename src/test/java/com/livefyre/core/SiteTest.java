package com.livefyre.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Calendar;

import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

import com.livefyre.Livefyre;
import com.livefyre.config.LfTest;

public class SiteTest extends LfTest {
    @Test
    @Ignore
    public void testGetCollectionInfo() {
        Site site = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY).getSite(SITE_ID, SITE_KEY);
        String collectionContent = site.getCollectionContent(ARTICLE_ID);
        
        assertNotNull(collectionContent);
        
        JSONObject collectionJson = site.getCollectionContentJson(ARTICLE_ID);
        
        assertNotNull(collectionJson);
    }
    
    @Test
    @Ignore
    public void testCreateUpdateCollection() {
        Site site = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY).getSite(SITE_ID, SITE_KEY);
        String name = "JavaCreateCollection" + Calendar.getInstance().getTime();

        Collection collection = site.createCollection(name, name, "http://answers.livefyre.com/JAVA", null);
        String otherId = site.getCollectionId(name);
        
        assertEquals(otherId, collection.getCollectionId());

//        id = site.createOrUpdateCollection(name, name, "http://answers.livefyre.com/JAVA", ImmutableMap.<String, Object>of("tags", "super"));
//        
//        assertEquals(otherId, id);
    }
    
    @Test
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
