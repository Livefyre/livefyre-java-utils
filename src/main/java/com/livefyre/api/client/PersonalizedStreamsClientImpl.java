package com.livefyre.api.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.google.common.collect.Lists;
import com.livefyre.api.client.filter.LftokenAuthFilter;
import com.livefyre.api.dto.CollectionTopicDto;
import com.livefyre.api.dto.Subscription;
import com.livefyre.api.dto.Topic;
import com.livefyre.core.Network;
import com.livefyre.core.Site;

public class PersonalizedStreamsClientImpl {

    private static final String BASE_URL = "https://%1$s/api/v4/%1$s";
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
        CollectionTopicDto dto = postNetworkTopics(network, Arrays.asList(topic));
        return dto.getCreated() == 1 || dto.getUpdated() == 1;
    }
    
    public static boolean deleteNetworkTopic(Network network, Topic topic) {
        checkNotNull(network);
        checkNotNull(topic);
        CollectionTopicDto dto = deleteNetworkTopics(network, Arrays.asList(topic));
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
        CollectionTopicDto dto = postSiteTopics(site, Arrays.asList(topic));
        return dto.getCreated() == 1 || dto.getUpdated() == 1;
    }
    
    public static boolean deleteSiteTopic(Site site, Topic topic) {
        checkNotNull(site);
        checkNotNull(topic);
        CollectionTopicDto dto = deleteSiteTopics(site, Arrays.asList(topic));
        return dto.getDeleted() == 1;
    }
    
    /* Multiple Topic API */
    public static List<Topic> getNetworkTopics(Network network, Integer limit, Integer offset) {
        checkNotNull(network);
        checkNotNull(limit);
        checkNotNull(offset);
        return client(network).getTopics(limit, offset);
    }
    
    public static CollectionTopicDto postNetworkTopics(Network network, List<Topic> topics) {
        checkNotNull(network);
        checkNotNull(topics);
        for (Topic topic : topics) {
            if (topic.getLabel().length() > 128 || StringUtils.isEmpty(topic.getLabel())) {
                throw new IllegalArgumentException("Topic label is of incorrect length or empty.");
            }
        }
        return client(network).postTopics(topics);
    }
    
    public static CollectionTopicDto deleteNetworkTopics(Network network, List<Topic> topics) {
        checkNotNull(network);
        checkNotNull(topics);
        return client(network).deleteTopics(getTopicIds(topics));
    }
    
    public static List<Topic> getSiteTopics(Site site, Integer limit, Integer offset) {
        checkNotNull(site);
        checkNotNull(limit);
        checkNotNull(offset);
        return client(site).getTopics(limit, offset);
    }
    
    public static CollectionTopicDto postSiteTopics(Site site, List<Topic> topics) {
        checkNotNull(site);
        checkNotNull(topics);
        for (Topic topic : topics) {
            if (topic.getLabel().length() > 128 || StringUtils.isEmpty(topic.getLabel())) {
                throw new IllegalArgumentException("Topic label is of incorrect length or empty.");
            }
        }
        return client(site).postTopics(topics);
    }
    
    public static CollectionTopicDto deleteSiteTopics(Site site, List<Topic> topics) {
        checkNotNull(site);
        checkNotNull(topics);
        return client(site).deleteTopics(getTopicIds(topics));
    }
    
    /* Collection Topic API */
    public static CollectionTopicDto getCollectionTopics(Site site, String collectionId) {
        checkNotNull(site);
        checkNotNull(collectionId);
        return client(site).getCollectionTopics(collectionId);
    }
    
    public static CollectionTopicDto postCollectionTopics(Site site, String collectionId, List<Topic> topics) {
        checkNotNull(site);
        checkNotNull(collectionId);
        checkNotNull(topics);
        return client(site).postCollectionTopics(collectionId, topics);
    }
    
    public static CollectionTopicDto putCollectionTopics(Site site, String collectionId, List<Topic> topics) {
        checkNotNull(site);
        checkNotNull(collectionId);
        checkNotNull(topics);
        return client(site).putCollectionTopics(collectionId, topics);
    }
    
    public static CollectionTopicDto deleteCollectionTopics(Site site, String collectionId, List<Topic> topics) {
        checkNotNull(site);
        checkNotNull(collectionId);
        checkNotNull(topics);
        return client(site).deleteCollectionTopics(collectionId, getTopicIds(topics));
    }
    
    /* Subscription API */
    public static List<Subscription> getSubscriptions(Network network, String user) {
        checkNotNull(network);
        checkNotNull(user);
        return client(network).getSubscriptions(user);
    }
    
    public static CollectionTopicDto postSubscriptions(Network network, String user, List<Topic> topics) {
        checkNotNull(network);
        checkNotNull(user);
        checkNotNull(topics);
        return client(network).postSubscriptions(user, getSubscriptions(topics));
    }
    
    public static CollectionTopicDto putSubscriptions(Network network, String user, List<Topic> topics) {
        checkNotNull(network);
        checkNotNull(user);
        checkNotNull(topics);
        return client(network).putSubscriptions(user, getSubscriptions(topics));
    }

    public static CollectionTopicDto deleteSubscriptions(Network network, String user, List<Topic> topics) {
        checkNotNull(network);
        checkNotNull(user);
        checkNotNull(topics);
        return client(network).deleteSubscriptions(user, getSubscriptions(topics));
    }
    
    /* Subscriber API */
    public static List<String> getSubscribers(Network network, Topic topic) {
        checkNotNull(network);
        checkNotNull(topic);
        return client(network).getSubscribers(topic.getId());
    }
    
    public static CollectionTopicDto postSubscribers(Network network, Topic topic, List<String> users) {
        checkNotNull(network);
        checkNotNull(topic);
        checkNotNull(users);
        return client(network).postSubscribers(topic.getId(), users);
    }
    
    public static CollectionTopicDto putSubscribers(Network network, Topic topic, List<String> users) {
        checkNotNull(network);
        checkNotNull(topic);
        checkNotNull(users);
        return client(network).putSubscribers(topic.getId(), users);
    }

    public static CollectionTopicDto deleteSubscribers(Network network, Topic topic, List<String> users) {
        checkNotNull(network);
        checkNotNull(topic);
        checkNotNull(users);
        return client(network).deleteSubscribers(topic.getId(), users);
    }
    
    /* Helper methods */
    private static List<String> getTopicIds(List<Topic> topics) {
        List<String> ids = Lists.newArrayList();
        for (Topic topic : topics) {
            ids.add(topic.getId());
        }
        return ids;
    }
    
    private static List<Subscription> getSubscriptions(List<Topic> topics) {
        List<Subscription> subscriptions = Lists.newArrayList();
        for (Topic topic : topics) {
            subscriptions.add(new Subscription(topic.getId()));
        }
        return subscriptions;
    }
    
    private static PersonalizedStreamsClient client(Network network) {
        return client(network, null);
    }
    
    private static PersonalizedStreamsClient client(Site site) {
        return client(site.getNetwork(), site);
    }
    
    private static PersonalizedStreamsClient client(Network network, Site site) {
        ResteasyClient client = new ResteasyClientBuilder().build();
        client.register(new LftokenAuthFilter(network));
        
        ResteasyWebTarget target = client.target(site == null ?
                String.format(BASE_URL, network.getName()) : String.format(SITE_PATH_ADD, network.getName(), site.getId()));
        
        PersonalizedStreamsClient psclient = target.proxy(PersonalizedStreamsClient.class);
        return psclient;
    }
}
