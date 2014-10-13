package com.livefyre.cursor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.google.gson.JsonObject;
import com.livefyre.Livefyre;
import com.livefyre.config.IntegrationTest;
import com.livefyre.config.PojoTest;
import com.livefyre.config.UnitTest;
import com.livefyre.core.Network;
import com.livefyre.factory.CursorFactory;
import com.livefyre.model.CursorData;
import com.livefyre.validator.CursorValidator;
import com.livefyre.validator.Validator;

public class TimelineCursorTest extends PojoTest<TimelineCursor> {
    @Test
    @Category(IntegrationTest.class)
    @Ignore
    public void testApiCalls() {
        Network network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
        TimelineCursor ch = CursorFactory.getPersonalStreamCursor(network, USER_ID, 50, Calendar.getInstance().getTime());
        
        ch.next();
        JsonObject json = ch.previous();
        assertNotNull(json);
    }
    
    @Test
    @Category(UnitTest.class)
    public void testInit() {
        Network network = Livefyre.getNetwork(NETWORK_NAME, NETWORK_KEY);
        try {
            TimelineCursor.init(network, null, null, null);
            fail("resource cannot be null");
        } catch (IllegalArgumentException e) {}
        
        Validator<CursorData> v = new CursorValidator();
        try {
            new CursorData(null, null, null);
            fail("date can never be null in this instance");
        } catch (NullPointerException e) {}
        
        CursorData data = new CursorData(null, null, new Date());
        try {
            data.setCursorTime("");
            v.validate(data);
            fail("none of these fields should be null");
        }
        catch (IllegalArgumentException e) {}
    }
}
