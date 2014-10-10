package com.livefyre.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.google.common.collect.Lists;
import com.livefyre.config.UnitTest;
import com.livefyre.dto.Topic;
import com.livefyre.pojo.DataTest;
import com.livefyre.type.CollectionType;

@Category(UnitTest.class)
public class CollectionDataTest extends DataTest<CollectionData> {
    @Test
    public void testAsMap() {
        CollectionData data = new CollectionData(CollectionType.LIVECOMMENTS, TITLE, ARTICLE_ID, URL);
        Map<String, Object> map = data.asMap();
        
        assertNotNull(map);
        assertEquals(TITLE, map.get("title"));
        assertEquals(ARTICLE_ID, map.get("articleId"));
        assertEquals(URL, map.get("url"));
        assertEquals(CollectionType.LIVECOMMENTS.toString(), map.get("type"));
        
        String tags = "TAGS";
        String extensions = "EXTENSIONS";
        List<Topic> topics = Lists.newArrayList();
        topics.add(new Topic());
        data.setTags(tags);
        data.setExtensions(extensions);
        data.setTopics(topics);
        
        map = data.asMap();
        assertNotNull(map);
        assertEquals(TITLE, map.get("title"));
        assertEquals(ARTICLE_ID, map.get("articleId"));
        assertEquals(URL, map.get("url"));
        assertEquals(CollectionType.LIVECOMMENTS.toString(), map.get("type"));
        assertEquals(tags, map.get("tags"));
        assertEquals(extensions, map.get("extensions"));
        assertEquals(topics, map.get("topics"));
    }
}
