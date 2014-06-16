package com.livefyre.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.InvalidKeyException;

import net.oauth.jsontoken.JsonToken;

import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.livefyre.Livefyre;
import com.livefyre.utils.LivefyreJwtUtil;

public class SiteTest {
    private static final String CHECKSUM = "6e2e4faf7b95f896260fe695eafb34ba";
    private static final String NETWORK_NAME = "<NETWORK-NAME>";
    private static final String NETWORK_KEY = "<NETWORK-KEY>";
    private static final String SITE_ID = "<SITE-ID>";
    private static final String SITE_KEY = "<SITE-KEY>";
    private static final String ARTICLE_ID = "<ARTICLE-ID>";

    @Test
    @Ignore
    public void testGetCollectionInfo() {
        Network network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
        Site site = network.getSite(SITE_ID, SITE_KEY);
        String collectionContent = site.getCollectionContent(ARTICLE_ID);
        
        assertNotNull(collectionContent);
        
        JsonObject collectionJson = site.getCollectionContentJson(ARTICLE_ID);
        
        assertNotNull(collectionJson);
    }
    
    @Test
    @Ignore
    public void testGetCollectionId() {
        Network network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
        Site site = network.getSite(SITE_ID, SITE_KEY);
        String id = site.getCollectionId(ARTICLE_ID);
        
        assertNotNull(id);
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
    
    @Test
    public void testSiteCollectionToken() {
        Site site = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY).getSite(SITE_ID, SITE_KEY);
        
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
        Site site = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY).getSite(SITE_ID, SITE_KEY);
        
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
        Site site = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY).getSite(SITE_ID, SITE_KEY);
        
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
        Site site = new Site(new Network(NETWORK_NAME, NETWORK_KEY), SITE_ID, SITE_KEY);
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
            site.getCollectionContent(null);
            fail("displayName cannot be null");
        } catch(NullPointerException e) {}
    }
}
