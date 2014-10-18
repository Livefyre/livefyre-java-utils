package com.livefyre.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.livefyre.Livefyre;
import com.livefyre.config.IntegrationTest;
import com.livefyre.config.PojoTest;
import com.livefyre.config.UnitTest;
import com.livefyre.exception.TokenException;

public class NetworkTest extends PojoTest<Network> {
    private static final String USER_SYNC_URL = "<USER-SYNC-URL {id}>";
    
    @Test
    @Category(IntegrationTest.class)
    public void testSetUserSync() {
        Network network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
        network.setUserSyncUrl(USER_SYNC_URL);
        network.syncUser(USER_ID);
    }
    
    @Test
    @Category(UnitTest.class)
    public void testNetworkCreation() {
        try {
            Livefyre.getNetwork("", null);
            fail("key cannot be null");
        } catch(IllegalArgumentException e) {}
        try {
            Livefyre.getNetwork(null, "");
            fail("name cannot be null");
        } catch(IllegalArgumentException e) {}
        try {
            Livefyre.getNetwork("", "");
            fail("name and key cannot be blank");
        } catch(IllegalArgumentException e) {}
        try {
            Livefyre.getNetwork("blah", NETWORK_KEY);
            fail("name must end in fyre.co");
        } catch(IllegalArgumentException e) {}
    }
    
    @Test
    @Category(UnitTest.class)
    public void testNetworkSetUserSyncId() {
        Network network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
        try {
            network.setUserSyncUrl("http://thisisa.test.url/");
            fail("network must contain {id}");
        } catch (IllegalArgumentException e) {}
    }
    
    @Test
    @Category(UnitTest.class)
    public void testNetworkUserToken() {
        Network network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
        
        try {
            network.buildUserAuthToken("fjaowie.123", "", 1.0);
            fail("userid must be alphanumeric");
        } catch (IllegalArgumentException e) {}
        
        String token = network.buildUserAuthToken("system", "testName", 86400.0);
        
        assertNotNull(token);
        assertTrue(network.validateLivefyreToken(token));
        
        network.getData().setKey("blah");
        try {
            network.validateLivefyreToken(token);
            fail("This should not work.");
        } catch (TokenException e) {}
    }
    
    @Test
    @Category(UnitTest.class)
    public void testGetSite() {
        Network network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
        try {
            network.getSite(SITE_ID, null);
            fail("siteKey cannot be null");
        } catch(IllegalArgumentException e) {}
        try {
            network.getSite(null, SITE_KEY);
            fail("siteId cannot be null");
        } catch(IllegalArgumentException e) {}
        Site site = network.getSite(SITE_ID, SITE_KEY);
        assertNotNull(site);
        assertEquals(SITE_ID, site.getData().getId());
        assertEquals(SITE_KEY, site.getData().getKey());
    }
    
    @Test
    @Category(UnitTest.class)
    public void testGetUrns() {
        Network network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
        String urn = network.getUrn();
        assertEquals("urn:livefyre:"+NETWORK_NAME, urn);
        
        urn = network.getUrnForUser(USER_ID);
        assertEquals("urn:livefyre:"+NETWORK_NAME+":user="+USER_ID, urn);
    }
    
    @Test
    @Category(UnitTest.class) 
    public void testNetworkName() {
        Network network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
        String networkName = network.getNetworkName();
        assertEquals(NETWORK_NAME.split("\\.")[0], networkName);
    }
    
    @Test
    @Category(UnitTest.class)
    public void testNullChecks() {
        Network network = Network.init(NETWORK_NAME, NETWORK_KEY);
        network.getData().setName(null);
        network.getData().setKey(null);;
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
        network.getData().setName("");
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
