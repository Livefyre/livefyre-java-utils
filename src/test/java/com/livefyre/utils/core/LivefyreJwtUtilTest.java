package com.livefyre.utils.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.security.InvalidKeyException;
import java.security.SignatureException;

import net.oauth.jsontoken.JsonToken;

import org.junit.Test;

import com.google.gson.JsonObject;
import com.livefyre.utils.core.LivefyreJwtUtil;
import com.livefyre.utils.core.StreamType;

public class LivefyreJwtUtilTest {
    private static final String TEST_SECRET = "testkeytest";

    @Test
    public void testUserAuthToken() {
        String token = null;
        JsonToken json = null;
        try {
            token = LivefyreJwtUtil.getJwtUserAuthToken("test.fyre.com", TEST_SECRET, "some", "user", 86400);
            json = LivefyreJwtUtil.decodeJwt(TEST_SECRET, token);
        } catch (InvalidKeyException e) {
            fail("shouldn't be an issue encoding/decoding");
        } catch (SignatureException e) {
            fail("shouldn't be an issue encoding/decoding");
        }
        assertNotNull(token);
        assertNotNull(json);
        JsonObject jsonObj = json.getPayloadAsJsonObject();
        assertEquals("test.fyre.com", jsonObj.get("domain").getAsString());
        assertEquals("some", jsonObj.get("user_id").getAsString());
        assertEquals("user", jsonObj.get("display_name").getAsString());
        assertNotNull(jsonObj.get("expires"));
    }
    
    @Test
    public void testCollectionMetaToken() {
        String token = null;
        JsonToken json = null;
        try {
            token = LivefyreJwtUtil.getJwtCollectionMetaToken(TEST_SECRET, "title", "tags", "url", "id", StreamType.REVIEWS);
            json = LivefyreJwtUtil.decodeJwt(TEST_SECRET, token);
        } catch (InvalidKeyException e) {
            fail("shouldn't be an issue encoding/decoding");
        } catch (SignatureException e) {
            fail("shouldn't be an issue encoding/decoding");
        }
        assertNotNull(token);
        assertNotNull(json);
        JsonObject jsonObj = json.getPayloadAsJsonObject();
        assertEquals("title", jsonObj.get("title").getAsString());
        assertEquals("url", jsonObj.get("url").getAsString());
        assertEquals("tags", jsonObj.get("tags").getAsString());
        assertEquals("id", jsonObj.get("articleId").getAsString());
        assertEquals("reviews", jsonObj.get("type").getAsString());
    }
}
