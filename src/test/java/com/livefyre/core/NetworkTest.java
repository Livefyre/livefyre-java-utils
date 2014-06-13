package com.livefyre.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.livefyre.Livefyre;
import com.livefyre.core.Network;

public class NetworkTest {
    private static final String NETWORK = "test.fyre.com";
    private static final String NETWORK_KEY = "testkeytest";
    
    @Test
    public void testNetworkCreation() {
        try {
            Livefyre.getNetwork("", null);
            fail("key cannot be null");
        } catch(NullPointerException e) {}
        try {
            Livefyre.getNetwork(null, "");
            fail("name cannot be null");
        } catch(NullPointerException e) {}
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
        
        try {
            network.buildUserAuthToken("fjaowie.123", "", 1.0);
            fail("userid must be alphanumeric");
        } catch (IllegalArgumentException e) {}
        
        String token = network.buildUserAuthToken("system", "testName", 86400.0);
        
        assertNotNull(token);
        assertTrue(network.validateLivefyreToken(token));
    }
    
    @Test
    public void testNullChecks() {
        Network network = new Network("", "");
        network.setNetworkName(null);
        network.setNetworkKey(null);;
        /* param checks */
        try {
            network.setUserSyncUrl(null);
            fail("urlTemplate cannot be null");
        } catch(NullPointerException e) {}
        try {
            network.syncUser(null);
            fail("userId cannot be null");
        } catch(NullPointerException e) {}
        try {
            network.buildUserAuthToken(null, null, null);
            fail("userId cannot be null");
        } catch(NullPointerException e) {}
        try {
            network.buildUserAuthToken("", null, null);
            fail("displayName cannot be null");
        } catch(NullPointerException e) {}
        try {
            network.buildUserAuthToken("", "", null);
            fail("epires cannot be null");
        } catch(NullPointerException e) {}
        try {
            network.validateLivefyreToken(null);
            fail("lfToken cannot be null");
        } catch(NullPointerException e) {}
        /* name checks */
        try {
            network.setUserSyncUrl("{id}");
            fail("network name cannot be null");
        } catch(NullPointerException e) {}
        try {
            network.syncUser("");
            fail("network name cannot be null");
        } catch(NullPointerException e) {}
        try {
            network.buildUserAuthToken("", "", 0.0);
            fail("network name cannot be null");
        } catch(NullPointerException e) {}
        /* key checks */
        network.setNetworkName("");
        try {
            network.setUserSyncUrl("{id}");
            fail("network key cannot be null");
        } catch(NullPointerException e) {}
        try {
            network.buildUserAuthToken("", "", 0.0);
            fail("network key cannot be null");
        } catch(NullPointerException e) {}
        try {
            network.validateLivefyreToken("");
            fail("network key cannot be null");
        } catch(NullPointerException e) {}
    }
}
