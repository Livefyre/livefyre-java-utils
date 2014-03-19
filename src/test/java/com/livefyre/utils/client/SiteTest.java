package com.livefyre.utils.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.security.InvalidKeyException;

import org.junit.Test;

import com.livefyre.utils.core.LivefyreJwtUtil;
import com.livefyre.utils.core.StreamType;

public class SiteTest {
    private static final String SITE_ID = "1";
    private static final String SITE_KEY = "testkeytest";
    
    @Test
    public void testSiteCreation() {
        Network network = Livefyre.getNetwork("", "");
        try {
            network.getSite("", null);
            fail("siteKey cannot be null");
        } catch(NullPointerException ex) {}
        try {
            network.getSite(null, "");
            fail("siteId cannot be null");
        } catch(NullPointerException ex) {}
        Site site = network.getSite("", "");
        assertNotNull(site);
    }
    
    @Test
    public void testSiteCollectionToken() {
        Site site = Livefyre.getNetwork("", "").getSite(SITE_ID, SITE_KEY);
        String token = site.buildCollectionToken("test", "testId", "testUrl", "testTags");
        assertNotNull(token);
        try {
            assertEquals(LivefyreJwtUtil.decodeJwt(SITE_KEY, token).getParamAsPrimitive("url").getAsString(), "testUrl");
        } catch (InvalidKeyException e) {
            fail("shouldn't fail");
        }
        
        token = site.buildCollectionToken("test", "testId", "testUrl", "testTags", StreamType.REVIEWS);
        assertNotNull(token);
        try {
            assertEquals(LivefyreJwtUtil.decodeJwt(SITE_KEY, token).getParamAsPrimitive("type").getAsString(), "reviews");
        } catch (InvalidKeyException e) {
            fail("shouldn't fail");
        }
    }
    
    @Test
    public void testNullChecks() {
        Site site = new Site();
        try {
            site.buildCollectionToken(null, null, null, null, null);
            fail("title cannot be null");
        } catch(NullPointerException ex) {}
        try {
            site.buildCollectionToken("", null, null, null, null);
            fail("articleId cannot be null");
        } catch(NullPointerException ex) {}
        try {
            site.buildCollectionToken("", "", null, null, null);
            fail("url cannot be null");
        } catch(NullPointerException ex) {}
        try {
            site.buildCollectionToken("", "", "", null, null);
            fail("tags cannot be null");
        } catch(NullPointerException ex) {}
        try {
            site.buildCollectionToken("", "", "", "", null);
            fail("stream cannot be null");
        } catch(NullPointerException ex) {}
        try {
            site.buildCollectionToken("", "", "", "", StreamType.NONE);
            fail("siteKey cannot be null");
        } catch(NullPointerException ex) {}
        try {
            site.getCollectionContent(null);
            fail("displayName cannot be null");
        } catch(NullPointerException ex) {}
        try {
            site.getCollectionContent("");
            fail("siteId cannot be null");
        } catch(NullPointerException ex) {}
        site.setSiteId("");
        try {
            site.getCollectionContent("");
            fail("networkName cannot be null");
        } catch(NullPointerException ex) {}
    }
}
