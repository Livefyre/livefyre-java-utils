package com.livefyre.api.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.livefyre.Livefyre;
import com.livefyre.config.LfTest;
import com.livefyre.core.Network;
import com.livefyre.core.Site;
import com.livefyre.entity.Subscription;
import com.livefyre.entity.Topic;

@Ignore
public class PersonalizedStreamsClientTest extends LfTest {
    private Network network;
    private Site site;
    
    private String userToken;
    private Map<String, String> topicMap;
    
    @Before
    public void setup() {
        network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
        site = network.getSite(SITE_ID, SITE_KEY);
        
        this.userToken = network.buildUserAuthToken(USER_ID, USER_ID + "@" + NETWORK_NAME, Network.DEFAULT_EXPIRES);
        this.topicMap = ImmutableMap.of("1", "UNO", "2", "DOS", "3", "TRES");
    }
    
    @Test
    public void testNetworkTopicApi() {
        Topic topic = (PersonalizedStreamsClient.createOrUpdateTopic(network, "1", "UNO"));
        Topic t = PersonalizedStreamsClient.getTopic(network, topic.truncatedId());
        assertNotNull(t);
        
        assertTrue(PersonalizedStreamsClient.deleteTopic(network, t));
        
        List<Topic> ts = PersonalizedStreamsClient.getTopics(network, null, null);
        assertTrue(ts.isEmpty());
    }
    
    @Test
    public void testNetworkMultipleTopicApi() {
        List<Topic> topics = PersonalizedStreamsClient.createOrUpdateTopics(network, topicMap);
        assertEquals(topics.size(), 3);
        
        List<Topic> ts = PersonalizedStreamsClient.getTopics(network, 1, 0);
        assertEquals(ts.size(), 1);
        
        int deleted = PersonalizedStreamsClient.deleteTopics(network, topics);
        assertEquals(deleted, topics.size());
        
        ts = PersonalizedStreamsClient.getTopics(network, null, null);
        assertTrue(ts.isEmpty());
    }
    
    @Test
    public void testSiteTopicApi() {
        Topic topic = PersonalizedStreamsClient.createOrUpdateTopic(site, "2", "DOS");
        
        Topic t = PersonalizedStreamsClient.getTopic(site, topic.truncatedId());
        assertNotNull(t);
        assertEquals(t.getLabel(), topic.getLabel());
        
        assertTrue(PersonalizedStreamsClient.deleteTopic(site, t));
        
        List<Topic> ts = PersonalizedStreamsClient.getTopics(network, null, null);
        assertTrue(ts.isEmpty());
    }
    
    @Test
    public void testSiteMultipleTopicApi() {
        List<Topic> topics = PersonalizedStreamsClient.createOrUpdateTopics(site, topicMap);
        assertEquals(topics.size(), 3);
        
        List<Topic> ts = PersonalizedStreamsClient.getTopics(site, 2, 0);
        assertEquals(ts.size(), 2);
        
        int deleted = PersonalizedStreamsClient.deleteTopics(site, topics);
        assertEquals(deleted, topics.size());
        
        ts = PersonalizedStreamsClient.getTopics(site, null, null);
        assertTrue(ts.isEmpty());
    }
    
    @Test
    public void testCollectionTopicApi_network() {
        List<Topic> topics = PersonalizedStreamsClient.createOrUpdateTopics(network, topicMap);
        
        List<String> topicIds = PersonalizedStreamsClient.getCollectionTopics(site, COLLECTION_ID);
        assertTrue(topicIds.isEmpty());
        
        int added = PersonalizedStreamsClient.addCollectionTopics(site, COLLECTION_ID, topics);
        assertTrue(added == 3);
        
        topicIds = PersonalizedStreamsClient.getCollectionTopics(site, COLLECTION_ID);
        assertTrue(topicIds.size() == 3);

        Map<String, Integer> results = PersonalizedStreamsClient.replaceCollectionTopics(site, COLLECTION_ID, Lists.newArrayList(topics.get(0)));
        assertTrue(results.get("added") > 0 || results.get("removed") > 0);
        
        topicIds = PersonalizedStreamsClient.getCollectionTopics(site, COLLECTION_ID);
        assertTrue(topicIds.size() == 1);
        
        int deleted = PersonalizedStreamsClient.removeCollectionTopics(site, COLLECTION_ID, topics);
        assertTrue(deleted == 1);
        
        topicIds = PersonalizedStreamsClient.getCollectionTopics(site, COLLECTION_ID);
        assertTrue(topicIds.isEmpty());
        
        PersonalizedStreamsClient.deleteTopics(network, topics);
    }

    @Test
    public void testCollectionTopicApi_site() {
        List<Topic> topics = PersonalizedStreamsClient.createOrUpdateTopics(site, topicMap);
        
        int added = PersonalizedStreamsClient.addCollectionTopics(site, COLLECTION_ID, topics);
        assertTrue(added == topics.size());
        
        Map<String, Integer> results = PersonalizedStreamsClient.replaceCollectionTopics(site, COLLECTION_ID, Lists.newArrayList(topics.get(0)));
        assertTrue(results.get("added") > 0 || results.get("removed") > 0);
        
        List<String> topicIds = PersonalizedStreamsClient.getCollectionTopics(site, COLLECTION_ID);
        assertTrue(topicIds.size() == 1);

        int deleted = PersonalizedStreamsClient.removeCollectionTopics(site, COLLECTION_ID, topics);
        assertTrue(deleted == 1);
        
        topicIds = PersonalizedStreamsClient.getCollectionTopics(site, COLLECTION_ID);
        assertTrue(topicIds.isEmpty());
        
        PersonalizedStreamsClient.deleteTopics(site, topics);
    }
    
    @Test
    public void testSubscriberApi() {
        List<Topic> topics = PersonalizedStreamsClient.createOrUpdateTopics(network, topicMap);
        
        List<Subscription> su = PersonalizedStreamsClient.getSubscriptions(network, USER_ID);
        assertTrue(su.isEmpty());
        
        int subs = PersonalizedStreamsClient.addSubscriptions(network, userToken, topics);
        assertTrue(subs == 3);

        Map<String, Integer> results = PersonalizedStreamsClient.replaceSubscriptions(network, userToken, Lists.newArrayList(topics.get(0), topics.get(1)));
        assertTrue(results.get("added") > 0 || results.get("removed") > 0);
        
        su = PersonalizedStreamsClient.getSubscribers(network, topics.get(0), 100, 0);

        int del = PersonalizedStreamsClient.removeSubscriptions(network, userToken, topics);
        assertTrue(del == 2);
        
        List<Subscription> sub = PersonalizedStreamsClient.getSubscriptions(network, USER_ID);
        assertTrue(sub.isEmpty());

        PersonalizedStreamsClient.deleteTopics(network, topics);
    }
    
    @Test
    public void testTimelineStream() {
        Topic topic = PersonalizedStreamsClient.createOrUpdateTopic(network, "TOPIC", "LABEL");
        JSONObject test = PersonalizedStreamsClient.getTimelineStream(network, topic.getId() +":topicStream", 50, null, null);
        assertNotNull(test);
        PersonalizedStreamsClient.deleteTopic(network, topic);
    }
}
