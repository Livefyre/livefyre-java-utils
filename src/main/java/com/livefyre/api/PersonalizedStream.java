package com.livefyre.api;

import java.security.InvalidKeyException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.livefyre.api.filter.LftokenAuthFilter;
import com.livefyre.core.Collection;
import com.livefyre.core.LfCore;
import com.livefyre.core.Network;
import com.livefyre.entity.Subscription;
import com.livefyre.entity.Subscription.Type;
import com.livefyre.entity.Topic;
import com.livefyre.utils.LivefyreJwtUtil;
import com.livefyre.utils.LivefyreUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

public class PersonalizedStream {

    private static final String BASE_URL = "%s/api/v4";
    private static final String STREAM_BASE_URL = "%s/api/v4";
    
    private static final String TOPIC_PATH = "/%s/";
    private static final String MULTIPLE_TOPIC_PATH = "/%s:topics/";
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
        
        return Topic.serializeFromJson(
                LivefyreUtil.stringToJson(jsonResp).getAsJsonObject("data").getAsJsonObject("topic"));
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
        JsonArray data = LivefyreUtil.stringToJson(jsonResp).getAsJsonObject("data").getAsJsonArray("topics");
        
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                topics.add(Topic.serializeFromJson(data.get(i).getAsJsonObject()));
            }
        }
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
        
        String form = LivefyreUtil.mapToJsonString(ImmutableMap.<String, Object>of("topics", topics));
        builder(core)
            .path(String.format(MULTIPLE_TOPIC_PATH, core.getUrn()))
            .accept(MediaType.APPLICATION_JSON)
            .type(MediaType.APPLICATION_JSON)
            .post(String.class, form);
        
        // Doesn't matter what the response details are here as long as it's a 200.
        return topics;
    }
    
    public static int deleteTopics(LfCore core, List<Topic> topics) {
        String form = LivefyreUtil.mapToJsonString(ImmutableMap.<String, Object>of("delete", getTopicIds(topics)));
        String jsonResp = builder(core)
                .path(String.format(MULTIPLE_TOPIC_PATH, core.getUrn()))
                .queryParam("_method", PATCH_METHOD)
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(String.class, form);
        
        JsonObject data = LivefyreUtil.stringToJson(jsonResp).getAsJsonObject("data");
        return data.has("deleted") ? data.get("deleted").getAsInt() : 0;
    }
    
    /* Collection Topic API */
    public static List<String> getCollectionTopics(Collection collection) {
        String jsonResp = builder(collection)
                .path(String.format(MULTIPLE_TOPIC_PATH, collection.getUrn()))
                .accept(MediaType.APPLICATION_JSON)
                .get(String.class);
        
        List<String> topicIds = Lists.newArrayList();
        JsonArray data = LivefyreUtil.stringToJson(jsonResp).getAsJsonObject("data").getAsJsonArray("topicIds");
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                topicIds.add(data.get(i).getAsString());
            }
        }
        return topicIds;
    }
    
    public static int addCollectionTopics(Collection collection, List<Topic> topics) {
        String form = LivefyreUtil.mapToJsonString(ImmutableMap.<String, Object>of("topicIds", getTopicIds(topics)));
        
        String jsonResp = builder(collection)
                .path(String.format(MULTIPLE_TOPIC_PATH, collection.getUrn()))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(String.class, form);
    
        JsonObject data = LivefyreUtil.stringToJson(jsonResp).getAsJsonObject("data");
        return data.has("added") ? data.get("added").getAsInt() : 0;
    }
    
    public static Map<String, Integer> replaceCollectionTopics(Collection collection, List<Topic> topics) {
        String form = LivefyreUtil.mapToJsonString(ImmutableMap.<String, Object>of("topicIds", getTopicIds(topics)));

        String jsonResp = builder(collection)
                .path(String.format(MULTIPLE_TOPIC_PATH, collection.getUrn()))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .put(String.class, form);

        Map<String, Integer> results = Maps.newHashMap();
        JsonObject data = LivefyreUtil.stringToJson(jsonResp).getAsJsonObject("data");

        results.put("added", data.has("added") ? data.get("added").getAsInt() : 0);
        results.put("removed", data.has("removed") ? data.get("removed").getAsInt() : 0);
        return results;
    }
    
    public static int removeCollectionTopics(Collection collection, List<Topic> topics) {
        String form = LivefyreUtil.mapToJsonString(ImmutableMap.<String, Object>of("delete", getTopicIds(topics)));
        
        String jsonResp = builder(collection)
                .path(String.format(MULTIPLE_TOPIC_PATH, collection.getUrn()))
                .queryParam("_method", PATCH_METHOD)
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(String.class, form);
        
        JsonObject data = LivefyreUtil.stringToJson(jsonResp).getAsJsonObject("data");
        return data.has("removed") ? data.get("removed").getAsInt() : 0;
    }
    
    /* Subscription API */
    public static List<Subscription> getSubscriptions(Network network, String userId) {
        String jsonResp = builder(network)
                .path(String.format(USER_SUBSCRIPTION_PATH, network.getUserUrn(userId)))
                .accept(MediaType.APPLICATION_JSON)
                .get(String.class);
        
        List<Subscription> subscriptions = Lists.newArrayList();
        JsonArray data = LivefyreUtil.stringToJson(jsonResp).getAsJsonObject("data").getAsJsonArray("subscriptions");
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                subscriptions.add(Subscription.serializeFromJson(data.get(i).getAsJsonObject()));
            }
        }
        return subscriptions;
    }
    
    public static int addSubscriptions(Network network, String userToken, List<Topic> topics) {
        String userId = getUserFromToken(network, userToken);
        String userUrn = network.getUserUrn(userId);
        String form = LivefyreUtil.mapToJsonString(ImmutableMap.<String, Object>of("subscriptions", buildSubscriptions(topics, userUrn)));

        String jsonResp = builder(network, userToken)
                .path(String.format(USER_SUBSCRIPTION_PATH, userUrn))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(String.class, form);
        
        JsonObject data = LivefyreUtil.stringToJson(jsonResp).getAsJsonObject("data");
        return data.has("added") ? data.get("added").getAsInt() : 0;
    }
    
    public static Map<String, Integer> replaceSubscriptions(Network network, String userToken, List<Topic> topics) {
        String userId = getUserFromToken(network, userToken);
        String userUrn = network.getUserUrn(userId);
        String form = LivefyreUtil.mapToJsonString(ImmutableMap.<String, Object>of("subscriptions", buildSubscriptions(topics, userUrn)));

        String jsonResp = builder(network, userToken)
                .path(String.format(USER_SUBSCRIPTION_PATH, userUrn))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .put(String.class, form);
        
        Map<String, Integer> results = Maps.newHashMap();
        JsonObject data = LivefyreUtil.stringToJson(jsonResp).getAsJsonObject("data");

        results.put("added", data.has("added") ? data.get("added").getAsInt() : 0);
        results.put("removed", data.has("removed") ? data.get("removed").getAsInt() : 0);
        return results;
    }

    public static int removeSubscriptions(Network network, String userToken, List<Topic> topics) {
        String userId = getUserFromToken(network, userToken);
        String userUrn = network.getUserUrn(userId);
        String form = LivefyreUtil.mapToJsonString(ImmutableMap.<String, Object>of("delete", buildSubscriptions(topics, userUrn)));

        String jsonResp = builder(network, userToken)
                .path(String.format(USER_SUBSCRIPTION_PATH, userUrn))
                .queryParam("_method", PATCH_METHOD)
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .post(String.class, form);
        
        JsonObject data = LivefyreUtil.stringToJson(jsonResp).getAsJsonObject("data");
        return data.has("removed") ? data.get("removed").getAsInt() : 0;
    }
    
    public static List<Subscription> getSubscribers(Network network, Topic topic, Integer limit, Integer offset) {
        String jsonResp = builder(network)
                .path(String.format(TOPIC_SUBSCRIPTION_PATH, topic.getId()))
                .queryParam("limit", limit == null ? "100" : limit.toString())
                .queryParam("offset", offset == null ? "0" : offset.toString())
                .accept(MediaType.APPLICATION_JSON)
                .get(String.class);
        
        List<Subscription> subscriptions = Lists.newArrayList();
        JsonArray data = LivefyreUtil.stringToJson(jsonResp).getAsJsonObject("data").getAsJsonArray("subscriptions");
        
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                subscriptions.add(Subscription.serializeFromJson(data.get(i).getAsJsonObject()));
            }
        }
        return subscriptions;
    }
    
    /**
     * This call is used specifically by the TimelineCursor class.  
     */
    public static JsonObject getTimelineStream(LfCore core, String resource, Integer limit, String until, String since) {
        WebResource r = streamBuilder(core)
                .path(TIMELINE_PATH)
                .queryParam("limit", limit == null ? "50" : limit.toString())
                .queryParam("resource", resource);
        
        if (until != null) {
            r = r.queryParam("until", until);
        } else if (since != null) {
            r = r.queryParam("since", since);
        }
        
        return LivefyreUtil.stringToJson(r.accept(MediaType.APPLICATION_JSON).get(String.class));
    }
    
    /* Helper methods */
    private static WebResource builder(LfCore core) {
        return builder(core, null);
    }
    
    private static WebResource builder(LfCore core, String userToken) {
        return client(core, userToken).resource(String.format(BASE_URL, Domain.quill(core)));
    }
    
    private static WebResource streamBuilder(LfCore core) {
        return client(core, null).resource(String.format(STREAM_BASE_URL, Domain.bootstrap(core)));
    }

    private static Client client(LfCore core, String userToken) {
        Client c = Client.create();
        c.getProperties().put(URLConnectionClientHandler.PROPERTY_HTTP_URL_CONNECTION_SET_METHOD_WORKAROUND, true);
        c.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, 1000);
        c.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, 10000);
        c.addFilter(new LftokenAuthFilter(core, userToken));
        return c;
    }
    
    private static List<String> getTopicIds(List<Topic> topics) {
        List<String> ids = Lists.newArrayList();
        for (Topic topic : topics) {
            ids.add(topic.getId());
        }
        return ids;
    }
    
    private static List<Subscription> buildSubscriptions(List<Topic> topics, String userUrn) {
        List<Subscription> subscriptions = Lists.newArrayList();
        for (Topic topic : topics) {
            subscriptions.add(new Subscription(topic.getId(), userUrn, Type.personalStream, null));
        }
        return subscriptions;
    }

    private static String getUserFromToken(Network network, String userToken) {
        JsonObject json;
        try {
            json = LivefyreJwtUtil.decodeLivefyreJwt(network.getKey(), userToken);
        } catch (InvalidKeyException e1) {
            throw new IllegalArgumentException("The userToken provided does not belong to this network.");
        }
        return json.get("user_id").getAsString();
    }
}
