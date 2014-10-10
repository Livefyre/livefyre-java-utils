package com.livefyre.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.google.gson.JsonObject;
import com.livefyre.Livefyre;
import com.livefyre.config.LfTest;
import com.livefyre.config.UnitTest;
import com.livefyre.core.Network;

@Category(UnitTest.class)
public class TopicTest extends LfTest {
    private static final int DATE_MULTIPLIER = 1000;
    private static final String ID = "id";
    private static final String LABEL = "label";
    private static final Integer CREATED_AT = 10;
    private static final Integer MODIFIED_AT = 1000;
    
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
        assertEquals(new Date(topic.getCreatedAt() * DATE_MULTIPLIER), topic.createdAtDate());
        assertEquals(new Date(topic.getModifiedAt() * DATE_MULTIPLIER), topic.modifiedAtDate());
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
    
    @Test
    public void testGenerateUrn() {
        Network network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
        String urn = Topic.generateUrn(network, ID);
        
        assertNotNull(urn);
        assertEquals(network.getUrn()+":topic="+ID, urn);
    }
}
