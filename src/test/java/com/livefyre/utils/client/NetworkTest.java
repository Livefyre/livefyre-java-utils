package com.livefyre.utils.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class NetworkTest {
    private static final String NETWORK = "test.fyre.com";
    private static final String NETWORK_KEY = "testkeytest";
    
    @Test
    public void testNetworkCreation() {
        try {
            Livefyre.getNetwork("", null);
            fail("key cannot be null");
        } catch(NullPointerException ex) {}
        try {
            Livefyre.getNetwork(null, "");
            fail("name cannot be null");
        } catch(NullPointerException ex) {}
        Network network = Livefyre.getNetwork("", "");
        assertNotNull(network);
    }
    
    @Test
    public void testNetworkSetUserSyncId() {
        Network network = Livefyre.getNetwork(NETWORK, NETWORK_KEY);
        try {
            network.setUserSyncUrl("http://thisisa.test.url/");
            fail("network must contain {id}");
        } catch (IllegalArgumentException e) {}
    }
    
    @Test
    public void testNetworkUserToken() {
        Network network = Livefyre.getNetwork(NETWORK, NETWORK_KEY);
        String token = network.getUserAuthToken("system", "testName", 86400.0);
        
        assertNotNull(token);
        assertTrue(network.validateLivefyreToken(token));
    }
    
    @Test
    public void testNullChecks() {
        Network network = new Network();
        /* param checks */
        try {
            network.setUserSyncUrl(null);
            fail("urlTemplate cannot be null");
        } catch(NullPointerException ex) {}
        try {
            network.syncUser(null);
            fail("userId cannot be null");
        } catch(NullPointerException ex) {}
        try {
            network.getUserAuthToken(null, null, null);
            fail("userId cannot be null");
        } catch(NullPointerException ex) {}
        try {
            network.getUserAuthToken("", null, null);
            fail("displayName cannot be null");
        } catch(NullPointerException ex) {}
        try {
            network.getUserAuthToken("", "", null);
            fail("expires cannot be null");
        } catch(NullPointerException ex) {}
        try {
            network.validateLivefyreToken(null);
            fail("lfToken cannot be null");
        } catch(NullPointerException ex) {}
        /* name checks */
        try {
            network.setUserSyncUrl("");
            fail("network name cannot be null");
        } catch(NullPointerException ex) {}
        try {
            network.syncUser("");
            fail("network name cannot be null");
        } catch(NullPointerException ex) {}
        try {
            network.getUserAuthToken("", "", 0.0);
            fail("network name cannot be null");
        } catch(NullPointerException ex) {}
        /* key checks */
        network.setNetworkName("");
        try {
            network.setUserSyncUrl("");
            fail("network key cannot be null");
        } catch(NullPointerException ex) {}
        try {
            network.getUserAuthToken("", "", 0.0);
            fail("network key cannot be null");
        } catch(NullPointerException ex) {}
        try {
            network.validateLivefyreToken("");
            fail("network key cannot be null");
        } catch(NullPointerException ex) {}
    }
}
