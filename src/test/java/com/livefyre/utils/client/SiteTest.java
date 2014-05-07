package com.livefyre.utils.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.InvalidKeyException;

import net.oauth.jsontoken.JsonToken;

import org.junit.Test;

import com.livefyre.utils.core.LivefyreJwtUtil;

public class SiteTest {
    private static final String CHECKSUM = "6e2e4faf7b95f896260fe695eafb34ba";
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
            site.buildCollectionMetaToken("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456", "", "", "", null);
            fail("titles longer than 255 char are not allowed");
        } catch (IllegalArgumentException e) {}
        try {
            site.buildCollectionMetaToken("", "", "tet.com", "" , "");
            fail("url must start with valid url scheme (http:// or https://)");
        } catch (IllegalArgumentException e) {}
        try {
            site.buildCollectionMetaToken("", "", "tet.com/", "", "");
            fail("url must be a valid domain");
        } catch (IllegalArgumentException e) {}
        try {
            site.buildCollectionMetaToken("", "", "http://www.test.com", "", "abc");
            fail("type must be of a known type");
        } catch (IllegalArgumentException e) {}
        
        String token = site.buildCollectionMetaToken("title", "testId", "http://www.livefyre.com", "tags", "reviews");
        assertNotNull(token);
        JsonToken decodedToken = null;
        try {
            decodedToken = LivefyreJwtUtil.decodeJwt(SITE_KEY, token);
        } catch (InvalidKeyException e) {
            fail("shouldn't fail");
        }
        
        assertEquals(decodedToken.getParamAsPrimitive("url").getAsString(), "http://www.livefyre.com");
        assertEquals(decodedToken.getParamAsPrimitive("type").getAsString(), "reviews");
        
        token = site.buildCollectionMetaToken("title", "testId", "http://www.livefyre.com", "tags", "liveblog");
        assertNotNull(token);
        try {
            decodedToken = LivefyreJwtUtil.decodeJwt(SITE_KEY, token);
        } catch (InvalidKeyException e) {
            fail("shouldn't fail");
        }
        
        assertEquals(decodedToken.getParamAsPrimitive("type").getAsString(), "liveblog");
    }
    
    @Test
    public void testSiteChecksum() {
        Site site = Livefyre.getNetwork("", "").getSite(SITE_ID, SITE_KEY);
        
        try {
            site.buildChecksum("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456", "", "");
            fail("titles longer than 255 char are not allowed");
        } catch (IllegalArgumentException e) {}
        try {
            site.buildChecksum("", "tet.com", "");
            fail("url must start with valid url scheme (http:// or https://)");
        } catch (IllegalArgumentException e) {}
        
        String checksum = site.buildChecksum("title", "https://www.url.com", "tags");
        assertNotNull(checksum);
        assertEquals(CHECKSUM, checksum);
    }
    
    @Test
    public void testSiteUrlChecker() {
        Site site = Livefyre.getNetwork("", "").getSite(SITE_ID, SITE_KEY);
        
        assertFalse(site.isValidFullUrl("test.com"));
        
        assertTrue(site.isValidFullUrl("http://localhost:8000"));
        assertTrue(site.isValidFullUrl("http://清华大学.cn"));
        assertTrue(site.isValidFullUrl("http://www.mysite.com/myresumé.html"));
        assertTrue(site.isValidFullUrl("https://test.com/"));
        assertTrue(site.isValidFullUrl("http://test.com/"));
        assertTrue(site.isValidFullUrl("https://test.com/path/test.-_~!$&'()*+,;=:@/dash"));
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
            site.buildCollectionMetaToken("", "", "https://test.com", "", null);
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
