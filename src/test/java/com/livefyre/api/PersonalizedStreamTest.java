package com.livefyre.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.livefyre.Livefyre;
import com.livefyre.config.IntegrationTest;
import com.livefyre.config.LfTest;
import com.livefyre.core.Collection;
import com.livefyre.core.Network;
import com.livefyre.core.Site;
import com.livefyre.entity.Subscription;
import com.livefyre.entity.Topic;

@Category(IntegrationTest.class)
public class PersonalizedStreamTest extends LfTest {
    private Network network;
    private Site site;
    private Map<String, String> topicMap;
    
    @Before
    public void setup() {
        network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
        site = network.getSite(SITE_ID, SITE_KEY);
        
        this.topicMap = ImmutableMap.of("1", "UNO", "2", "DOS", "3", "TRES");
    }
    
    @Test
    public void testNetworkTopicApi() {
        Topic topic = (PersonalizedStream.createOrUpdateTopic(network, "1", "UNO"));
        Topic t = PersonalizedStream.getTopic(network, "1");
        assertNotNull(t);
        assertEquals(t.getLabel(), topic.getLabel());
        
        assertTrue(PersonalizedStream.deleteTopic(network, t));
    }
    
    @Test
    public void testNetworkMultipleTopicApi() {
        List<Topic> topics = PersonalizedStream.createOrUpdateTopics(network, topicMap);
        assertEquals(topics.size(), 3);
        
        List<Topic> ts = PersonalizedStream.getTopics(network, 1, 0);
        assertEquals(ts.size(), 1);
        
        int deleted = PersonalizedStream.deleteTopics(network, topics);
        assertEquals(deleted, topics.size());
    }
    
    @Test
    public void testSiteTopicApi() {
        Topic topic = PersonalizedStream.createOrUpdateTopic(site, "2", "DOS");
        Topic t = PersonalizedStream.getTopic(site, topic.truncatedId());
        assertNotNull(t);
        assertEquals(t.getLabel(), topic.getLabel());
        
        assertTrue(PersonalizedStream.deleteTopic(site, t));
    }
    
    @Test
    public void testSiteMultipleTopicApi() {
        List<Topic> topics = PersonalizedStream.createOrUpdateTopics(site, topicMap);
        assertEquals(3, topics.size());
        
        List<Topic> ts = PersonalizedStream.getTopics(site, 2, 0);
        assertEquals(2, ts.size());
        
        int deleted = PersonalizedStream.deleteTopics(site, topics);
        PersonalizedStream.deleteTopics(site, topics);
        assertEquals(deleted, topics.size());
    }
    
    @Test
    public void testCollectionTopicApi_network() {
        List<Topic> topics = PersonalizedStream.createOrUpdateTopics(network, topicMap);
        String collectionName = "JAVA PSSTREAM TEST " + Calendar.getInstance().getTimeInMillis();
        Collection collection = site.buildCollection(collectionName, collectionName, URL).createOrUpdate();
        
        List<String> topicIds = PersonalizedStream.getCollectionTopics(collection);
        assertTrue(topicIds.isEmpty());
        
        int added = PersonalizedStream.addCollectionTopics(collection, topics);
        assertEquals(3, added);
        
        topicIds = PersonalizedStream.getCollectionTopics(collection);
        assertEquals(3, topicIds.size());

        Map<String, Integer> results = PersonalizedStream.replaceCollectionTopics(collection, Lists.newArrayList(topics.get(0)));
        assertTrue(results.get("added") == 0 && results.get("removed") == 2);
        
        topicIds = PersonalizedStream.getCollectionTopics(collection);
        assertEquals(1, topicIds.size());
        
        int deleted = PersonalizedStream.removeCollectionTopics(collection, topics);
        assertEquals(1, deleted);
        
        topicIds = PersonalizedStream.getCollectionTopics(collection);
        assertTrue(topicIds.isEmpty());

        collectionName = "JAVA PSSTREAM TEST " + Calendar.getInstance().getTimeInMillis();
        collection = site.buildCollection(collectionName, collectionName, URL);
        collection.getData().setTopics(topics);
        collection.createOrUpdate();

        PersonalizedStream.deleteTopics(network, topics);
    }

    @Test
    public void testCollectionTopicApi_site() {
        List<Topic> topics = PersonalizedStream.createOrUpdateTopics(site, topicMap);
        String collectionName = "JAVA PSSTREAM TEST " + Calendar.getInstance().getTimeInMillis();
        Collection collection = site.buildCollection(collectionName, collectionName, URL, null).createOrUpdate();
        
        int added = PersonalizedStream.addCollectionTopics(collection, topics);
        assertTrue(added == topics.size());
        
        Map<String, Integer> results = PersonalizedStream.replaceCollectionTopics(collection, Lists.newArrayList(topics.get(0)));
        assertTrue(results.get("added") == 0 && results.get("removed") == 2);
        
        List<String> topicIds = PersonalizedStream.getCollectionTopics(collection);
        assertEquals(1, topicIds.size());

        int deleted = PersonalizedStream.removeCollectionTopics(collection, topics);
        assertEquals(1, deleted);
        
        topicIds = PersonalizedStream.getCollectionTopics(collection);
        assertTrue(topicIds.isEmpty());
        
        collectionName = "JAVA PSSTREAM TEST " + Calendar.getInstance().getTimeInMillis();
        site.buildCollection(collectionName, collectionName, URL, ImmutableMap.<String, Object>of("topics", topics)).createOrUpdate();
        
        PersonalizedStream.deleteTopics(site, topics);
    }
    
    @Test
    public void testSubscriberApi() {
        String userToken = network.buildUserAuthToken(USER_ID, USER_ID + "@" + NETWORK_NAME, Network.DEFAULT_EXPIRES);
        List<Topic> topics = PersonalizedStream.createOrUpdateTopics(network, topicMap);
        
        List<Subscription> su = PersonalizedStream.getSubscriptions(network, USER_ID);
        assertTrue(su.isEmpty());
        
        int subs = PersonalizedStream.addSubscriptions(network, userToken, topics);
        assertEquals(3, subs);

        Map<String, Integer> results = PersonalizedStream.replaceSubscriptions(network, userToken, Lists.newArrayList(topics.get(0), topics.get(1)));
        assertTrue(results.get("added") == 0 && results.get("removed") == 1);
        
        su = PersonalizedStream.getSubscribers(network, topics.get(0), 100, 0);
        assertEquals(1, su.size());

        int del = PersonalizedStream.removeSubscriptions(network, userToken, topics);
        assertEquals(2, del);
        
        List<Subscription> sub = PersonalizedStream.getSubscriptions(network, USER_ID);
        assertTrue(sub.isEmpty());

        PersonalizedStream.deleteTopics(network, topics);
    }
    
    @Test
    public void testTimelineStream() {
        Topic topic = PersonalizedStream.createOrUpdateTopic(network, "TOPIC", "LABEL");
        JsonObject result = PersonalizedStream.getTimelineStream(network, topic.getId() +":topicStream", 50, null, null);
        assertNotNull(result);
        PersonalizedStream.deleteTopic(network, topic);
    }
}
