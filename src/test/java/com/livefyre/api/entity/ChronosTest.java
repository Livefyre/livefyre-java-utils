package com.livefyre.api.entity;

import static org.junit.Assert.assertNotNull;

import java.util.Calendar;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.livefyre.Livefyre;
import com.livefyre.api.factory.ChronosFactory;
import com.livefyre.config.LfTest;
import com.livefyre.core.Network;

@Ignore
public class ChronosTest extends LfTest {
    private Network network;
    
    @Before
    public void setup() {
        network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
    }
    
    @Test
    public void testEntity() {
        Chronos ch = ChronosFactory.getPersonalStreamChronos(network, USER, 50, Calendar.getInstance().getTime());
        
        JSONObject json = new JSONObject(ch.previous());
        assertNotNull(json);
    }
}
