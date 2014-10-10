package com.livefyre.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.google.gson.JsonObject;
import com.livefyre.Livefyre;
import com.livefyre.config.LfTest;
import com.livefyre.config.UnitTest;
import com.livefyre.core.Network;
import com.livefyre.dto.Topic;

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
        json.addProperty("createdAt", CREATED_AT);
        json.addProperty("modifiedAt", MODIFIED_AT);
        
        Topic topic = Topic.serializeFromJson(json);
        assertEquals(ID, topic.getId());
        assertEquals(LABEL, topic.getLabel());
        assertEquals(CREATED_AT, topic.getCreatedAt());
        assertEquals(MODIFIED_AT, topic.getModifiedAt());
    }
}
