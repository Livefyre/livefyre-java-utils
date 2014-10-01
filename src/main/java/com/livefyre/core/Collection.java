package com.livefyre.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.livefyre.api.Domain;
import com.livefyre.entity.Topic;
import com.livefyre.exceptions.LivefyreException;
import com.livefyre.exceptions.TokenException;
import com.livefyre.repackaged.apache.commons.Base64;
import com.livefyre.utils.LivefyreJwtUtil;
import com.livefyre.utils.LivefyreUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class Collection implements LfCore {
    private static final String TOKEN_FAILURE_MSG = "Failure creating token.";
    private static final ImmutableList<String> TYPE = ImmutableList.of("reviews", "sidenotes", "ratings", "counting",
            "liveblog", "livechat", "livecomments", "");

    private Site site;
    private String collectionId;
    private String articleId;
    private String title;
    private String url;
    private boolean networkIssued;
    private Map<String, Object> options;

    public Collection(Site site, String title, String articleId, String url, Map<String, Object> options) {
        checkNotNull(articleId);
        checkArgument(checkNotNull(title).length() <= 255, "title is longer than 255 characters.");
        checkArgument(LivefyreUtil.isValidFullUrl(checkNotNull(url)),
                "url is not a valid url. see http://www.ietf.org/rfc/rfc2396.txt");
        if (options != null) {
            if (options.containsKey("type") && !TYPE.contains(options.get("type"))) {
                throw new IllegalArgumentException("type is not a recognized type. should be one of these types: " + TYPE.toString());
            }
            if (options.containsKey("topics")) {
                networkIssued = checkTopics(site.getNetwork().getUrn(), options.get("topics"));
            }
        }

        this.site = site;
        this.articleId = articleId;
        this.title = title;
        this.url = url;
        this.options = options == null ? Maps.<String, Object> newHashMap() : options;
    }

    /**
     * Informs Livefyre to either create or update a collection based on the attributes of this Collection.
     * Makes an external API call. Returns this.
     * 
     * @returns Collection
     */
    public Collection createOrUpdate() {
        ClientResponse response = invokeCollectionApi("create");
        if (response.getStatus() == 200) {
            setCollectionId(LivefyreUtil.stringToJson(response.getEntity(String.class))
                    .getAsJsonObject("data").get("collectionId").getAsString());
            return this;
        } else if (response.getStatus() == 409) {
            response = invokeCollectionApi("update");
            if (response.getStatus() == 200) {
                return this;
            }
            throw new LivefyreException("Error updating Livefyre collection. Status code: " + response.getStatus());
        }
        throw new LivefyreException("Error creating Livefyre collection. Status code: " + response.getStatus());
    }

    /**
     * Generates a collection meta token representing this collection.
     * @return String.
     */
    public String buildCollectionMetaToken() {
        try {
            Map<String, Object> json = getAttr();
            json.put("iss", networkIssued ? site.getNetwork().getUrn() : site.getUrn());
            return LivefyreJwtUtil.serializeAndSign(networkIssued ? site.getNetwork().getKey() : site.getKey(), json);
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
            Map<String, Object> attr = getAttr();
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
        String b64articleId = Base64.encodeBase64URLSafeString(articleId.getBytes());
        if (b64articleId.length() % 4 != 0) {
            b64articleId = b64articleId + StringUtils.repeat("=", 4 - (b64articleId.length() % 4));
        }
        String url = String.format("%s/bs3/%s/%s/%s/init", Domain.bootstrap(this), site.getNetwork().getName(), site.getId(), b64articleId);

        ClientResponse response = Client.create().resource(url).accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);
        if (response.getStatus() != 200) {
            throw new LivefyreException("Error contacting Livefyre. Status code: " + response.getStatus());
        }
        Gson gson = new Gson();
        return gson.fromJson(response.getEntity(String.class), JsonObject.class);
    }
    
    private Map<String, Object> getAttr() {
        Map<String, Object> attr = Maps.newTreeMap();
        attr.putAll(options);
        attr.put("articleId", articleId);
        attr.put("url", url);
        attr.put("title", title);
        return attr;
    }

    private ClientResponse invokeCollectionApi(String method) {
        String uri = String.format("%s/api/v3.0/site/%s/collection/%s/", Domain.quill(site), site.getId(), method);
        ClientResponse response = Client.create().resource(uri).queryParam("sync", "1")
                .accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, getPayload());
        return response;
    }
    
    private String getPayload() {
        Map<String, Object> payload = ImmutableMap.<String, Object>of(
            "articleId", articleId,
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

    private boolean checkTopics(String networkUrn, Object obj) {
        if (obj instanceof Iterable<?>) {
            Iterable<?> topics = (Iterable<?>) obj;
    
            for (Object topic : topics) {
                if (!(topic instanceof Topic)) {
                    return false;
                }
                String topicId = ((Topic) topic).getId();
                if (topicId.startsWith(networkUrn) && !topicId.replace(networkUrn, "").startsWith(":site=")) {
                    return true;
                }
            }
        }
        return false;
    }

    /* Getters/setters */
    public String buildLivefyreToken() {
        return site.buildLivefyreToken();
    }

    public String getUrn() {
        return String.format("%s:collection=%s", site.getUrn(), collectionId);
    }

    public Site getSite() {
        return site;
    }

    /**
     * It is preferable to use the constructor to set the Site object. 
     */
    public void setSite(Site site) {
        this.site = site;
    }

    public String getCollectionId() {
        if (collectionId == null) {
            throw new LivefyreException("Call createOrUpdate() to set the collection id.");
        }
        return collectionId;
    }
    
    /**
     * It is preferable to use createOrUpdate() to set the collectionId.
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getArticleId() {
        return articleId;
    }

    public Collection setArticleId(String articleId) {
        this.articleId = articleId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Collection setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Collection setUrl(String url) {
        this.url = url;
        return this;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public Collection setOptions(Map<String, Object> options) {
        this.options = options;
        return this;
    }

    public boolean isNetworkIssued() {
        return networkIssued;
    }

    /**
     * It is preferable to use the constructor to set networkIssued. 
     */
    public void setNetworkIssued(boolean networkIssued) {
        this.networkIssued = networkIssued;
    }
}
