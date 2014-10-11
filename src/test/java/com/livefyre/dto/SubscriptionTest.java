package com.livefyre.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.google.gson.JsonObject;
import com.livefyre.config.PojoTest;
import com.livefyre.config.UnitTest;
import com.livefyre.type.SubscriptionType;

@Category(UnitTest.class)
public class SubscriptionTest extends PojoTest<Subscription> {
    private final static String TO = "to";
    private final static String BY = "by";
    private final static SubscriptionType TYPE = SubscriptionType.PERSONAL_STREAM;
    private final static Integer CREATED_AT = 10;
    
    @Test
    public void testInit() {
        Subscription sub = new Subscription(TO, BY, TYPE, CREATED_AT);
        assertEquals(TO, sub.getTo());
        assertEquals(BY, sub.getBy());
        assertEquals(TYPE.toString(), sub.getType());
        assertEquals(CREATED_AT, sub.getCreatedAt());
        
        assertTrue(CREATED_AT * 1000 == sub.createdAtDate().getTime());
    }
    
    @Test
    public void testSerializedFromJson() {
        JsonObject json = new JsonObject();
        json.addProperty(TO, TO);
        json.addProperty(BY, BY);
        json.addProperty("type", TYPE.name());
        json.addProperty("createdAt", CREATED_AT);
        
        Subscription sub = Subscription.serializeFromJson(json);
        assertEquals(TO, sub.getTo());
        assertEquals(BY, sub.getBy());
        assertEquals(TYPE.name(), sub.getType());
        assertEquals(CREATED_AT, sub.getCreatedAt());
    }
}
