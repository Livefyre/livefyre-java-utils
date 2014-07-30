package com.livefyre.api.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.livefyre.Livefyre;
import com.livefyre.api.dto.TopicPostDto;
import com.livefyre.api.dto.TopicPutDto;
import com.livefyre.config.LfTest;
import com.livefyre.core.Network;
import com.livefyre.core.Site;
import com.livefyre.entity.Subscription;
import com.livefyre.entity.Topic;

@Ignore
public class PersonalizedStreamsClientTest extends LfTest {
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
        network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
        site = network.getSite(SITE_ID, SITE_KEY);
        
        this.topic = Topic.create(network, "TOPIC", "LABEL");
        this.topic1 = Topic.create(network, "1", "UNO");
        this.topic2 = Topic.create(network, "2", "DOS");
        this.topic3 = Topic.create(network, "3", "TRES");

        this.topic4 = Topic.create(site, "4", "NET");
        this.topic5 = Topic.create(site, "5", "DASUT");
        this.topic6 = Topic.create(site, "6", "YUSUT");
        
        
        this.topics = Lists.newArrayList(topic, topic1, topic2, topic3, topic4, topic5, topic6);
        this.topics1 = Lists.newArrayList(topic1, topic2, topic3);
        this.topics2 = Lists.newArrayList(topic4, topic5, topic6);
    }
    
    @Test
    public void testNetworkTopicApi() {
        assertNotNull(PersonalizedStreamsClient.postTopics(network, Lists.newArrayList(topic)));
        Topic t = PersonalizedStreamsClient.getTopic(network, topic.truncatedId());
        assertNotNull(t);
        
        assertTrue(PersonalizedStreamsClient.patchTopics(network, Lists.newArrayList(t)) == 1);
        
//      PersonalizedStreamsClientImpl.getTopic(site, t.getId());
        List<Topic> ts = PersonalizedStreamsClient.getTopics(network, null, null);
        assertTrue(ts.isEmpty());
    }
    
    @Test
    public void testNetworkMultipleTopicApi() {
        TopicPostDto dto = PersonalizedStreamsClient.postTopics(network, topics);
        assertTrue(dto.getCreated() == topics.size());
        
        List<Topic> ts = PersonalizedStreamsClient.getTopics(network, 3, 0);
        assertTrue(ts.size() == 3);
        
        int deleted = PersonalizedStreamsClient.patchTopics(network, topics);
        assertTrue(deleted == topics.size());
        
        ts = PersonalizedStreamsClient.getTopics(network, null, null);
        assertTrue(ts.isEmpty());
    }
    
    @Test
    public void testSiteTopicApi() {
        assertNotNull(PersonalizedStreamsClient.postTopics(site, Lists.newArrayList(topic4)));
        
        Topic t = PersonalizedStreamsClient.getTopic(site, topic4.truncatedId());
        assertNotNull(t);
        
        assertTrue(PersonalizedStreamsClient.patchTopics(site, Lists.newArrayList(t)) == 1);
        
//        PersonalizedStreamsClientImpl.getTopic(site, t.getId());
        List<Topic> ts = PersonalizedStreamsClient.getTopics(network, null, null);
        assertTrue(ts.isEmpty());
    }
    
    @Test
    public void testSiteMultipleTopicApi() {
        TopicPostDto dto = PersonalizedStreamsClient.postTopics(site, topics2);
        assertTrue(dto.getCreated() == topics2.size());
        
        List<Topic> ts = PersonalizedStreamsClient.getTopics(site, 2, 0);
        assertTrue(ts.size() == 2);
        
        int deleted = PersonalizedStreamsClient.patchTopics(site, topics2);
        assertTrue(deleted == topics2.size());
        
        ts = PersonalizedStreamsClient.getTopics(site, null, null);
        assertTrue(ts.isEmpty());
    }
    
    @Test
    public void testCollectionTopicApi_network() {
        PersonalizedStreamsClient.postTopics(network, topics);
        
        List<String> topicIds = PersonalizedStreamsClient.getCollectionTopics(site, COLLECTION_ID);
        assertTrue(topicIds.isEmpty());
        
        int added = PersonalizedStreamsClient.postCollectionTopics(site, COLLECTION_ID, topics);
        assertTrue(added == 7);
        
        topicIds = PersonalizedStreamsClient.getCollectionTopics(site, COLLECTION_ID);
        assertTrue(topicIds.size() == 7);

        TopicPutDto dto = PersonalizedStreamsClient.putCollectionTopics(site, COLLECTION_ID, topics1);
        assertTrue(dto.getAdded() > 0 || dto.getRemoved() > 0);
        
        topicIds = PersonalizedStreamsClient.getCollectionTopics(site, COLLECTION_ID);
        assertTrue(topicIds.size() == 3);
        
        int deleted = PersonalizedStreamsClient.patchCollectionTopics(site, COLLECTION_ID, topics);
        assertTrue(deleted == 3);
        
        topicIds = PersonalizedStreamsClient.getCollectionTopics(site, COLLECTION_ID);
        assertTrue(topicIds.isEmpty());
        
        PersonalizedStreamsClient.patchTopics(network, topics);
        topicIds = PersonalizedStreamsClient.getCollectionTopics(site, COLLECTION_ID);
        
        topicIds.size();
    }

    @Test
    public void testCollectionTopicApi_site() {
        PersonalizedStreamsClient.postTopics(site, topics2);
        
        int added = PersonalizedStreamsClient.postCollectionTopics(site, COLLECTION_ID, topics2);
        assertTrue(added == topics2.size());
        
        TopicPutDto dto = PersonalizedStreamsClient.putCollectionTopics(site, COLLECTION_ID, Lists.newArrayList(topic4));
        assertNotNull(dto.getAdded() > 0 || dto.getRemoved() > 0);
        
        List<String> topicIds = PersonalizedStreamsClient.getCollectionTopics(site, COLLECTION_ID);
        assertTrue(topicIds.size() == 1);

        int deleted = PersonalizedStreamsClient.patchCollectionTopics(site, COLLECTION_ID, topics2);
        assertTrue(deleted == 1);
        
        topicIds = PersonalizedStreamsClient.getCollectionTopics(site, COLLECTION_ID);
        assertTrue(topicIds.isEmpty());
        
        PersonalizedStreamsClient.patchTopics(site, topics2);
    }
    
    @Test
    public void testSubscriberApi() {
        PersonalizedStreamsClient.patchTopics(network, topics);
        PersonalizedStreamsClient.postTopics(network, topics);
        
        List<Subscription> su = PersonalizedStreamsClient.getSubscriptions(network, USER);
        assertTrue(su.isEmpty());
        
        int subs = PersonalizedStreamsClient.postSubscriptions(network, USER, topics);
        assertTrue(subs == 7);

        assertTrue(PersonalizedStreamsClient.putSubscriptions(network, USER, topics1).isChanged());
        
        su = PersonalizedStreamsClient.getSubscribers(network, topic, 100, 0);

        int del = PersonalizedStreamsClient.patchSubscriptions(network, USER, topics);
        assertTrue(del == 3);
        
        List<Subscription> sub = PersonalizedStreamsClient.getSubscriptions(network, USER);
        assertTrue(sub.isEmpty());

        PersonalizedStreamsClient.patchTopics(network, topics);
    }
    
    @Test
    public void testTimelineStream() {
        Topic topic = network.createOrUpdateTopic("TOPIC", "LABEL");
        JSONObject test = PersonalizedStreamsClient.getTimelineStream(network, topic.getId() +":topicStream", 50, null, null);
        assertNotNull(test);
        network.deleteTopic(topic);
    }
}
