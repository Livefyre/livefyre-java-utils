package com.livefyre.utils.client;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.livefyre.utils.core.LivefyreException;
import com.livefyre.utils.core.LivefyreJwtUtil;
import com.livefyre.utils.core.TokenException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class Site {
    private static final String TOKEN_FAILURE_MSG = "Failure creating token.";

    private String networkName = null;
    private String siteId = null;
    private String siteKey = null;

    public Site(String networkName, String siteId, String siteKey) {
        this.networkName = checkNotNull(networkName);
        this.siteId = checkNotNull(siteId);
        this.siteKey = checkNotNull(siteKey);
    }

    public String buildCollectionMetaToken(String title, String articleId, String url, String tags, String stream) {
        checkArgument(checkNotNull(title).length() <= 255, "title is longer than 255 characters.");
        checkNotNull(articleId);
        checkArgument(isValidFullUrl(checkNotNull(url)),
                "url is not a valid url. see http://www.ietf.org/rfc/rfc2396.txt");
        checkNotNull(this.siteKey);

        String t = tags == null ? "" : tags;

        try {
            return LivefyreJwtUtil.getJwtCollectionMetaToken(this.siteKey, title, t, url, articleId, stream);
        } catch (InvalidKeyException e) {
            throw new TokenException(TOKEN_FAILURE_MSG + e);
        } catch (SignatureException e) {
            throw new TokenException(TOKEN_FAILURE_MSG + e);
        }
    }

    public String buildChecksum(String title, String url, String tags) {
        checkArgument(checkNotNull(title).length() <= 255, "title is longer than 255 characters.");
        checkArgument(isValidFullUrl(checkNotNull(url)),
                "url is not a valid url. see http://www.ietf.org/rfc/rfc2396.txt");

        String t = tags == null ? "" : tags;

        try {
            return LivefyreJwtUtil.getChecksum(title, url, t);
        } catch (NoSuchAlgorithmException e) {
            throw new LivefyreException("Failure creating checksum." + e);
        }
    }

    public JsonObject getCollectionContentJson(String articleId) {
        return (JsonObject) new JsonParser().parse(getCollectionContent(articleId));
    }

    public String getCollectionContent(String articleId) {
        checkNotNull(articleId);
        checkNotNull(this.siteId);
        checkNotNull(this.networkName);

        ClientResponse response = Client
                .create()
                .resource(
                        String.format("http://bootstrap.%1$s/bs3/%1$s/%2$s/%3$s/init", this.networkName, this.siteId,
                                Base64.encodeBase64URLSafeString(articleId.getBytes())))
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        if (response.getStatus() != 200) {
            throw new LivefyreException("Error contacting Livefyre. Status code: " + response.getStatus());
        }
        return response.getEntity(String.class);
    }

    public String getCollectionId(String articleId) {
        JsonObject collection = getCollectionContentJson(articleId);
        return collection.get("collectionSettings").getAsJsonObject().get("collectionId").getAsString();
    }

    protected boolean isValidFullUrl(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }

    /* Getters/Setters */
    public String getNetworkName() {
        return networkName;
    }

    protected void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getSiteId() {
        return siteId;
    }

    protected void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSiteKey() {
        return siteKey;
    }

    protected void setSiteKey(String key) {
        this.siteKey = key;
    }
}
