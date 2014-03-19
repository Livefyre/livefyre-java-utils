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
        } catch(NullPointerException e) {}
        try {
            network.getSite(null, "");
            fail("siteId cannot be null");
        } catch(NullPointerException e) {}
        Site site = network.getSite("", "");
        assertNotNull(site);
    }
    
    @Test
    public void testSiteCollectionToken() {
        Site site = Livefyre.getNetwork("", "").getSite(SITE_ID, SITE_KEY);
        
        try {
            site.buildCollectionMetaToken("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345", "", "", "");
            fail("titles longer than 255 char are not allowed");
        } catch (IllegalArgumentException e) {}
        try {
            site.buildCollectionMetaToken("", "", "tet.com", "");
            fail("url must start with valid url scheme (http:// or https://)");
        } catch (IllegalArgumentException e) {}
        try {
            site.buildCollectionMetaToken("", "", "tet.com/", "");
            fail("url must be a valid domain");
        } catch (IllegalArgumentException e) {}
        
        String token = site.buildCollectionMetaToken("test", "testId", "https://test.com", "testTags");
        assertNotNull(token);
        try {
            assertEquals(LivefyreJwtUtil.decodeJwt(SITE_KEY, token).getParamAsPrimitive("url").getAsString(), "https://test.com");
        } catch (InvalidKeyException e) {
            fail("shouldn't fail");
        }
        
        token = site.buildCollectionMetaToken("test", "testId", "http://www.test.com", "testTags", StreamType.REVIEWS);
        assertNotNull(token);
        try {
            assertEquals(LivefyreJwtUtil.decodeJwt(SITE_KEY, token).getParamAsPrimitive("type").getAsString(), "reviews");
        } catch (InvalidKeyException e) {
            fail("shouldn't fail");
        }
    }
    
    @Test
    public void testNullChecks() {
        Site site = new Site("", "", "");
        site.setNetworkName(null);
        site.setSiteId(null);
        site.setSiteKey(null);
        try {
            site.buildCollectionMetaToken(null, null, null, null, null);
            fail("title cannot be null");
        } catch(NullPointerException e) {}
        try {
            site.buildCollectionMetaToken("", null, null, null, null);
            fail("articleId cannot be null");
        } catch(NullPointerException e) {}
        try {
            site.buildCollectionMetaToken("", "", null, null, null);
            fail("url cannot be null");
        } catch(NullPointerException e) {}
        try {
            site.buildCollectionMetaToken("", "", "http://test.com", null, null);
            fail("tags cannot be null");
        } catch(NullPointerException e) {}
        try {
            site.buildCollectionMetaToken("", "", "http://test.com", "", null);
            fail("stream cannot be null");
        } catch(NullPointerException e) {}
        try {
            site.buildCollectionMetaToken("", "", "https://test.com", "", StreamType.NONE);
            fail("siteKey cannot be null");
        } catch(NullPointerException e) {}
        try {
            site.getCollectionContent(null);
            fail("displayName cannot be null");
        } catch(NullPointerException e) {}
        try {
            site.getCollectionContent("");
            fail("siteId cannot be null");
        } catch(NullPointerException e) {}
        site.setSiteId("");
        try {
            site.getCollectionContent("");
            fail("networkName cannot be null");
        } catch(NullPointerException e) {}
    }
}
