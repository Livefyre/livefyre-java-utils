package com.livefyre.api.entity;

import static org.junit.Assert.assertNotNull;

import java.util.Calendar;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.livefyre.Livefyre;
import com.livefyre.config.LfTest;
import com.livefyre.core.Network;
import com.livefyre.entity.TimelineCursor;
import com.livefyre.factory.CursorFactory;

@Ignore
public class TimelineCursorTest extends LfTest {
    private Network network;
    
    @Before
    public void setup() {
        network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
    }
    
    @Test
    public void testEntity() {
        TimelineCursor ch = CursorFactory.getPersonalStreamCursor(network, USER, 50, Calendar.getInstance().getTime());
        
        ch.next();
        JSONObject json = new JSONObject(ch.previous());
        assertNotNull(json);
    }
}
