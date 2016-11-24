package com.livefyre.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import junit.framework.AssertionFailedError;

import com.google.gson.JsonObject;
import com.livefyre.Livefyre;
import com.livefyre.config.LfTest;
import com.livefyre.config.UnitTest;
import com.livefyre.core.Collection;
import com.livefyre.core.Network;
import com.livefyre.core.Site;

public class DomainTest extends LfTest {
    private Network network;
    private Site site;
    private Collection collection;
    
    @Before
    public void setup() {
        network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
        site = network.getSite(SITE_ID, SITE_KEY);
        collection = site.buildCommentsCollection(TITLE, ARTICLE_ID, URL);
    }
    
    @Test
    @Category(UnitTest.class)
    public void testQuill() {
        String quillDomainSsl = String.format("https://%s.quill.fyre.co", network.getNetworkName());
        String domain = Domain.quill(network);
        assertEquals(quillDomainSsl, domain);
        domain = Domain.quill(site);
        assertEquals(quillDomainSsl, domain);
        domain = Domain.quill(collection);
        assertEquals(quillDomainSsl, domain);
        
        String quillDomain = String.format("http://quill.%s.fyre.co", network.getNetworkName());
        network.setSsl(false);
        domain = Domain.quill(network);
        assertEquals(quillDomain, domain);
        domain = Domain.quill(site);
        assertEquals(quillDomain, domain);
        domain = Domain.quill(collection);
        assertEquals(quillDomain, domain);
    }
    
    @Test
    @Category(UnitTest.class)
    public void testBootstrap() {
        String bootstrapDomainSsl = String.format("https://%s.bootstrap.fyre.co", network.getNetworkName());
        String bootstrapDomain = String.format("http://bootstrap.%s.fyre.co", network.getNetworkName());
        String domain = Domain.bootstrap(network);
        assertEquals(bootstrapDomainSsl, domain);
        domain = Domain.bootstrap(site);
        assertEquals(bootstrapDomainSsl, domain);
        domain = Domain.bootstrap(collection);
        assertEquals(bootstrapDomainSsl, domain);
        
        network.setSsl(false);
        domain = Domain.bootstrap(network);
        assertEquals(bootstrapDomain, domain);
        domain = Domain.bootstrap(site);
        assertEquals(bootstrapDomain, domain);
        domain = Domain.bootstrap(collection);
        assertEquals(bootstrapDomain, domain);
    }

    @Test
    @Category(UnitTest.class)
    public void testGettingCollectionData(){
        collection = collection.createOrUpdateAsSystemUser(network);
        JsonObject json = collection.getCollectionContent();
        assertNotNull(json);

    }
}
