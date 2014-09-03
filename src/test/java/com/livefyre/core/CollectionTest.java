package com.livefyre.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.InvalidKeyException;
import java.util.Calendar;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.google.common.collect.ImmutableMap;
import com.livefyre.Livefyre;
import com.livefyre.config.IntegrationTest;
import com.livefyre.config.LfTest;
import com.livefyre.config.UnitTest;
import com.livefyre.utils.LivefyreJwtUtil;

public class CollectionTest extends LfTest {
    private static final String CHECKSUM = "f99927e6dfd7203f51a3d290d9947290";

    @Test
    @Category(IntegrationTest.class)
    public void testCreateUpdateCollection() {
        Site site = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY).getSite(SITE_ID, SITE_KEY);
        String name = "JavaCreateCollection" + Calendar.getInstance().getTime();

        Collection collection = site.buildCollection(name, name, "http://answers.livefyre.com/JAVA", null).createOrUpdate();
        String otherId = site.getCollectionId(name);
        assertEquals(otherId, collection.getCollectionId());

        Collection coll1 = collection.setOptions(ImmutableMap.<String, Object>of("tags", "super")).createOrUpdate();
        assertEquals("super", coll1.getOptions().get("tags"));
    }
    
    @Test
    @Category(UnitTest.class)
    public void testSiteCollectionToken() {
        Site site = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY).getSite(SITE_ID, SITE_KEY);
        
        try {
            site.buildCollection("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456", "", "", null);
            fail("titles longer than 255 char are not allowed");
        } catch (IllegalArgumentException e) {}
        try {
            site.buildCollection("", "", "tet.com", null);
            fail("url must start with valid url scheme (http:// or https://)");
        } catch (IllegalArgumentException e) {}
        try {
            site.buildCollection("", "", "tet.com/", null);
            fail("url must be a valid domain");
        } catch (IllegalArgumentException e) {}
        try {
            site.buildCollection("", "", "http://www.test.com", ImmutableMap.<String,Object>of("type", "abc"));
            fail("type must be of a known type");
        } catch (IllegalArgumentException e) {}
        
        site.buildCollection("title", "testId", "http://www.livefyre.com", null); // checks the null map case

        String token = site.buildCollection("title", "testId", "http://www.livefyre.com",
                ImmutableMap.<String,Object>of("tags", "tags", "type", "reviews")).buildCollectionMetaToken();
        assertNotNull(token);
        
        JSONObject decodedToken = null;
        try {
            decodedToken = LivefyreJwtUtil.decodeLivefyreJwt(SITE_KEY, token);
        } catch (InvalidKeyException e) {
            fail("shouldn't fail");
        }
        
        assertEquals(decodedToken.getString("url"), "http://www.livefyre.com");
        assertEquals(decodedToken.getString("type"), "reviews");
        
        token = site.buildCollection("title", "testId", "http://www.livefyre.com", ImmutableMap.<String,Object>of("type", "liveblog")).buildCollectionMetaToken();
        assertNotNull(token);
        try {
            decodedToken = LivefyreJwtUtil.decodeLivefyreJwt(SITE_KEY, token);
        } catch (InvalidKeyException e) {
            fail("shouldn't fail");
        }
        
        assertEquals(decodedToken.getString("type"), "liveblog");
    }
    
    @Test
    @Category(UnitTest.class)
    public void testSiteChecksum() {
        Site site = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY).getSite(SITE_ID, SITE_KEY);
        Collection collection = site.buildCollection("articleId", "title", "https://www.url.com", ImmutableMap.<String, Object>of("tags", "tags"));
        String checksum = collection.buildChecksum();
        assertNotNull(checksum);
        assertEquals(CHECKSUM, checksum);
    }
    
    @Test
    @Category(UnitTest.class)
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
    @Category(UnitTest.class)
    public void testNullChecks() {
        Site site = new Site(new Network(NETWORK_NAME, NETWORK_KEY), SITE_ID, SITE_KEY);
        try {
            site.buildCollection(null, null, null, null);
            fail("title cannot be null");
        } catch(NullPointerException e) {}
        try {
            site.buildCollection("", null, null, null);
            fail("articleId cannot be null");
        } catch(NullPointerException e) {}
        try {
            site.buildCollection("", "", null, null);
            fail("url cannot be null");
        } catch(NullPointerException e) {}
        try {
            site.getCollectionContent(null);
            fail("displayName cannot be null");
        } catch(NullPointerException e) {}
    }
}
