package com.livefyre.entity;

import static org.junit.Assert.assertNotNull;

import java.util.Calendar;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.google.gson.JsonObject;
import com.livefyre.Livefyre;
import com.livefyre.config.IntegrationTest;
import com.livefyre.config.LfTest;
import com.livefyre.core.Network;
import com.livefyre.cursor.TimelineCursor;
import com.livefyre.factory.CursorFactory;


public class TimelineCursorTest extends LfTest {
    @Test
    @Category(IntegrationTest.class)
    public void testApiCalls() {
        Network network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
        TimelineCursor ch = CursorFactory.getPersonalStreamCursor(network, USER_ID, 50, Calendar.getInstance().getTime());
        
        ch.next();
        JsonObject json = ch.previous();
        assertNotNull(json);
    }
}
