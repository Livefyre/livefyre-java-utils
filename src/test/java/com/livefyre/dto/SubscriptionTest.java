package com.livefyre.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
    private final static SubscriptionType TYPE = SubscriptionType.personalStream;
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
        json.addProperty("type", TYPE.toString());
        json.addProperty("createdAt", CREATED_AT);
        
        Subscription sub = Subscription.serializeFromJson(json);
        assertEquals(TO, sub.getTo());
        assertEquals(BY, sub.getBy());
        assertEquals(TYPE.toString(), sub.getType());
        assertEquals(CREATED_AT, sub.getCreatedAt());
        

        json.addProperty("type", 1);
        sub = Subscription.serializeFromJson(json);
        
        try {
            json.addProperty("type", 0);
            sub = Subscription.serializeFromJson(json);
            fail("this should throw an error");
        } catch (IllegalArgumentException e) {}
    }
}
