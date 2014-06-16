package com.livefyre.api.client;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.livefyre.api.dto.CollectionTopicDto;
import com.livefyre.api.dto.PostDto;
import com.livefyre.api.dto.Subscription;
import com.livefyre.api.dto.Topic;
import com.livefyre.api.dto.TopicsDto;

public interface PersonalizedStreamsClient {
    
    public static final String TOPIC_URL = "/topic/{topicId}/";
    public static final String MULTIPLE_TOPIC_URL = "/topics/";
    public static final String COLLECTION_TOPICS_URL = "/collection/{collectionId}/topics/";
    public static final String USER_SUBSCRIPTION_URL = "/user/{user}/subscriptions/";
    public static final String TOPIC_SUBSCRIPTION_URL = "/topic/{topicId}/subscribers/";

    @GET
    @Path(TOPIC_URL)
    @Produces(MediaType.APPLICATION_JSON)
    Topic getTopic(@PathParam("topicId") String topicId);
    
    @GET
    @Path(MULTIPLE_TOPIC_URL)
    @Produces(MediaType.APPLICATION_JSON)
    List<Topic> getTopics(
            @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    @POST
    @Path(MULTIPLE_TOPIC_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    TopicsDto postTopics(@FormParam("topics") List<Topic> topics);
    
    @PATCH
    @Path(MULTIPLE_TOPIC_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    TopicsDto patchTopics(@FormParam("delete") List<String> topicIds);

    @GET
    @Path(COLLECTION_TOPICS_URL)
    @Produces(MediaType.APPLICATION_JSON)
    CollectionTopicDto getCollectionTopics(@PathParam("collectionId") String collectionId);
    
    @POST
    @Path(COLLECTION_TOPICS_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    PostDto postCollectionTopics(@PathParam("collectionId") String collectionId, @FormParam("topicIds") List<String> topicIds);
    
    @PUT
    @Path(COLLECTION_TOPICS_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    PostDto putCollectionTopics(@PathParam("collectionId") String collectionId, @FormParam("topicIds") List<String> topicIds);
    
    @PATCH
    @Path(COLLECTION_TOPICS_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    PostDto patchCollectionTopics(@PathParam("collectionId") String collectionId, @FormParam("delete") List<String> topicIds);
    
    @GET
    @Path(USER_SUBSCRIPTION_URL)
    @Produces(MediaType.APPLICATION_JSON)
    List<Subscription> getSubscriptions(@PathParam("user") String user);
    
    @POST
    @Path(USER_SUBSCRIPTION_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    PostDto postSubscriptions(@PathParam("user") String user, @FormParam("subscriptions") List<Subscription> subscriptions);
    
    @PUT
    @Path(USER_SUBSCRIPTION_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    PostDto putSubscriptions(@PathParam("user") String user, @FormParam("subscriptions") List<Subscription> subscriptions);
    
    @PATCH
    @Path(USER_SUBSCRIPTION_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    PostDto patchSubscriptions(@PathParam("user") String user, @FormParam("subscriptions") List<Subscription> subscriptions);

    @GET
    @Path(TOPIC_SUBSCRIPTION_URL)
    @Produces(MediaType.APPLICATION_JSON)
    List<String> getSubscribers(@PathParam("topicId") String topicId, 
            @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
}
