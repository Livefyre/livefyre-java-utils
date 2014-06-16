package com.livefyre.api.client;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.livefyre.Livefyre;
import com.livefyre.api.dto.Subscription;
import com.livefyre.api.dto.Subscription.Type;
import com.livefyre.api.dto.Topic;
import com.livefyre.core.Network;
import com.livefyre.core.Site;


/*
 * TODO: Add asserts - correct logic.
 */
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
    @Ignore
    public void testNetworkTopicApi() {
        PersonalizedStreamsClientImpl.getNetworkTopic(network, topic.getId());
        PersonalizedStreamsClientImpl.postNetworkTopic(network, topic);
        PersonalizedStreamsClientImpl.getNetworkTopic(network, topic.getId());
        
        PersonalizedStreamsClientImpl.deleteNetworkTopic(network, topic);
        PersonalizedStreamsClientImpl.getNetworkTopic(network, topic.getId());
    }
    
    @Test
    @Ignore
    public void testMultipleNetworkTopicApi() {
        PersonalizedStreamsClientImpl.postNetworkTopics(network, topics);
        PersonalizedStreamsClientImpl.getNetworkTopics(network, 3, 0);
        PersonalizedStreamsClientImpl.deleteNetworkTopics(network, topics);
        PersonalizedStreamsClientImpl.getNetworkTopics(network, null, null);
    }
    
    @Test
    @Ignore
    public void testSiteTopicApi() {
        PersonalizedStreamsClientImpl.postSiteTopic(site, topic1);
        PersonalizedStreamsClientImpl.getSiteTopic(site, topic1.getId());
        
        PersonalizedStreamsClientImpl.deleteSiteTopic(site, topic1);
        PersonalizedStreamsClientImpl.getSiteTopic(site, topic1.getId());
    }
    
    @Test
    @Ignore
    public void testMultipleSiteTopicApi() {
        PersonalizedStreamsClientImpl.postSiteTopics(site, topics);
        PersonalizedStreamsClientImpl.getSiteTopics(site, 3, 0);
        PersonalizedStreamsClientImpl.deleteSiteTopics(site, topics);
        PersonalizedStreamsClientImpl.getSiteTopics(site, null, null);
    }
    
    @Test
    @Ignore
    public void testCollectionTopicApi() {
        PersonalizedStreamsClientImpl.postNetworkTopics(network, topics);
        
        PersonalizedStreamsClientImpl.getCollectionTopics(site, COLLECTION_ID);
        PersonalizedStreamsClientImpl.postCollectionTopics(site, COLLECTION_ID, topics);
        PersonalizedStreamsClientImpl.putCollectionTopics(site, COLLECTION_ID, topics1);
        PersonalizedStreamsClientImpl.deleteCollectionTopics(site, COLLECTION_ID, topics);
        
        PersonalizedStreamsClientImpl.deleteNetworkTopics(network, topics);

        PersonalizedStreamsClientImpl.postSiteTopics(site, topics);
        PersonalizedStreamsClientImpl.getCollectionTopics(site, COLLECTION_ID);
        PersonalizedStreamsClientImpl.postCollectionTopics(site, COLLECTION_ID, topics2);
        PersonalizedStreamsClientImpl.getCollectionTopics(site, COLLECTION_ID);
        
        PersonalizedStreamsClientImpl.putCollectionTopics(site, COLLECTION_ID, topics1);
        PersonalizedStreamsClientImpl.getCollectionTopics(site, COLLECTION_ID);

        List<Topic> topics3 = Lists.newArrayList(topic2);
        PersonalizedStreamsClientImpl.deleteCollectionTopics(site, COLLECTION_ID, topics3);
    }
    
    @Test
    @Ignore
    public void testSubscriberApi() {
        String user1 = "user-1";
        String user2 = "user-2";
        
        Subscription subscription = new Subscription(topic1.getId(), user1, Type.PERSONAL_STREAM);
        
        PersonalizedStreamsClientImpl.getSubscriptions(network, user1);
    }
}
