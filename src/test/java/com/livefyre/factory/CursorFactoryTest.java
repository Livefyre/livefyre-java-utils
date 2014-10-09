package com.livefyre.factory;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.livefyre.Livefyre;
import com.livefyre.config.LfTest;
import com.livefyre.config.UnitTest;
import com.livefyre.core.Network;
import com.livefyre.entity.TimelineCursor;
import com.livefyre.entity.Topic;

public class CursorFactoryTest extends LfTest {
    private static final int LIMIT = 10;
    
    @Test
    @Category(UnitTest.class)
    public void testPersonalStreamCursor() {
        Network network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
        Date date = Calendar.getInstance().getTime();
        String psResource = String.format("urn:livefyre:%s.fyre.co:user=%s:personalStream", network.getNetworkName(), USER_ID);
        
        TimelineCursor cursor = CursorFactory.getPersonalStreamCursor(network, USER_ID);
        assertEquals(psResource, cursor.getResource());
        
        cursor = CursorFactory.getPersonalStreamCursor(network, USER_ID, LIMIT, date);
        assertEquals(psResource, cursor.getResource());
        assertEquals(LIMIT, cursor.getLimit());
    }
        
    @Test
    @Category(UnitTest.class)
    public void testTopicStreamCursor() {
        Network network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
        Date date = Calendar.getInstance().getTime();
        String topicId = "topic";
        String label = "label";
        String tsResource = String.format("urn:livefyre:%s.fyre.co:topic=%s:topicStream", network.getNetworkName(), topicId);
        
        Topic topic = Topic.create(network, topicId, label);
        TimelineCursor cursor = CursorFactory.getTopicStreamCursor(network, topic);
        assertEquals(tsResource, cursor.getResource());
        
        cursor = CursorFactory.getTopicStreamCursor(network, topic, LIMIT, date);
        assertEquals(tsResource, cursor.getResource());
        assertEquals(LIMIT, cursor.getLimit());
    }
}
