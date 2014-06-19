package com.livefyre.core;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.livefyre.Livefyre;
import com.livefyre.api.entity.Topic;

@Ignore
public class NetworkApiTest {
     private static final String NETWORK_NAME = "<NETWORK-NAME>";
     private static final String NETWORK_KEY = "<NETWORK-KEY>";

    private Network network;

    @Before
    public void setup() {
        this.network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
    }

    @Test
    public void testNetworkTopicApi() {
        Topic topic = network.createOrUpdateTopic("1", "UNO");
        topic = network.getTopic("1");
        network.deleteTopic(topic);

        List<Topic> topics = network.createOrUpdateTopics(ImmutableMap.of("1", "UNO"));
        topics = network.getTopics();
        network.deleteTopics(topics);
    }
}
