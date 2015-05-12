package com.livefyre.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.livefyre.Livefyre;
import com.livefyre.config.IntegrationTest;
import com.livefyre.config.PojoTest;
import com.livefyre.config.UnitTest;
import com.livefyre.dto.Topic;
import com.livefyre.exceptions.ApiException;
import com.livefyre.exceptions.LivefyreException;
import com.livefyre.exceptions.TokenException;
import com.livefyre.type.CollectionType;
import com.livefyre.utils.LivefyreUtil;

public class CollectionTest extends PojoTest<Collection> {
    private static final String CHECKSUM = "8bcfca7fb2187b1dcb627506deceee32";
    private Site site;
    
    @Before
    public void setup() {
        site = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY).getSite(SITE_ID, SITE_KEY);
    }
    
    @Test
    @Category(UnitTest.class)
    public void testBuildCollection() {
        Site site = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY).getSite(SITE_ID, SITE_KEY);
        try {
            Collection.init(site, CollectionType.COMMENTS, "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456", "", "");
            fail("titles longer than 255 char are not allowed");
        } catch (IllegalArgumentException e) {}
        try {
            Collection.init(site, CollectionType.COMMENTS, "", "", "");
            fail("url cannot be blank");
        } catch (IllegalArgumentException e) {}
        try {
            Collection.init(site, CollectionType.COMMENTS, "", "", "tet.com");
            fail("url must start with valid url scheme (http:// or https://)");
        } catch (IllegalArgumentException e) {}
        try {
            Collection.init(site, CollectionType.COMMENTS, "", "", "tet.com/");
            fail("url must be a valid domain");
        } catch (IllegalArgumentException e) {}

        Collection collection  = Collection.init(site, CollectionType.COMMENTS, TITLE, ARTICLE_ID, URL);
        assertNotNull(collection);
    }

    @Test
    @Category(IntegrationTest.class)
    public void testCreateUpdateCollection() {
        String name = "JavaCreateCollection" + Calendar.getInstance().getTimeInMillis();

        Collection collection = site.buildCommentsCollection(name, name, URL).createOrUpdate();
        String otherId = collection.getCollectionContent().getAsJsonObject("collectionSettings").get("collectionId").getAsString();
        assertEquals(otherId, collection.getData().getId());

        collection.getData().setTitle(name+"super");
        Collection coll1 = collection.createOrUpdate();
        /* works but takes some time on the server side to update... */
//        JsonObject obj = coll1.getCollectionContent();
//        assertEquals(name+"super", 
//                obj.getAsJsonObject("collectionSettings").getAsJsonPrimitive("title").getAsString());
        
        String id = collection.getData().getId();
        collection.getData().setId(null);
        collection.createOrUpdate();
        assertEquals(id, collection.getData().getId());
    }

    @Test
    @Category(IntegrationTest.class)
    public void testGetCollectionContent_fail() {
        String name = "JavaCreateCollection" + Calendar.getInstance().getTimeInMillis();
        Collection collection = site.buildCommentsCollection(name, name, URL);
        collection.getSite().getData().setId("0");
        try {
            collection.getCollectionContent();
            fail("this should not work");
        }
        catch(ApiException e) {}
    }
    
    @Test
    @Category(UnitTest.class)
    public void testCreateCollectionToken() {
        Collection collection = site.buildCommentsCollection("title", "testId", "http://www.livefyre.com");
        collection.getData().setTags("tags").setType(CollectionType.REVIEWS);
        
        String token = collection.buildCollectionMetaToken();
        assertNotNull(token);
        
        JsonObject decodedToken = null;
        decodedToken = LivefyreUtil.decodeJwt(token, SITE_KEY);
        
        assertEquals(decodedToken.get("url").getAsString(), "http://www.livefyre.com");
        assertEquals(decodedToken.get("type").getAsString(), "reviews");
        
        collection = site.buildCommentsCollection(TITLE, ARTICLE_ID, URL);
        collection.getData().setType(CollectionType.BLOG);
        token = collection.buildCollectionMetaToken();
        assertNotNull(token);
        decodedToken = LivefyreUtil.decodeJwt(token, SITE_KEY);
        
        assertEquals(decodedToken.get("type").getAsString(), "liveblog");
        
        // test network topics
        List<Topic> topics = Lists.newArrayList(Topic.create(site.getNetwork(), "1", "1"));
        Collection coll = site.buildCommentsCollection(TITLE, ARTICLE_ID, URL);
        coll.getData().setTopics(topics);
        assertTrue(coll.isNetworkIssued());
        
        token = coll.buildCollectionMetaToken();
        try {
            LivefyreUtil.decodeJwt(token, site.getData().getKey());
            fail("Should be encoded with network key.");
        } catch (TokenException e) {}
        
        decodedToken = LivefyreUtil.decodeJwt(token, site.getNetwork().getData().getKey());
        assertEquals(decodedToken.get("iss").getAsString(), site.getNetwork().getUrn());
    }
    
    @Test
    @Category(UnitTest.class)
    public void testCollectionChecksum() {
        Collection collection = site.buildCommentsCollection("title", "articleId", "http://livefyre.com");
        collection.getData().setTags("tags");
        String checksum = collection.buildChecksum();
        assertNotNull(checksum);
        assertEquals(CHECKSUM, checksum);
    }
    
    @Test
    @Category(UnitTest.class) 
    public void testGetCollectionId_fail() {
        Collection collection = site.buildCommentsCollection(TITLE, ARTICLE_ID, URL);
        try {
            collection.getData().getId();
            fail();
        } catch (LivefyreException e) {
            assertEquals("Call createOrUpdate() on the collection to set the id.", e.getMessage());
        }
    }
    
    @Test
    @Category(UnitTest.class)
    public void testInstantiationNullChecks() {
        try {
            site.buildCommentsCollection(null, null, null);
            fail("title cannot be null");
        } catch(IllegalArgumentException e) {}
        try {
            site.buildCommentsCollection("", null, null);
            fail("articleId cannot be null");
        } catch(IllegalArgumentException e) {}
        try {
            site.buildCommentsCollection("", "", null);
            fail("url cannot be null");
        } catch(IllegalArgumentException e) {}
        try {
            site.buildCollection(null, TITLE, ARTICLE_ID, URL);
            fail("type cannot be null");
        } catch(IllegalArgumentException e) {}
    }
    
    @Test
    @Category(UnitTest.class)
    public void testNetworkIssued() {
        Collection collection = site.buildCommentsCollection(TITLE, ARTICLE_ID, URL);
        assertFalse(collection.isNetworkIssued());
        
        List<Topic> topics = Lists.<Topic>newArrayList();
        collection.getData().setTopics(topics);
        assertFalse(collection.isNetworkIssued());
        
        topics.add(Topic.create(site, "1", "FAIL"));
        assertFalse(collection.isNetworkIssued());

        topics.add(Topic.create(site.getNetwork(), "2", "PASS"));
        assertTrue(collection.isNetworkIssued());
    }
    
    @Test
    @Category(UnitTest.class)
    public void testGetUrn() {
        Collection collection = site.buildCommentsCollection(TITLE, ARTICLE_ID, URL);
        collection.getData().setId("ID");
        assertEquals(site.getUrn()+":collection=ID", collection.getUrn());
    }
}
