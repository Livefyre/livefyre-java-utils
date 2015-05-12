package com.livefyre.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.livefyre.config.LfTest;
import com.livefyre.config.UnitTest;

public class LivefyreUtilTest extends LfTest {
    private static final String JSON_STRING = "{\"test\":\"super\"}";

    @Test
    @Category(UnitTest.class)
    public void testStringToJson() {
        JsonObject jsonObj = LivefyreUtil.stringToJson(JSON_STRING);
        assertEquals("super", jsonObj.get("test").getAsString());
    }
    
    @Test
    @Category(UnitTest.class)
    public void testMapToJsonString() {
        Map<String, Object> map = ImmutableMap.<String, Object>of("test", "super");
        
        String json = LivefyreUtil.mapToJsonString(map);
        assertEquals(JSON_STRING, json);
    }
    
    @Test
    @Category(UnitTest.class)
    public void testCollectionUrlChecker() {
        assertFalse(LivefyreUtil.isValidFullUrl("test.com"));
        assertTrue(LivefyreUtil.isValidFullUrl("http://localhost:8000"));
        assertTrue(LivefyreUtil.isValidFullUrl("http://清华大学.cn"));
        assertTrue(LivefyreUtil.isValidFullUrl("http://www.mysite.com/myresumé.html"));
        assertTrue(LivefyreUtil.isValidFullUrl("https://test.com/"));
        assertTrue(LivefyreUtil.isValidFullUrl("http://test.com/"));
        assertTrue(LivefyreUtil.isValidFullUrl("https://test.com/path/test.-_~!$&'()*+,;=:@/dash"));
    }
    
    @Test
    public void testJwtEncodeDecode() {
        String token = null;
        JsonObject json = null;
        
        Map<String, Object> data = ImmutableMap.<String,Object>of(
            "domain", "test.fyre.com",
            "user_id", "user",
            "display_name", "superuser",
            "expires", 86400
        );
        
        token = LivefyreUtil.serializeAndSign(data, NETWORK_KEY);
        json = LivefyreUtil.decodeJwt(token, NETWORK_KEY);
        
        assertNotNull(token);
        assertNotNull(json);
        assertEquals("test.fyre.com", json.get("domain").getAsString());
        assertEquals("user", json.get("user_id").getAsString());
        assertEquals("superuser", json.get("display_name").getAsString());
        assertEquals(86400, json.get("expires").getAsInt());
    }
}
