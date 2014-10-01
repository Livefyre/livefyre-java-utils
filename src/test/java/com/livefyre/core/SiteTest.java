package com.livefyre.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.google.common.collect.ImmutableMap;
import com.livefyre.Livefyre;
import com.livefyre.config.LfTest;
import com.livefyre.config.UnitTest;

public class SiteTest extends LfTest {
    @Test
    @Category(UnitTest.class)
    public void testBuildCollection() {
        Site site = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY).getSite(SITE_ID, SITE_KEY);
        try {
            site.buildCollection("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456", "", "", null);
            fail("titles longer than 255 char are not allowed");
        } catch (IllegalArgumentException e) {}
        try {
            site.buildCollection("", "", "tet.com", null);
            fail("url must start with valid url scheme (http:// or https://)");
        } catch (IllegalArgumentException e) {}
        try {
            site.buildCollection("", "", "tet.com/", null);
            fail("url must be a valid domain");
        } catch (IllegalArgumentException e) {}
        try {
            site.buildCollection("", "", "http://www.test.com", ImmutableMap.<String,Object>of("type", "abc"));
            fail("type must be of a known type");
        } catch (IllegalArgumentException e) {}

        Collection collection  = site.buildCollection(TITLE, ARTICLE_ID, URL, null);
        assertNotNull(collection);
    }
}
