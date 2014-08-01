package com.livefyre.api.client;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.livefyre.api.client.filter.LftokenAuthFilter;
import com.livefyre.core.LfCore;
import com.livefyre.core.Network;
import com.livefyre.core.Site;
import com.livefyre.entity.Subscription;
import com.livefyre.entity.Subscription.Type;
import com.livefyre.entity.Topic;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

public class PersonalizedStreamsClient {

    private static final String BASE_URL = "https://%s.quill.fyre.co/api/v4";
    private static final String STREAM_BASE_URL = "https://bootstrap.livefyre.com/api/v4";
    
    private static final String TOPIC_PATH = "/%s/";
    private static final String MULTIPLE_TOPIC_PATH = "/%s:topics/";
    private static final String COLLECTION_TOPICS_PATH = "/%s:collection=%s:topics/";
    private static final String USER_SUBSCRIPTION_PATH = "/%s:subscriptions/";
    private static final String TOPIC_SUBSCRIPTION_PATH = "/%s:subscribers/";
    private static final String TIMELINE_PATH = "/timeline/";
    
    private static final String PATCH_METHOD = "PATCH";
    
    /* Topic API */
    public static Topic getTopic(LfCore core, String topicId) {
        String jsonResp = builder(core)
                .path(String.format(TOPIC_PATH, Topic.generateUrn(core, topicId)))
                .accept(MediaType.APPLICATION_JSON)
                .get(String.class);
        
        try {
            return Topic.serializeFromJson(new JSONObject(jsonResp).getJSONObject("data").getJSONObject("topic"));
        } catch (JSONException e) {}
        
        return null;
    }
    
    public static Topic createOrUpdateTopic(LfCore core, String topicId, String label) {
        return createOrUpdateTopics(core, ImmutableMap.of(topicId, label)).get(0);
    }
    
    public static boolean deleteTopic(LfCore core, Topic topic) {
        return deleteTopics(core, Lists.newArrayList(topic)) == 1;
    }
    
    /* Multiple Topic API */
    public static List<Topic> getTopics(LfCore core, Integer limit, Integer offset) {
        String jsonResp = builder(core)
                .path(String.format(MULTIPLE_TOPIC_PATH, core.getUrn()))
                .queryParam("limit", limit == null ? "100" : limit.toString())
                .queryParam("offset", offset == null ? "0" : offset.toString())
                .accept(MediaType.APPLICATION_JSON)
                .get(String.class);
        
        List<Topic> topics = Lists.newArrayList();
        try {
            JSONArray data = new JSONObject(jsonResp).getJSONObject("data").getJSONArray("topics");
            for (int i = 0; i < data.length(); i++) {
                topics.add(Topic.serializeFromJson(data.getJSONObject(i)));
            }
        } catch (JSONException e) {}
        
        return topics;
    }
    
    public static List<Topic> createOrUpdateTopics(LfCore core, Map<String, String> topicMap) {
        List<Topic> topics = Lists.newArrayList();
        for (String k : topicMap.keySet()) {
            String label = topicMap.get(k);
            
            if (StringUtils.isEmpty(label) || label.length() > 128) {
                throw new IllegalArgumentException("Topic label is of incorrect length or empty.");
            }
            
            topics.add(Topic.create(core, k, label));
        }
        
        String form = new JSONObject(ImmutableMap.<String, Object>of("topics", topics)).toString();
        builder(core)
            .path(String.format(MULTIPLE_TOPIC_PATH, core.getUrn()))
            .accept(MediaType.APPLICATION_JSON)
            .type(MediaType.APPLICATION_JSON)
            .post(String.class, form);
        
        // Doesn't matter what the response details are here as long as it's a 200.
        
        return topics;
    }
    
    public static int deleteTopics(LfCore core, List<Topic> topics) {
        String form = new JSONObject(ImmutableMap.<String, Object>of("delete", getTopicIds(topics))).toString();
        String jsonResp = builder(core)
                .path(String.format(MULTIPLE_TOPIC_PATH, core.getUrn()))
                .queryParam("_method", PATCH_METHOD)
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(String.class, form);
        
        try {
            JSONObject data = new JSONObject(jsonResp).getJSONObject("data");
            return data.getInt("deleted");
        } catch (JSONException e) {
            return 0;
        }
    }
    
    /* Collection Topic API */
    public static List<String> getCollectionTopics(Site site, String collectionId) {
        String jsonResp = builder(site.getNetwork())
                .path(String.format(COLLECTION_TOPICS_PATH, site.getUrn(), collectionId))
                .accept(MediaType.APPLICATION_JSON)
                .get(String.class);
        
        List<String> topicIds = Lists.newArrayList();
        try {
            JSONArray data = new JSONObject(jsonResp).getJSONObject("data").getJSONArray("topicIds");
            for (int i = 0; i < data.length(); i++) {
                topicIds.add(data.getString(i));
            }
        } catch (JSONException e) {}
        
        return topicIds;
    }
    
    public static int postCollectionTopics(Site site, String collectionId, List<Topic> topics) {
        String form = new JSONObject(ImmutableMap.<String, Object>of("topicIds", getTopicIds(topics))).toString();
        
        String jsonResp = builder(site.getNetwork())
                .path(String.format(COLLECTION_TOPICS_PATH, site.getUrn(), collectionId))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(String.class, form);
        
        try {
            JSONObject data = new JSONObject(jsonResp).getJSONObject("data");
            return data.getInt("added");
        } catch (JSONException e) {
            return 0;
        }
    }
    
    public static Map<String, Integer> putCollectionTopics(Site site, String collectionId, List<Topic> topics) {
        String form = new JSONObject(ImmutableMap.<String, Object>of("topicIds", getTopicIds(topics))).toString();
        
        String jsonResp = builder(site.getNetwork())
                .path(String.format(COLLECTION_TOPICS_PATH, site.getUrn(), collectionId))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .put(String.class, form);

        Map<String, Integer> results = Maps.newHashMap();
        try {
            JSONObject data = new JSONObject(jsonResp).getJSONObject("data");

            results.put("added", data.has("added") ? data.getInt("added") :0);
            results.put("removed", data.has("removed") ? data.getInt("removed") : 0);
        } catch (JSONException e) {}
        
        return results;
    }
    
    public static int patchCollectionTopics(Site site, String collectionId, List<Topic> topics) {
        String form = new JSONObject(ImmutableMap.<String, Object>of("delete", getTopicIds(topics))).toString();
        
        String jsonResp = builder(site.getNetwork())
                .path(String.format(COLLECTION_TOPICS_PATH, site.getUrn(), collectionId))
                .queryParam("_method", PATCH_METHOD)
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(String.class, form);
        
        try {
            JSONObject data = new JSONObject(jsonResp).getJSONObject("data");
            return data.getInt("removed");
        } catch (JSONException e) {
            return 0;
        }
    }
    
    /* Subscription API */
    public static List<Subscription> getSubscriptions(Network network, String user) {
        String jsonResp = builder(network)
                .path(String.format(USER_SUBSCRIPTION_PATH, network.getUserUrn(user)))
                .accept(MediaType.APPLICATION_JSON)
                .get(String.class);
        
        List<Subscription> subscriptions = Lists.newArrayList();
        try {
            JSONArray data = new JSONObject(jsonResp).getJSONObject("data").getJSONArray("subscriptions");
            for (int i = 0; i < data.length(); i++) {
                subscriptions.add(Subscription.serializeFromJson(data.getJSONObject(i)));
            }
        } catch (JSONException e) {}
        
        return subscriptions;
    }
    
    public static int postSubscriptions(Network network, String user, List<Topic> topics) {
        String userUrn = network.getUserUrn(user);
        String form = new JSONObject(ImmutableMap.<String, Object>of("subscriptions", buildSubscriptions(topics, userUrn))).toString();

        String jsonResp = builder(network, user)
                .path(String.format(USER_SUBSCRIPTION_PATH, userUrn))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(String.class, form);
        
        try {
            JSONObject data = new JSONObject(jsonResp).getJSONObject("data");
            return data.getInt("added");
        } catch (JSONException e) {
            return 0;
        }
    }
    
    public static Map<String, Integer> putSubscriptions(Network network, String user, List<Topic> topics) {
        String userUrn = network.getUserUrn(user);
        String form = new JSONObject(ImmutableMap.<String, Object>of("subscriptions", buildSubscriptions(topics, userUrn))).toString();

        String jsonResp = builder(network, user)
                .path(String.format(USER_SUBSCRIPTION_PATH, userUrn))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .put(String.class, form);
        
        Map<String, Integer> results = Maps.newHashMap();
        try {
            JSONObject data = new JSONObject(jsonResp).getJSONObject("data");

            results.put("added", data.has("added") ? data.getInt("added") :0);
            results.put("removed", data.has("removed") ? data.getInt("removed") : 0);
        } catch (JSONException e) {}
        
        return results;
    }

    public static int patchSubscriptions(Network network, String user, List<Topic> topics) {
        String userUrn = network.getUserUrn(user);
        String form = new JSONObject(ImmutableMap.<String, Object>of("delete", buildSubscriptions(topics, userUrn))).toString();

        String jsonResp = builder(network, user)
                .path(String.format(USER_SUBSCRIPTION_PATH, userUrn))
                .queryParam("_method", PATCH_METHOD)
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(String.class, form);
        
        try {
            JSONObject data = new JSONObject(jsonResp).getJSONObject("data");
            return data.getInt("removed");
        } catch (JSONException e) {
            return 0;
        }
    }
    
    public static List<Subscription> getSubscribers(Network network, Topic topic, Integer limit, Integer offset) {
        String jsonResp = builder(network)
                .path(String.format(TOPIC_SUBSCRIPTION_PATH, topic.getId()))
                .queryParam("limit", limit == null ? "100" : limit.toString())
                .queryParam("offset", offset == null ? "0" : offset.toString())
                .accept(MediaType.APPLICATION_JSON)
                .get(String.class);
        
        List<Subscription> subscriptions = Lists.newArrayList();
        try {
            JSONArray data = new JSONObject(jsonResp).getJSONObject("data").getJSONArray("subscriptions");
            for (int i = 0; i < data.length(); i++) {
                subscriptions.add(Subscription.serializeFromJson(data.getJSONObject(i)));
            }
        } catch (JSONException e) {}
        
        return subscriptions;
    }
    
    /**
     * This call is used specifically by the TimelineCursor class.  
     */
    public static JSONObject getTimelineStream(LfCore core, String resource, Integer limit, String until, String since) {
        WebResource r = streamBuilder(core)
                .path(TIMELINE_PATH)
                .queryParam("limit", limit == null ? "50" : limit.toString())
                .queryParam("resource", resource);
        
        if (until != null) {
            r = r.queryParam("until", until);
        } else if (since != null) {
            r = r.queryParam("since", since);
        }
        
        return new JSONObject(r.accept(MediaType.APPLICATION_JSON).get(String.class));
    }
    
    /* Helper methods */
    private static WebResource builder(LfCore core) {
        return builder(core, null);
    }
    
    private static WebResource builder(LfCore core, String user) {
        return client(core, user).resource(String.format(BASE_URL, core.getNetworkName()));
    }
    
    private static WebResource streamBuilder(LfCore core) {
        return client(core, null).resource(STREAM_BASE_URL);
    }

    private static Client client(LfCore core, String user) {
        Client c = Client.create();
        c.getProperties().put(URLConnectionClientHandler.PROPERTY_HTTP_URL_CONNECTION_SET_METHOD_WORKAROUND, true);
        c.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, 1000);
        c.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, 10000);
        c.addFilter(new LftokenAuthFilter(core, user));
        return c;
    }
    
    private static List<String> getTopicIds(List<Topic> topics) {
        List<String> ids = Lists.newArrayList();
        for (Topic topic : topics) {
            ids.add(topic.getId());
        }
        return ids;
    }
    
    private static List<Subscription> buildSubscriptions(List<Topic> topics, String user) {
        List<Subscription> subscriptions = Lists.newArrayList();
        for (Topic topic : topics) {
            subscriptions.add(new Subscription(topic.getId(), user, Type.personalStream, null));
        }
        return subscriptions;
    }
}
