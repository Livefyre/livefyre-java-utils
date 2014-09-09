package com.livefyre.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.InvalidKeyException;
import java.util.Calendar;
import java.util.List;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.livefyre.Livefyre;
import com.livefyre.config.IntegrationTest;
import com.livefyre.config.LfTest;
import com.livefyre.config.UnitTest;
import com.livefyre.entity.Topic;
import com.livefyre.utils.LivefyreJwtUtil;

public class CollectionTest extends LfTest {
    private static final String CHECKSUM = "f99927e6dfd7203f51a3d290d9947290";
    private Site site;
    
    @Before
    public void setup() {
        site = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY).getSite(SITE_ID, SITE_KEY);
    }

    @Test
    @Category(IntegrationTest.class)
    public void testCreateUpdateCollection() {
        String name = "JavaCreateCollection" + Calendar.getInstance().getTimeInMillis();

        Collection collection = site.buildCollection(name, name, URL, null).createOrUpdate();
        String otherId = collection.getCollectionContent().getJSONObject("collectionSettings").getString("collectionId");
        assertEquals(otherId, collection.getCollectionId());

        Collection coll1 = collection.setOptions(ImmutableMap.<String, Object>of("tags", "super")).createOrUpdate();
        assertEquals("super", coll1.getOptions().get("tags"));
    }
    
    @Test
    @Category(UnitTest.class)
    public void testCreateCollectionToken() {
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
        
        // test network topics
        List<Topic> topics = Lists.newArrayList(Topic.create(site.getNetwork(), "1", "1"));
        Collection coll = site.buildCollection("id", "title", "http://www.livefyre.com", ImmutableMap.<String, Object>of("topics", topics));
        assertTrue(coll.isNetworkIssued());
        
        token = coll.buildCollectionMetaToken();
        try {
            LivefyreJwtUtil.decodeLivefyreJwt(site.getKey(), token);
            fail("Should be encoded with network key.");
        } catch (InvalidKeyException e) {}
        
        try {
            decodedToken = LivefyreJwtUtil.decodeLivefyreJwt(site.getNetwork().getKey(), token);
        } catch (InvalidKeyException e) {
            fail("Should pass when decoded with network key.");
        }
        assertEquals(decodedToken.getString("iss"), site.getNetwork().getUrn());
    }
    
    @Test
    @Category(UnitTest.class)
    public void testCollectionChecksum() {
        Collection collection = site.buildCollection("articleId", "title", "https://www.url.com", ImmutableMap.<String, Object>of("tags", "tags"));
        String checksum = collection.buildChecksum();
        assertNotNull(checksum);
        assertEquals(CHECKSUM, checksum);
    }
    
    @Test
    @Category(UnitTest.class)
    public void testCollectionUrlChecker() {
        Collection collection = site.buildCollection("", "", "http://filler.com", null);
        
        assertFalse(collection.isValidFullUrl("test.com"));
        assertTrue(collection.isValidFullUrl("http://localhost:8000"));
        assertTrue(collection.isValidFullUrl("http://清华大学.cn"));
        assertTrue(collection.isValidFullUrl("http://www.mysite.com/myresumé.html"));
        assertTrue(collection.isValidFullUrl("https://test.com/"));
        assertTrue(collection.isValidFullUrl("http://test.com/"));
        assertTrue(collection.isValidFullUrl("https://test.com/path/test.-_~!$&'()*+,;=:@/dash"));
    }
    
    @Test
    @Category(UnitTest.class)
    public void testInstantiationNullChecks() {
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
    }
}
