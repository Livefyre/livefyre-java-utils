package com.livefyre.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.security.InvalidKeyException;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class LivefyreJwtUtilTest {
    private static final String TEST_SECRET = "testkeytest";

    @Test
    public void testJwtEncodeDecode() {
        String token = null;
        JSONObject json = null;
        
        Map<String, Object> data = ImmutableMap.<String,Object>of(
            "domain", "test.fyre.com",
            "user_id", "user",
            "display_name", "superuser",
            "expires", 86400
        );
        
        try {
            token = LivefyreJwtUtil.encodeLivefyreJwt(TEST_SECRET, data);
            json = LivefyreJwtUtil.decodeLivefyreJwt(TEST_SECRET, token);
        } catch (InvalidKeyException e) {
            fail("shouldn't be an issue encoding/decoding");
        }
        assertNotNull(token);
        assertNotNull(json);
        assertEquals("test.fyre.com", json.getString("domain"));
        assertEquals("user", json.getString("user_id"));
        assertEquals("superuser", json.getString("display_name"));
        assertEquals(86400, json.getInt("expires"));
    }
}