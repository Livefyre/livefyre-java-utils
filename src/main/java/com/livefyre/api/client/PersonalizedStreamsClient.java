package com.livefyre.api.client;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.livefyre.api.dto.CollectionTopicDto;
import com.livefyre.api.dto.Subscription;
import com.livefyre.api.dto.Topic;

public interface PersonalizedStreamsClient {
    
    public static final String TOPIC_URL = "/topic/{topicId}/";
    public static final String MULTIPLE_TOPIC_URL = "/topics/";
    public static final String COLLECTION_TOPICS_URL = "/collection/{collectionId}/topics/";
    public static final String USER_SUBSCRIPTION_URL = "/user/{user}/subscriptions/";
    public static final String TOPIC_SUBSCRIPTION_URL = "/topic/{topicId}/subscribers/";

    /* Network and Site level OK */
    @GET
    @Path(TOPIC_URL)
    @Produces(MediaType.APPLICATION_JSON)
    Topic getTopic(@PathParam("topicId") String topicId);
    
    @GET
    @Path(MULTIPLE_TOPIC_URL)
    @Produces(MediaType.APPLICATION_JSON)
    List<Topic> getTopics(@QueryParam("limit") @DefaultValue("100") Integer limit, @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    @POST
    @Path(MULTIPLE_TOPIC_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    CollectionTopicDto postTopics(@FormParam("topics") List<Topic> topics);
    
    @DELETE
    @Path(MULTIPLE_TOPIC_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    CollectionTopicDto deleteTopics(@FormParam("topicIds") List<String> topicIds);

    /* Site->Collection level OK */
    @GET
    @Path(COLLECTION_TOPICS_URL)
    @Produces(MediaType.APPLICATION_JSON)
    CollectionTopicDto getCollectionTopics(@PathParam("collectionId") String collectionId);
    
    @POST
    @Path(COLLECTION_TOPICS_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    CollectionTopicDto postCollectionTopics(@PathParam("collectionId") String collectionId, @FormParam("topics") List<Topic> topics);
    
    @POST
    @Path(COLLECTION_TOPICS_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    CollectionTopicDto putCollectionTopics(@PathParam("collectionId") String collectionId, @FormParam("topics") List<Topic> topics);
    
    @DELETE
    @Path(COLLECTION_TOPICS_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    CollectionTopicDto deleteCollectionTopics(@PathParam("collectionId") String collectionId, @FormParam("topicIds") List<String> topicIds);
    
    /* Network level OK */
    @GET
    @Path(USER_SUBSCRIPTION_URL)
    @Produces(MediaType.APPLICATION_JSON)
    List<Subscription> getSubscriptions(@PathParam("user") String user);
    
    @POST
    @Path(USER_SUBSCRIPTION_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    CollectionTopicDto postSubscriptions(@PathParam("user") String user, @FormParam("subscriptions") List<Subscription> subscriptions);
    
    @POST
    @Path(USER_SUBSCRIPTION_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    CollectionTopicDto putSubscriptions(@PathParam("user") String user, @FormParam("subscriptions") List<Subscription> subscriptions);
    
    @DELETE
    @Path(USER_SUBSCRIPTION_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    CollectionTopicDto deleteSubscriptions(@PathParam("user") String user, @FormParam("subscriptions") List<Subscription> subscriptions);

    @GET
    @Path(TOPIC_SUBSCRIPTION_URL)
    @Produces(MediaType.APPLICATION_JSON)
    List<String> getSubscribers(@PathParam("topicId") String topicId);
    
    @POST
    @Path(TOPIC_SUBSCRIPTION_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    CollectionTopicDto postSubscribers(@PathParam("topicId") String topicId, @FormParam("objectIds") List<String> users);
    
    @POST
    @Path(TOPIC_SUBSCRIPTION_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    CollectionTopicDto putSubscribers(@PathParam("topicId") String topicId, @FormParam("objectIds") List<String> users);
    
    @DELETE
    @Path(TOPIC_SUBSCRIPTION_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    CollectionTopicDto deleteSubscribers(@PathParam("topicId") String topicId, @FormParam("objectIds") List<String> users);
 
}
