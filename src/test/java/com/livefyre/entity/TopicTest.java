package com.livefyre.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.google.gson.JsonObject;
import com.livefyre.Livefyre;
import com.livefyre.config.LfTest;
import com.livefyre.config.UnitTest;
import com.livefyre.core.Network;

@Category(UnitTest.class)
public class TopicTest extends LfTest {
    private static String ID = "id";
    private static String LABEL = "label";
    private static Integer CREATED_AT = 10;
    private static Integer MODIFIED_AT = 1000;
    
    @Test
    public void testInit() {
        Topic topic = new Topic(ID, LABEL, CREATED_AT, MODIFIED_AT);
        assertEquals(ID, topic.getId());
        assertEquals(LABEL, topic.getLabel());
        assertEquals(CREATED_AT, topic.getCreatedAt());
        assertEquals(MODIFIED_AT, topic.getModifiedAt());
    }
    
    @Test
    public void testCreate() {
        Network network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
        Topic topic = Topic.create(network, ID, LABEL);
        assertEquals(network.getUrn()+":topic="+ID, topic.getId());
        assertEquals(LABEL, topic.getLabel());
        assertEquals(ID, topic.truncatedId());
    }
    
    @Test
    public void testSerializeFromJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", ID);
        json.addProperty("label", LABEL);
        json.addProperty("createdAt", 100);
        json.addProperty("modifiedAt", 1000);
        
        Topic topic = Topic.serializeFromJson(json);
        assertEquals(ID, topic.getId());
        assertEquals(LABEL, topic.getLabel());
        assertEquals(100, topic.getCreatedAt().intValue());
        assertEquals(1000, topic.getModifiedAt().intValue());
    }
}
