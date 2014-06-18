package com.livefyre.api.client;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.client.proxy.WebResourceFactory;

import com.google.common.collect.Lists;
import com.livefyre.api.client.filter.LftokenAuthFilter;
import com.livefyre.api.dto.CollectionTopicDataDto;
import com.livefyre.api.dto.CollectionTopicDto;
import com.livefyre.api.dto.Subscription;
import com.livefyre.api.dto.Subscription.Type;
import com.livefyre.api.dto.SubscriptionDataDto;
import com.livefyre.api.dto.Topic;
import com.livefyre.api.dto.TopicDataDto;
import com.livefyre.api.forms.PatchTopicsForm;
import com.livefyre.api.forms.SubscriptionsForm;
import com.livefyre.api.forms.TopicIdsForm;
import com.livefyre.api.forms.TopicsForm;
import com.livefyre.core.LfCore;
import com.livefyre.core.Network;
import com.livefyre.core.Site;

public class PersonalizedStreamsClientImpl {

    private static final String BASE_URL = "http://quill.%1$s/api/v4";
    
    /* Topic API */
    public static Topic getTopic(LfCore core, String topicId) {
        return client(core).getTopic(topicId).getData().getTopic();
    }
    
    public static boolean postTopic(LfCore core, Topic topic) {
        TopicDataDto dto = postTopics(core, Arrays.asList(topic));
        return dto.getCreated() == 1 || dto.getUpdated() == 1;
    }
    
    public static boolean deleteTopic(LfCore core, Topic topic) {
        TopicDataDto dto = deleteTopics(core, Arrays.asList(topic));
        return dto.getDeleted() == 1;
    }
    
    /* Multiple Topic API */
    public static List<Topic> getTopics(LfCore core, Integer limit, Integer offset) {
        return client(core).getTopics(core.getUrn(), limit, offset).getData().getTopics();
    }
    
    public static TopicDataDto postTopics(LfCore core, List<Topic> topics) {
        for (Topic topic : topics) {
            if (topic.getLabel().length() > 128 || StringUtils.isEmpty(topic.getLabel())) {
                throw new IllegalArgumentException("Topic label is of incorrect length or empty.");
            }
        }
        return client(core).postTopics(core.getUrn(), new TopicsForm(topics)).getData();
    }
    
    public static TopicDataDto deleteTopics(LfCore core, List<Topic> topics) {
        return client(core).patchTopics(core.getUrn(), new PatchTopicsForm(getTopicIds(topics)), null).getData();
    }
    
    /* Collection Topic API */
    public static List<String> getCollectionTopics(Site site, String collectionId) {
        return client(site.getNetwork()).getCollectionTopics(site.getUrn(), collectionId).getData().getTopicIds();
    }
    
    public static Integer postCollectionTopics(Site site, String collectionId, List<Topic> topics) {
        CollectionTopicDto postCollectionTopics = client(site.getNetwork())
                .postCollectionTopics(site.getUrn(), collectionId, new TopicIdsForm(getTopicIds(topics)));
        return postCollectionTopics
                .getData().getAdded();
    }
    
    public static Boolean putCollectionTopics(Site site, String collectionId, List<Topic> topics) {
        CollectionTopicDataDto data = client(site.getNetwork())
                .putCollectionTopics(site.getUrn(), collectionId, new TopicIdsForm(getTopicIds(topics)))
                .getData();
        return data.getAdded() > 0 || data.getRemoved() > 0;
    }
    
    public static Integer deleteCollectionTopics(Site site, String collectionId, List<Topic> topics) {
        return client(site.getNetwork())
                .patchCollectionTopics(site.getUrn(), collectionId, new PatchTopicsForm(getTopicIds(topics)), null)
                .getData().getRemoved();
    }
    
    /* Subscription API */
    public static List<Subscription> getSubscriptions(Network network, String user) {
        return client(network).getSubscriptions(network.getUserUrn(user)).getData().getSubscriptions();
    }
    
    public static Integer postSubscriptions(Network network, String user, List<Topic> topics) {
        String userUrn = network.getUserUrn(user);
        return client(network, user)
                .postSubscriptions(userUrn, new SubscriptionsForm(getSubscriptions(topics, userUrn)))
                .getData().getAdded();
    }
    
    public static Boolean putSubscriptions(Network network, String user, List<Topic> topics) {
        String userUrn = network.getUserUrn(user);
        SubscriptionDataDto data = client(network, user)
                .putSubscriptions(userUrn, new SubscriptionsForm(getSubscriptions(topics, userUrn)))
                .getData();
        return data.getAdded() > 0 || data.getRemoved() > 0;
    }

    public static Integer deleteSubscriptions(Network network, String user, List<Topic> topics) {
        String userUrn = network.getUserUrn(user);
        return client(network, user)
                .patchSubscriptions(userUrn, new SubscriptionsForm(getSubscriptions(topics, userUrn)), null)
                .getData().getRemoved();
    }
    
    /* Subscriber API */
    public static List<Subscription> getSubscribers(Network network, Topic topic, Integer limit, Integer offset) {
        return client(network).getSubscribers(topic.getId(), limit, offset).getData().getSubscriptions();
    }
    
    /* Helper methods */
    private static PersonalizedStreamsClient client(LfCore core) {
        return client(core, null);
    }
    
    private static PersonalizedStreamsClient client(LfCore core, String user) {
        ClientConfig config = new ClientConfig();
        config.connectorProvider(new HttpUrlConnectorProvider().useSetMethodWorkaround());
        
        Client client = ClientBuilder.newClient(config);
//        client.property(ClientProperties.CONNECT_TIMEOUT, 1000);
//        client.property(ClientProperties.READ_TIMEOUT,    10000);
        
        WebTarget target = client.target(String.format(BASE_URL, core.getNetworkName()));
        target.register(new LftokenAuthFilter(core, user));
        
        return WebResourceFactory.newResource(PersonalizedStreamsClient.class, target);
    }

    private static List<String> getTopicIds(List<Topic> topics) {
        List<String> ids = Lists.newArrayList();
        for (Topic topic : topics) {
            ids.add(topic.getId());
        }
        return ids;
    }
    
    private static List<Subscription> getSubscriptions(List<Topic> topics, String user) {
        List<Subscription> subscriptions = Lists.newArrayList();
        for (Topic topic : topics) {
            subscriptions.add(new Subscription(topic.getId(), user, Type.personalStream));
        }
        return subscriptions;
    }
}
