package com.livefyre.api.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.proxy.WebResourceFactory;

import com.google.common.collect.Lists;
import com.livefyre.api.client.filter.LftokenAuthFilter;
import com.livefyre.api.dto.CollectionTopicDto;
import com.livefyre.api.dto.PostDto;
import com.livefyre.api.dto.Subscription;
import com.livefyre.api.dto.Subscription.Type;
import com.livefyre.api.dto.Topic;
import com.livefyre.api.dto.TopicsDto;
import com.livefyre.core.Network;
import com.livefyre.core.Site;

public class PersonalizedStreamsClientImpl {

    private static final String BASE_URL = "http://%1$s/api/v4/%1$s";
    private static final String SITE_PATH_ADD = BASE_URL+"/site/%2$s/";
    
    /* Topic API */
    public static Topic getNetworkTopic(Network network, String topicId) {
        checkNotNull(network);
        checkNotNull(topicId);
        return client(network).getTopic(topicId);
    }
    
    public static boolean postNetworkTopic(Network network, Topic topic) {
        checkNotNull(network);
        checkNotNull(topic);
        TopicsDto dto = postNetworkTopics(network, Arrays.asList(topic));
        return dto.getCreated() == 1 || dto.getUpdated() == 1;
    }
    
    public static boolean deleteNetworkTopic(Network network, Topic topic) {
        checkNotNull(network);
        checkNotNull(topic);
        TopicsDto dto = deleteNetworkTopics(network, Arrays.asList(topic));
        return dto.getDeleted() == 1;
    }
    
    public static Topic getSiteTopic(Site site, String topicId) {
        checkNotNull(site);
        checkNotNull(topicId);
        return client(site).getTopic(topicId);
    }
    
    public static boolean postSiteTopic(Site site, Topic topic) {
        checkNotNull(site);
        checkNotNull(topic);
        TopicsDto dto = postSiteTopics(site, Arrays.asList(topic));
        return dto.getCreated() == 1 || dto.getUpdated() == 1;
    }
    
    public static boolean deleteSiteTopic(Site site, Topic topic) {
        checkNotNull(site);
        checkNotNull(topic);
        TopicsDto dto = deleteSiteTopics(site, Arrays.asList(topic));
        return dto.getDeleted() == 1;
    }
    
    /* Multiple Topic API */
    public static List<Topic> getNetworkTopics(Network network, Integer limit, Integer offset) {
        checkNotNull(network);
        return client(network).getTopics(limit, offset);
    }
    
    public static TopicsDto postNetworkTopics(Network network, List<Topic> topics) {
        checkNotNull(network);
        checkNotNull(topics);
        for (Topic topic : topics) {
            if (topic.getLabel().length() > 128 || StringUtils.isEmpty(topic.getLabel())) {
                throw new IllegalArgumentException("Topic label is of incorrect length or empty.");
            }
        }
        return client(network).postTopics(topics);
    }
    
    public static TopicsDto deleteNetworkTopics(Network network, List<Topic> topics) {
        checkNotNull(network);
        checkNotNull(topics);
        return client(network).patchTopics(getTopicIds(topics));
    }
    
    public static List<Topic> getSiteTopics(Site site, Integer limit, Integer offset) {
        checkNotNull(site);
        return client(site).getTopics(limit, offset);
    }
    
    public static TopicsDto postSiteTopics(Site site, List<Topic> topics) {
        checkNotNull(site);
        checkNotNull(topics);
        for (Topic topic : topics) {
            if (topic.getLabel().length() > 128 || StringUtils.isEmpty(topic.getLabel())) {
                throw new IllegalArgumentException("Topic label is of incorrect length or empty.");
            }
        }
        return client(site).postTopics(topics);
    }
    
    public static TopicsDto deleteSiteTopics(Site site, List<Topic> topics) {
        checkNotNull(site);
        checkNotNull(topics);
        return client(site).patchTopics(getTopicIds(topics));
    }
    
    /* Collection Topic API */
    public static CollectionTopicDto getCollectionTopics(Site site, String collectionId) {
        checkNotNull(site);
        checkNotNull(collectionId);
        return client(site).getCollectionTopics(collectionId);
    }
    
    public static Integer postCollectionTopics(Site site, String collectionId, List<Topic> topics) {
        checkNotNull(site);
        checkNotNull(collectionId);
        checkNotNull(topics);
        return client(site).postCollectionTopics(collectionId, getTopicIds(topics)).getAdded();
    }
    
    public static PostDto putCollectionTopics(Site site, String collectionId, List<Topic> topics) {
        checkNotNull(site);
        checkNotNull(collectionId);
        checkNotNull(topics);
        return client(site).putCollectionTopics(collectionId, getTopicIds(topics));
    }
    
    public static Integer deleteCollectionTopics(Site site, String collectionId, List<Topic> topics) {
        checkNotNull(site);
        checkNotNull(collectionId);
        checkNotNull(topics);
        return client(site).patchCollectionTopics(collectionId, getTopicIds(topics)).getRemoved();
    }
    
    /* Subscription API */
    public static List<Subscription> getSubscriptions(Network network, String user) {
        checkNotNull(network);
        checkNotNull(user);
        return client(network).getSubscriptions(user);
    }
    
    public static Integer postSubscriptions(Network network, String user, List<Topic> topics) {
        checkNotNull(network);
        checkNotNull(user);
        checkNotNull(topics);
        return client(network).postSubscriptions(user, getSubscriptions(topics, user)).getAdded();
    }
    
    public static PostDto putSubscriptions(Network network, String user, List<Topic> topics) {
        checkNotNull(network);
        checkNotNull(user);
        checkNotNull(topics);
        return client(network).putSubscriptions(user, getSubscriptions(topics, user));
    }

    public static Integer deleteSubscriptions(Network network, String user, List<Topic> topics) {
        checkNotNull(network);
        checkNotNull(user);
        checkNotNull(topics);
        return client(network).patchSubscriptions(user, getSubscriptions(topics, user)).getRemoved();
    }
    
    /* Subscriber API */
    public static List<String> getSubscribers(Network network, Topic topic, Integer limit, Integer offset) {
        checkNotNull(network);
        checkNotNull(topic);
        return client(network).getSubscribers(topic.getId(), limit, offset);
    }
    
    /* Helper methods */
    protected static PersonalizedStreamsClient client(Network network) {
        return client(network, null);
    }
    
    protected static PersonalizedStreamsClient client(Site site) {
        return client(site.getNetwork(), site);
    }
    
    private static PersonalizedStreamsClient client(Network network, Site site) {
        Client client = ClientBuilder.newClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, 1000);
        client.property(ClientProperties.READ_TIMEOUT,    5000);
        
        WebTarget target = client.target(site == null ?
              String.format(BASE_URL, network.getName()) : String.format(SITE_PATH_ADD, network.getName(), site.getId()));
        target.register(new LftokenAuthFilter(network));
        
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
            subscriptions.add(new Subscription(topic.getId(), user, Type.PERSONAL_STREAM));
        }
        return subscriptions;
    }
}
