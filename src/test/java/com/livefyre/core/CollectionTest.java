package com.livefyre.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.InvalidKeyException;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.livefyre.Livefyre;
import com.livefyre.config.IntegrationTest;
import com.livefyre.config.LfTest;
import com.livefyre.config.UnitTest;
import com.livefyre.entity.Topic;
import com.livefyre.exceptions.LivefyreException;
import com.livefyre.utils.LivefyreJwtUtil;

public class CollectionTest extends LfTest {
    private static final String CHECKSUM = "3631759a11c4e0671d9ab5c1c90153c9";
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
        String otherId = collection.getCollectionContent().getAsJsonObject("collectionSettings").get("collectionId").getAsString();
        assertEquals(otherId, collection.getCollectionId());

        Collection coll1 = collection.setOptions(ImmutableMap.<String, Object>of("tags", "super")).createOrUpdate();
        assertEquals("super", coll1.getOptions().get("tags"));
    }
    
    @Test
    @Category(UnitTest.class)
    public void testCreateCollectionToken() {
        String token = site.buildCollection("title", "testId", "http://www.livefyre.com",
                ImmutableMap.<String,Object>of("tags", "tags", "type", "reviews")).buildCollectionMetaToken();
        assertNotNull(token);
        
        JsonObject decodedToken = null;
        try {
            decodedToken = LivefyreJwtUtil.decodeLivefyreJwt(SITE_KEY, token);
        } catch (InvalidKeyException e) {
            fail("shouldn't fail");
        }
        
        assertEquals(decodedToken.get("url").getAsString(), "http://www.livefyre.com");
        assertEquals(decodedToken.get("type").getAsString(), "reviews");
        
        token = site.buildCollection(TITLE, ARTICLE_ID, URL, ImmutableMap.<String,Object>of("type", "liveblog")).buildCollectionMetaToken();
        assertNotNull(token);
        try {
            decodedToken = LivefyreJwtUtil.decodeLivefyreJwt(SITE_KEY, token);
        } catch (InvalidKeyException e) {
            fail("shouldn't fail");
        }
        
        assertEquals(decodedToken.get("type").getAsString(), "liveblog");
        
        // test network topics
        List<Topic> topics = Lists.newArrayList(Topic.create(site.getNetwork(), "1", "1"));
        Collection coll = site.buildCollection(TITLE, ARTICLE_ID, URL, ImmutableMap.<String, Object>of("topics", topics));
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
        assertEquals(decodedToken.get("iss").getAsString(), site.getNetwork().getUrn());
    }
    
    @Test
    @Category(UnitTest.class)
    public void testCollectionChecksum() {
        Collection collection = site.buildCollection("title", "articleId", "http://livefyre.com", ImmutableMap.<String, Object>of("tags", "tags"));
        String checksum = collection.buildChecksum();
        assertNotNull(checksum);
        assertEquals(CHECKSUM, checksum);
    }
    
    @Test
    @Category(UnitTest.class) 
    public void testGetCollectionId_fail() {
        Collection collection = site.buildCollection(TITLE, ARTICLE_ID, URL, ImmutableMap.<String, Object>of("tags", "tags"));
        try {
            collection.getCollectionId();
            fail();
        } catch (LivefyreException e) {
            assertEquals("Call createOrUpdate() to set the collection id.", e.getMessage());
        }
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
