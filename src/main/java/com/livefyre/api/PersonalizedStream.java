package com.livefyre.api;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.livefyre.api.filter.LftokenAuthFilter;
import com.livefyre.core.Collection;
import com.livefyre.core.LfCore;
import com.livefyre.core.Network;
import com.livefyre.cursor.TimelineCursor;
import com.livefyre.dto.Subscription;
import com.livefyre.dto.Topic;
import com.livefyre.exceptions.ApiException;
import com.livefyre.type.SubscriptionType;
import com.livefyre.utils.LivefyreUtil;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientProperties;

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
        Response response = builder(core)
                .path(String.format(TOPIC_PATH, Topic.generateUrn(core, topicId)))
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();
        JsonObject content = evaluateResponse(response);

        return Topic.serializeFromJson(
                content.getAsJsonObject("data").getAsJsonObject("topic"));
    }
    
    public static Topic createOrUpdateTopic(LfCore core, String topicId, String label) {
        return createOrUpdateTopics(core, ImmutableMap.of(topicId, label)).get(0);
    }
    
    public static boolean deleteTopic(LfCore core, Topic topic) {
        return deleteTopics(core, Lists.newArrayList(topic)) == 1;
    }
    
    /* Multiple Topic API */
    public static List<Topic> getTopics(LfCore core, Integer limit, Integer offset) {
        Response response = builder(core)
                .path(String.format(MULTIPLE_TOPIC_PATH, core.getUrn()))
                .queryParam("limit", limit == null ? "100" : limit.toString())
                .queryParam("offset", offset == null ? "0" : offset.toString())
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();
        JsonObject content = evaluateResponse(response);
        JsonArray topicsData = content.getAsJsonObject("data").getAsJsonArray("topics");
        
        List<Topic> topics = Lists.newArrayList();
        if (topicsData != null) {
            for (int i = 0; i < topicsData.size(); i++) {
                topics.add(Topic.serializeFromJson(topicsData.get(i).getAsJsonObject()));
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
        
        Response response = builder(core)
                .path(String.format(MULTIPLE_TOPIC_PATH, core.getUrn()))
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(form));
        evaluateResponse(response);
        
        // Doesn't matter what the response details are here as long as it's a 200.
        return topics;
    }
    
    public static int deleteTopics(LfCore core, List<Topic> topics) {
        String form = LivefyreUtil.mapToJsonString(ImmutableMap.<String, Object>of("delete", getTopicIds(topics)));
        
        Response response = builder(core)
                .path(String.format(MULTIPLE_TOPIC_PATH, core.getUrn()))
                .queryParam("_method", PATCH_METHOD)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(form));
        JsonObject content = evaluateResponse(response);
        JsonObject data = content.getAsJsonObject("data");
        
        return data.has("deleted") ? data.get("deleted").getAsInt() : 0;
    }
    
    /* Collection Topic API */
    public static List<String> getCollectionTopics(Collection collection) {
        Response response = builder(collection)
                .path(String.format(MULTIPLE_TOPIC_PATH, collection.getUrn()))
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();
        JsonObject content = evaluateResponse(response);
        JsonArray topicData = content.getAsJsonObject("data").getAsJsonArray("topicIds");
        
        List<String> topicIds = Lists.newArrayList();
        if (topicData != null) {
            for (int i = 0; i < topicData.size(); i++) {
                topicIds.add(topicData.get(i).getAsString());
            }
        }
        return topicIds;
    }
    
    public static int addCollectionTopics(Collection collection, List<Topic> topics) {
        String form = LivefyreUtil.mapToJsonString(ImmutableMap.<String, Object>of("topicIds", getTopicIds(topics)));
        
        Response response = builder(collection)
                .path(String.format(MULTIPLE_TOPIC_PATH, collection.getUrn()))
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(form));
        JsonObject content = evaluateResponse(response);
        JsonObject data = content.getAsJsonObject("data");
    
        return data.has("added") ? data.get("added").getAsInt() : 0;
    }
    
    public static Map<String, Integer> replaceCollectionTopics(Collection collection, List<Topic> topics) {
        String form = LivefyreUtil.mapToJsonString(ImmutableMap.<String, Object>of("topicIds", getTopicIds(topics)));

        Response response = builder(collection)
                .path(String.format(MULTIPLE_TOPIC_PATH, collection.getUrn()))
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .put(Entity.json(form));
        JsonObject content = evaluateResponse(response);
        JsonObject data = content.getAsJsonObject("data");
        
        Map<String, Integer> results = Maps.newHashMap();
        results.put("added", data.has("added") ? data.get("added").getAsInt() : 0);
        results.put("removed", data.has("removed") ? data.get("removed").getAsInt() : 0);
        return results;
    }
    
    public static int removeCollectionTopics(Collection collection, List<Topic> topics) {
        String form = LivefyreUtil.mapToJsonString(ImmutableMap.<String, Object>of("delete", getTopicIds(topics)));
        
        Response response = builder(collection)
                .path(String.format(MULTIPLE_TOPIC_PATH, collection.getUrn()))
                .queryParam("_method", PATCH_METHOD)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(form));
        JsonObject content = evaluateResponse(response);
        JsonObject data = content.getAsJsonObject("data");

        return data.has("removed") ? data.get("removed").getAsInt() : 0;
    }
    
    /* Subscription API */
    public static List<Subscription> getSubscriptions(Network network, String userId) {
        Response response = builder(network)
                .path(String.format(USER_SUBSCRIPTION_PATH, network.getUrnForUser(userId)))
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();
        JsonObject content = evaluateResponse(response);
        JsonArray subscriptionData = content.getAsJsonObject("data").getAsJsonArray("subscriptions");
        
        List<Subscription> subscriptions = Lists.newArrayList();
        if (subscriptionData != null) {
            for (int i = 0; i < subscriptionData.size(); i++) {
                subscriptions.add(Subscription.serializeFromJson(subscriptionData.get(i).getAsJsonObject()));
            }
        }
        return subscriptions;
    }
    
    public static int addSubscriptions(Network network, String userToken, List<Topic> topics) {
        String userId = getUserFromToken(network, userToken);
        String userUrn = network.getUrnForUser(userId);
        String form = LivefyreUtil.mapToJsonString(ImmutableMap.<String, Object>of("subscriptions", buildSubscriptions(topics, userUrn)));

        Response response = builder(network, userToken)
                .path(String.format(USER_SUBSCRIPTION_PATH, userUrn))
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(form));
        JsonObject content = evaluateResponse(response);
        JsonObject data = content.getAsJsonObject("data");

        return data.has("added") ? data.get("added").getAsInt() : 0;
    }
    
    public static Map<String, Integer> replaceSubscriptions(Network network, String userToken, List<Topic> topics) {
        String userId = getUserFromToken(network, userToken);
        String userUrn = network.getUrnForUser(userId);
        String form = LivefyreUtil.mapToJsonString(ImmutableMap.<String, Object>of("subscriptions", buildSubscriptions(topics, userUrn)));

        Response response = builder(network, userToken)
                .path(String.format(USER_SUBSCRIPTION_PATH, userUrn))
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .put(Entity.json(form));
        JsonObject content = evaluateResponse(response);
        JsonObject data = content.getAsJsonObject("data");
        
        Map<String, Integer> results = Maps.newHashMap();
        results.put("added", data.has("added") ? data.get("added").getAsInt() : 0);
        results.put("removed", data.has("removed") ? data.get("removed").getAsInt() : 0);
        return results;
    }

    public static int removeSubscriptions(Network network, String userToken, List<Topic> topics) {
        String userId = getUserFromToken(network, userToken);
        String userUrn = network.getUrnForUser(userId);
        String form = LivefyreUtil.mapToJsonString(ImmutableMap.<String, Object>of("delete", buildSubscriptions(topics, userUrn)));

        Response response = builder(network, userToken)
                .path(String.format(USER_SUBSCRIPTION_PATH, userUrn))
                .queryParam("_method", PATCH_METHOD)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(form));
        JsonObject content = evaluateResponse(response);
        JsonObject data = content.getAsJsonObject("data");

        return data.has("removed") ? data.get("removed").getAsInt() : 0;
    }
    
    public static List<Subscription> getSubscribers(Network network, Topic topic, Integer limit, Integer offset) {
        Response response = builder(network)
                .path(String.format(TOPIC_SUBSCRIPTION_PATH, topic.getId()))
                .queryParam("limit", limit == null ? "100" : limit.toString())
                .queryParam("offset", offset == null ? "0" : offset.toString())
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();
        JsonObject content = evaluateResponse(response);
        JsonArray data = content.getAsJsonObject("data").getAsJsonArray("subscriptions");
        
        List<Subscription> subscriptions = Lists.newArrayList();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                subscriptions.add(Subscription.serializeFromJson(data.get(i).getAsJsonObject()));
            }
        }
        return subscriptions;
    }
    
    /* This call is used specifically by the TimelineCursor class. */
    public static JsonObject getTimelineStream(TimelineCursor cursor, boolean isNext) {
        WebTarget r = streamBuilder(cursor.getCore())
                .path(TIMELINE_PATH)
                .queryParam("limit", cursor.getData().getLimit().toString())
                .queryParam("resource", cursor.getData().getResource());
        
        if (isNext) {
            r = r.queryParam("since", cursor.getData().getCursorTime());
        } else {
            r = r.queryParam("until", cursor.getData().getCursorTime());
        }
        
        Response response = r.request().accept(MediaType.APPLICATION_JSON).get();
        return evaluateResponse(response);
    }
    
    /* Helper methods */
    private static WebTarget builder(LfCore core) {
        return builder(core, null);
    }
    
    private static WebTarget builder(LfCore core, String userToken) {
        return client(core, userToken).target(String.format(BASE_URL, Domain.quill(core)));
    }
    
    private static WebTarget streamBuilder(LfCore core) {
        return client(core, null).target(String.format(STREAM_BASE_URL, Domain.bootstrap(core)));
    }

    private static Client client(LfCore core, String userToken) {
        Client c = ClientBuilder.newClient();
        c.property(ClientProperties.CONNECT_TIMEOUT, 1000);
        c.property(ClientProperties.READ_TIMEOUT, 10000);
        c.register(new LftokenAuthFilter(core, userToken));
        return c;
    }
    
    private static JsonObject evaluateResponse(Response response) {
        if (response.getStatus() >= 400) {
            throw new ApiException(response.getStatus());
        }
        return LivefyreUtil.stringToJson(response.readEntity(String.class));
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
            subscriptions.add(new Subscription(topic.getId(), userUrn, SubscriptionType.personalStream, null));
        }
        return subscriptions;
    }

    private static String getUserFromToken(Network network, String userToken) {
        JsonObject json = LivefyreUtil.decodeJwt(userToken, network.getData().getKey());
        return json.get("user_id").getAsString();
    }
}
