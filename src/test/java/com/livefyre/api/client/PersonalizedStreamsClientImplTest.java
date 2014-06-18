package com.livefyre.api.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.livefyre.Livefyre;
import com.livefyre.api.dto.Subscription;
import com.livefyre.api.dto.Subscription.Type;
import com.livefyre.api.dto.Topic;
import com.livefyre.api.dto.TopicDataDto;
import com.livefyre.core.Network;
import com.livefyre.core.Site;

public class PersonalizedStreamsClientImplTest {
    private static final String NETWORK_NAME = "<NETWORK-NAME>";
    private static final String NETWORK_KEY = "<NETWORK-KEY>";
    private static final String SITE_ID = "<SITE-ID>";
    private static final String SITE_KEY = "<SITE-KEY>";
    private static final String COLLECTION_ID = "<COLLECTION-ID>";
    
    private Network network;
    private Site site;
    
    private Topic topic;
    private Topic topic1;
    private Topic topic2;
    private Topic topic3;
    private Topic topic4;
    private Topic topic5;
    private Topic topic6;
    
    private List<Topic> topics;
    private List<Topic> topics1;
    private List<Topic> topics2;
    
    @Before
    public void setup() {
        this.network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
        this.site = network.getSite(SITE_ID, SITE_KEY);
        
        this.topic = new Topic(network, "TOPIC", "LABEL");
        this.topic1 = new Topic(network, "1", "UNO");
        this.topic2 = new Topic(network, "2", "DOS");
        this.topic3 = new Topic(network, "3", "TRES");

        this.topic4 = new Topic(site, "4", "NET");
        this.topic5 = new Topic(site, "5", "DASUT");
        this.topic6 = new Topic(site, "6", "YUSUT");
        
        
        this.topics = Lists.newArrayList(topic, topic1, topic2, topic3, topic4, topic5, topic6);
        this.topics1 = Lists.newArrayList(topic1, topic2, topic3);
        this.topics2 = Lists.newArrayList(topic4, topic5, topic6);
    }
    
    @Test
    public void testNetworkTopicApi() {
        assertTrue(PersonalizedStreamsClientImpl.postTopic(network, topic));
        Topic t = PersonalizedStreamsClientImpl.getTopic(network, topic.getId());
        assertNotNull(t);
        
        assertTrue(PersonalizedStreamsClientImpl.deleteTopic(network, t));
        
//      PersonalizedStreamsClientImpl.getTopic(site, t.getId());
        List<Topic> ts = PersonalizedStreamsClientImpl.getTopics(network, null, null);
        assertTrue(ts.isEmpty());
    }
    
    @Test
    public void testMultipleNetworkTopicApi() {
        TopicDataDto dto = PersonalizedStreamsClientImpl.postTopics(network, topics);
        assertTrue(dto.getCreated() == topics.size());
        
        List<Topic> ts = PersonalizedStreamsClientImpl.getTopics(network, 3, 0);
        assertTrue(ts.size() == 3);
        
        dto = PersonalizedStreamsClientImpl.deleteTopics(network, topics);
        assertTrue(dto.getDeleted() == topics.size());
        
        ts = PersonalizedStreamsClientImpl.getTopics(network, null, null);
        assertTrue(ts.isEmpty());
    }
    
    @Test
    public void testSiteTopicApi() {
        assertTrue(PersonalizedStreamsClientImpl.postTopic(site, topic4));
        
        Topic t = PersonalizedStreamsClientImpl.getTopic(site, topic4.getId());
        assertNotNull(t);
        
        assertTrue(PersonalizedStreamsClientImpl.deleteTopic(site, t));
        
//        PersonalizedStreamsClientImpl.getTopic(site, t.getId());
        List<Topic> ts = PersonalizedStreamsClientImpl.getTopics(network, null, null);
        assertTrue(ts.isEmpty());
    }
    
    @Test
    public void testMultipleSiteTopicApi() {
        TopicDataDto dto = PersonalizedStreamsClientImpl.postTopics(site, topics2);
        assertTrue(dto.getCreated() == topics2.size());
        
        List<Topic> ts = PersonalizedStreamsClientImpl.getTopics(site, 2, 0);
        assertTrue(ts.size() == 2);
        
        dto = PersonalizedStreamsClientImpl.deleteTopics(site, topics2);
        assertTrue(dto.getDeleted() == topics2.size());
        
        ts = PersonalizedStreamsClientImpl.getTopics(site, null, null);
        assertTrue(ts.isEmpty());
    }
    
    @Test
    public void testCollectionTopicApi_network() {
        PersonalizedStreamsClientImpl.postTopics(network, topics);
        
        List<String> topicIds = PersonalizedStreamsClientImpl.getCollectionTopics(site, COLLECTION_ID);
        assertTrue(topicIds.isEmpty());
        
        int added = PersonalizedStreamsClientImpl.postCollectionTopics(site, COLLECTION_ID, topics);
        assertTrue(added == 7);
        
        topicIds = PersonalizedStreamsClientImpl.getCollectionTopics(site, COLLECTION_ID);
        assertTrue(topicIds.size() == 7);

        assertTrue(PersonalizedStreamsClientImpl.putCollectionTopics(site, COLLECTION_ID, topics1));
        
        topicIds = PersonalizedStreamsClientImpl.getCollectionTopics(site, COLLECTION_ID);
        assertTrue(topicIds.size() == 3);
        
        int deleted = PersonalizedStreamsClientImpl.deleteCollectionTopics(site, COLLECTION_ID, topics);
        assertTrue(deleted == 3);
        
        topicIds = PersonalizedStreamsClientImpl.getCollectionTopics(site, COLLECTION_ID);
        assertTrue(topicIds.isEmpty());
        
        PersonalizedStreamsClientImpl.deleteTopics(network, topics);
        topicIds = PersonalizedStreamsClientImpl.getCollectionTopics(site, COLLECTION_ID);
    }

    @Test
    public void testCollectionTopicApi_site() {
        PersonalizedStreamsClientImpl.postTopics(site, topics2);
        
        PersonalizedStreamsClientImpl.getCollectionTopics(site, COLLECTION_ID);
        PersonalizedStreamsClientImpl.postCollectionTopics(site, COLLECTION_ID, topics2);
        PersonalizedStreamsClientImpl.getCollectionTopics(site, COLLECTION_ID);
//        
//        PersonalizedStreamsClientImpl.putCollectionTopics(site, COLLECTION_ID, topics1); // this would clear all topics for the given collection to the ones provided...
//        PersonalizedStreamsClientImpl.getCollectionTopics(site, COLLECTION_ID);
//
        PersonalizedStreamsClientImpl.deleteCollectionTopics(site, COLLECTION_ID, topics2);
        PersonalizedStreamsClientImpl.deleteTopics(site, topics2);
    }
    
    // TODO make a super mix up test
    
    @Test
    public void testSubscriberApi() {
        String user1 = network.getUserUrn("539f362185889e79f5000000"); //TODO get a different user.
        
        Subscription subscription = new Subscription(topic1.getId(), user1, Type.personalStream);

        PersonalizedStreamsClientImpl.postTopics(network, topics);
        PersonalizedStreamsClientImpl.deleteTopics(network, topics);
        List<Subscription> su = PersonalizedStreamsClientImpl.getSubscriptions(network, user1);
        int subs = PersonalizedStreamsClientImpl.postSubscriptions(network, user1, topics);
//        boolean s = PersonalizedStreamsClientImpl.putSubscriptions(network, user1, topics1);
        int del = PersonalizedStreamsClientImpl.deleteSubscriptions(network, user1, Lists.newArrayList(topics));
        List<Subscription> sub = PersonalizedStreamsClientImpl.getSubscriptions(network, user1);
        sub.size();

        PersonalizedStreamsClientImpl.deleteTopics(network, topics);
    }
}
