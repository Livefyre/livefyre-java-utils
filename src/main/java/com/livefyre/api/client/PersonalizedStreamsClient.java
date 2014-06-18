package com.livefyre.api.client;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.livefyre.api.dto.CollectionTopicDto;
import com.livefyre.api.dto.SubscriptionDto;
import com.livefyre.api.dto.TopicDto;
import com.livefyre.api.forms.PatchTopicsForm;
import com.livefyre.api.forms.SubscriptionsForm;
import com.livefyre.api.forms.TopicIdsForm;
import com.livefyre.api.forms.TopicsForm;

public interface PersonalizedStreamsClient {
    
    public static final String TOPIC_URL = "/{topicUrn}/";
    public static final String MULTIPLE_TOPIC_URL = "/{urn}:topics/";
    public static final String COLLECTION_TOPICS_URL = "/{siteUrn}:collection={collectionId}:topics/";
    public static final String USER_SUBSCRIPTION_URL = "/{userUrn}:subscriptions/";
    public static final String TOPIC_SUBSCRIPTION_URL = "/{topicUrn}:subscribers/";
    public static final String PATCH_OVERRIDE = "PATCH";

    @GET
    @Path(TOPIC_URL)
    @Produces(MediaType.APPLICATION_JSON)
    TopicDto getTopic(
            @PathParam("topicUrn") @NotNull String topicUrn);
    
    @GET
    @Path(MULTIPLE_TOPIC_URL)
    @Produces(MediaType.APPLICATION_JSON)
    TopicDto getTopics(
            @PathParam("urn") @NotNull String urn,
            @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    @POST
    @Path(MULTIPLE_TOPIC_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    TopicDto postTopics(
            @PathParam("urn") @NotNull String urn,
            TopicsForm form);
    
    @POST
    @Path(MULTIPLE_TOPIC_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    TopicDto patchTopics(
            @PathParam("urn") @NotNull String urn,
            PatchTopicsForm form,
            @QueryParam("_method") @DefaultValue(PATCH_OVERRIDE) String method);

    @GET
    @Path(COLLECTION_TOPICS_URL)
    @Produces(MediaType.APPLICATION_JSON)
    CollectionTopicDto getCollectionTopics(
            @PathParam("siteUrn") @NotNull String siteUrn,
            @PathParam("collectionId") @NotNull String collectionId);
    
    @POST
    @Path(COLLECTION_TOPICS_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    CollectionTopicDto postCollectionTopics(
            @PathParam("siteUrn") @NotNull String siteUrn,
            @PathParam("collectionId") @NotNull String collectionId,
            TopicIdsForm form);
    
    @PUT
    @Path(COLLECTION_TOPICS_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    CollectionTopicDto putCollectionTopics(
            @PathParam("siteUrn") @NotNull String siteUrn,
            @PathParam("collectionId") @NotNull String collectionId,
            TopicIdsForm form);
    
    @POST
    @Path(COLLECTION_TOPICS_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    CollectionTopicDto patchCollectionTopics(
            @PathParam("siteUrn") @NotNull String siteUrn,
            @PathParam("collectionId") @NotNull String collectionId,
            PatchTopicsForm form,
            @QueryParam("_method") @DefaultValue(PATCH_OVERRIDE) String method);
    
    @GET
    @Path(USER_SUBSCRIPTION_URL)
    @Produces(MediaType.APPLICATION_JSON)
    SubscriptionDto getSubscriptions(
            @PathParam("userUrn") @NotNull String user);
    
    @POST
    @Path(USER_SUBSCRIPTION_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    SubscriptionDto postSubscriptions(
            @PathParam("userUrn") @NotNull String user,
            SubscriptionsForm subscriptions);
    
    @PUT
    @Path(USER_SUBSCRIPTION_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    SubscriptionDto putSubscriptions(
            @PathParam("userUrn") @NotNull String user,
            SubscriptionsForm subscriptions);
    
    @POST
    @Path(USER_SUBSCRIPTION_URL)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    SubscriptionDto patchSubscriptions(
            @PathParam("userUrn") @NotNull String user,
            SubscriptionsForm subscriptions,
            @QueryParam("_method") @DefaultValue(PATCH_OVERRIDE) String method);

    @GET
    @Path(TOPIC_SUBSCRIPTION_URL)
    @Produces(MediaType.APPLICATION_JSON)
    SubscriptionDto getSubscribers(
            @PathParam("topicUrn") @NotNull String topicUrn, 
            @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
}
