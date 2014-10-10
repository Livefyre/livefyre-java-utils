package com.livefyre.core;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.livefyre.api.Domain;
import com.livefyre.dto.Topic;
import com.livefyre.exception.LivefyreException;
import com.livefyre.exception.TokenException;
import com.livefyre.model.CollectionData;
import com.livefyre.repackaged.apache.commons.Base64;
import com.livefyre.type.CollectionType;
import com.livefyre.utils.LivefyreJwtUtil;
import com.livefyre.utils.LivefyreUtil;
import com.livefyre.validator.ReflectiveValidator;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class Collection implements LfCore {
    private static final String TOKEN_FAILURE_MSG = "Failure creating token.";

    private Site site;
    private CollectionData data;
    
    public Collection(Site site, CollectionData data) {
        this.site = site;
        this.data = data;
    }
    
    public static Collection init(Site site, CollectionType type, String title, String articleId, String url) {
        CollectionData data = new CollectionData(type, title, articleId, url);
        return new Collection(site, ReflectiveValidator.validate(data));
    }
    
    /**
     * Informs Livefyre to either create or update a collection based on the attributes of this Collection.
     * Makes an external API call. Returns this.
     * 
     * @return Collection
     */
    public Collection createOrUpdate() {
        ClientResponse response = invokeCollectionApi("create");
        if (response.getStatus() == 200) {
            data.setCollectionId(LivefyreUtil.stringToJson(response.getEntity(String.class))
                    .getAsJsonObject("data").get("collectionId").getAsString());
            return this;
        } else if (response.getStatus() == 409) {
            response = invokeCollectionApi("update");
            if (response.getStatus() == 200) {
                return this;
            }
            throw new LivefyreException(String.format("Error updating Livefyre collection. Status code: %s \n Reason: %s", response.getStatus(), response.getEntity(String.class)));
        }
        throw new LivefyreException(String.format("Error creating Livefyre collection. Status code: %s \n Reason: %s", response.getStatus(), response.getEntity(String.class)));
    }

    /**
     * Generates a collection meta token representing this collection.
     * @return String.
     */
    public String buildCollectionMetaToken() {
        try {
            Map<String, Object> json = data.asMap();
            boolean isNetworkIssued = isNetworkIssued();
            json.put("iss", isNetworkIssued ?
                    site.getNetwork().getUrn() : site.getUrn());
            return LivefyreJwtUtil.serializeAndSign(isNetworkIssued ?
                    site.getNetwork().getData().getKey() : site.getData().getKey(), json);
        } catch (InvalidKeyException e) {
            throw new TokenException(TOKEN_FAILURE_MSG + e);
        }
    }

    /**
     * Generates a MD5-encrypted checksum based on this collection's attributes.
     * 
     * @return String.
     */
    public String buildChecksum() {
        try {
            Map<String, Object> attr = data.asMap();
            byte[] digest = MessageDigest.getInstance("MD5").digest(LivefyreUtil.mapToJsonString(attr).getBytes());
            return printHexBinary(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new LivefyreException("Failure creating checksum." + e);
        }
    }

    /**
     * Retrieves this collection's information from Livefyre. Makes an external API call.
     * 
     * @return JSONObject.
     */
    public JsonObject getCollectionContent() {
        String b64articleId = Base64.encodeBase64URLSafeString(data.getArticleId().getBytes());
        if (b64articleId.length() % 4 != 0) {
            b64articleId = b64articleId + StringUtils.repeat("=", 4 - (b64articleId.length() % 4));
        }
        String url = String.format("%s/bs3/%s.fyre.co/%s/%s/init", Domain.bootstrap(this), site.getNetwork().getNetworkName(), site.getData().getId(), b64articleId);

        ClientResponse response = Client.create().resource(url).accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);
        if (response.getStatus() != 200) {
            throw new LivefyreException(String.format("Error contacting Livefyre. Status code: %s \n Reason: %s", response.getStatus(), response.getEntity(String.class)));
        }
        Gson gson = new Gson();
        return gson.fromJson(response.getEntity(String.class), JsonObject.class);
    }

    private ClientResponse invokeCollectionApi(String method) {
        String uri = String.format("%s/api/v3.0/site/%s/collection/%s/", Domain.quill(this), site.getData().getId(), method);
        ClientResponse response = Client.create().resource(uri).queryParam("sync", "1")
                .accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, getPayload());
        return response;
    }
    
    private String getPayload() {
        Map<String, Object> payload = ImmutableMap.<String, Object>of(
            "articleId", data.getArticleId(),
            "checksum", buildChecksum(),
            "collectionMeta", buildCollectionMetaToken());
        return LivefyreUtil.mapToJsonString(payload);
    }

    private static final char[] hexCode = "0123456789abcdef".toCharArray();

    private String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

    public String getUrn() {
        return String.format("%s:collection=%s", site.getUrn(), data.getCollectionId());
    }
    
    public boolean isNetworkIssued() {
        List<Topic> topics = data.getTopics();
        if (topics == null || topics.isEmpty()) {
            return false;
        }

        for (Topic topic : topics) {
            String topicId = topic.getId();
            String networkUrn = site.getNetwork().getUrn();
            if (topicId.startsWith(networkUrn) && !topicId.replace(networkUrn, "").startsWith(":site=")) {
                return true;
            }
        }
        return false;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public CollectionData getData() {
        return data;
    }
    
    public void setData(CollectionData data) {
        this.data = data;
    }
}
