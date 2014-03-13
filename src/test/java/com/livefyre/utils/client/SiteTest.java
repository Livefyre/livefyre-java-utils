package com.livefyre.utils.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.livefyre.utils.client.Livefyre;
import com.livefyre.utils.client.Site;
import com.livefyre.utils.core.StreamType;

public class SiteTest {
    private static final String SITE_ID = "test.fyre.com";
    private static final String SITE_KEY = "testkeytest";
    
    @Test
    public void testSiteCreation() {
        try {
            Livefyre.getSite("", null);
            fail("siteKey cannot be null");
        } catch(NullPointerException ex) {}
        try {
            Livefyre.getSite(null, "");
            fail("siteId cannot be null");
        } catch(NullPointerException ex) {}
        Site site = Livefyre.getSite("", "");
        assertNotNull(site);
    }
    
    @Test
    public void testSiteCollectionToken() {
        Site site = Livefyre.getSite(SITE_ID, SITE_KEY);
        String token = site.getCollectionMetaToken("test", "testId", "testUrl", "testTags");
        assertNotNull(token);
        assertEquals(site.getCollectionContent(token), "testUrl");
        
        token = site.getCollectionMetaToken("test", "testId", "testUrl", "testTags", StreamType.REVIEWS);
        assertNotNull(token);
        assertEquals(site.getCollectionContent(token), "testUrl");
    }
    
    @Test
    public void testNullChecks() {
        Site site = new Site();
        try {
            site.getCollectionMetaToken(null, null, null, null, null);
            fail("title cannot be null");
        } catch(NullPointerException ex) {}
        try {
            site.getCollectionMetaToken("", null, null, null, null);
            fail("articleId cannot be null");
        } catch(NullPointerException ex) {}
        try {
            site.getCollectionMetaToken("", "", null, null, null);
            fail("url cannot be null");
        } catch(NullPointerException ex) {}
        try {
            site.getCollectionMetaToken("", "", "", null, null);
            fail("tags cannot be null");
        } catch(NullPointerException ex) {}
        try {
            site.getCollectionMetaToken("", "", "", "", null);
            fail("stream cannot be null");
        } catch(NullPointerException ex) {}
        try {
            site.getCollectionMetaToken("", "", "", "", StreamType.NONE);
            fail("siteKey cannot be null");
        } catch(NullPointerException ex) {}
        try {
            site.getCollectionContent(null);
            fail("displayName cannot be null");
        } catch(NullPointerException ex) {}
        try {
            site.getCollectionContent("");
            fail("displayName cannot be null");
        } catch(NullPointerException ex) {}
    }
}
