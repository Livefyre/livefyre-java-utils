package com.livefyre.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.livefyre.config.UnitTest;
import com.livefyre.pojo.DataTest;

@Category(UnitTest.class)
public class CursorDataTest extends DataTest<CursorData> {
    @Test
    public void testSetDate() {
        CursorData data = new CursorData(null, null, new Date());
        Date testDate = new Date();
        
        data.setCursorTime(testDate);
        assertNotNull(data.getCursorTime());
        assertEquals(CursorData.DATE_FORMAT.format(testDate), data.getCursorTime());
    }
}
