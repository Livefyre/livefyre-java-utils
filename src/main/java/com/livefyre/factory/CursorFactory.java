package com.livefyre.factory;

import java.util.Calendar;
import java.util.Date;

import com.livefyre.core.LfCore;
import com.livefyre.core.Network;
import com.livefyre.cursor.TimelineCursor;
import com.livefyre.dto.Topic;

public class CursorFactory {
    public static TimelineCursor getTopicStreamCursor(LfCore core, Topic topic) {
        return getTopicStreamCursor(core, topic, 50, Calendar.getInstance().getTime());
    }
    
    public static TimelineCursor getTopicStreamCursor(LfCore core, Topic topic, Integer limit, Date date) {
        String resource = topic.getId() + ":topicStream";
        return TimelineCursor.init(core, resource, limit, date);
    }
    
    public static TimelineCursor getPersonalStreamCursor(Network network, String user) {
        return getPersonalStreamCursor(network, user, 50, Calendar.getInstance().getTime());
    }
    
    public static TimelineCursor getPersonalStreamCursor(Network network, String userId, Integer limit, Date date) {
        String resource = network.getUrnForUser(userId) + ":personalStream";
        return TimelineCursor.init(network, resource, limit, date);
    }
}
