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

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.livefyre.Livefyre;
import com.livefyre.config.IntegrationTest;
import com.livefyre.config.LfTest;
import com.livefyre.config.UnitTest;
import com.livefyre.dto.Topic;
import com.livefyre.exception.LivefyreException;
import com.livefyre.type.CollectionType;
import com.livefyre.utils.LivefyreJwtUtil;

public class CollectionTest extends LfTest {
    private static final String CHECKSUM = "8bcfca7fb2187b1dcb627506deceee32";
    private Site site;
    
    @Before
    public void setup() {
        site = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY).getSite(SITE_ID, SITE_KEY);
    }

    @Test
    @Category(IntegrationTest.class)
    public void testCreateUpdateCollection() {
        String name = "JavaCreateCollection" + Calendar.getInstance().getTimeInMillis();

        Collection collection = site.buildLiveCommentsCollection(name, name, URL).createOrUpdate();
        String otherId = collection.getCollectionContent().getAsJsonObject("collectionSettings").get("collectionId").getAsString();
        assertEquals(otherId, collection.getData().getCollectionId());

        collection.getData().setTags("super");
        Collection coll1 = collection.createOrUpdate();
        assertEquals("super", coll1.getData().getTags());
    }
    
    @Test
    @Category(UnitTest.class)
    public void testCreateCollectionToken() {
        Collection collection = site.buildLiveCommentsCollection("title", "testId", "http://www.livefyre.com");
        collection.getData().setTags("tags").setType(CollectionType.REVIEWS);
        
        String token = collection.buildCollectionMetaToken();
        assertNotNull(token);
        
        JsonObject decodedToken = null;
        try {
            decodedToken = LivefyreJwtUtil.decodeLivefyreJwt(SITE_KEY, token);
        } catch (InvalidKeyException e) {
            fail("shouldn't fail");
        }
        
        assertEquals(decodedToken.get("url").getAsString(), "http://www.livefyre.com");
        assertEquals(decodedToken.get("type").getAsString(), "reviews");
        
        collection = site.buildLiveCommentsCollection(TITLE, ARTICLE_ID, URL);
        collection.getData().setType(CollectionType.LIVEBLOG);
        token = collection.buildCollectionMetaToken();
        assertNotNull(token);
        try {
            decodedToken = LivefyreJwtUtil.decodeLivefyreJwt(SITE_KEY, token);
        } catch (InvalidKeyException e) {
            fail("shouldn't fail");
        }
        
        assertEquals(decodedToken.get("type").getAsString(), "liveblog");
        
        // test network topics
        List<Topic> topics = Lists.newArrayList(Topic.create(site.getNetwork(), "1", "1"));
        Collection coll = site.buildLiveCommentsCollection(TITLE, ARTICLE_ID, URL);
        coll.getData().setTopics(topics);
        assertTrue(coll.isNetworkIssued());
        
        token = coll.buildCollectionMetaToken();
        try {
            LivefyreJwtUtil.decodeLivefyreJwt(site.getData().getKey(), token);
            fail("Should be encoded with network key.");
        } catch (InvalidKeyException e) {}
        
        try {
            decodedToken = LivefyreJwtUtil.decodeLivefyreJwt(site.getNetwork().getData().getKey(), token);
        } catch (InvalidKeyException e) {
            fail("Should pass when decoded with network key.");
        }
        assertEquals(decodedToken.get("iss").getAsString(), site.getNetwork().getUrn());
    }
    
    @Test
    @Category(UnitTest.class)
    public void testCollectionChecksum() {
        Collection collection = site.buildLiveCommentsCollection("title", "articleId", "http://livefyre.com");
        collection.getData().setTags("tags");
        String checksum = collection.buildChecksum();
        assertNotNull(checksum);
        assertEquals(CHECKSUM, checksum);
    }
    
    @Test
    @Category(UnitTest.class) 
    public void testGetCollectionId_fail() {
        Collection collection = site.buildLiveCommentsCollection(TITLE, ARTICLE_ID, URL);
        try {
            collection.getData().getCollectionId();
            fail();
        } catch (LivefyreException e) {
            assertEquals("Call createOrUpdate() on the collection to set the collection id.", e.getMessage());
        }
    }
    
    @Test
    @Category(UnitTest.class)
    public void testInstantiationNullChecks() {
        try {
            site.buildLiveCommentsCollection(null, null, null);
            fail("title cannot be null");
        } catch(IllegalArgumentException e) {}
        try {
            site.buildLiveCommentsCollection("", null, null);
            fail("articleId cannot be null");
        } catch(IllegalArgumentException e) {}
        try {
            site.buildLiveCommentsCollection("", "", null);
            fail("url cannot be null");
        } catch(IllegalArgumentException e) {}
    }
}
