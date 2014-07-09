package com.livefyre.api.factory;

import java.util.Date;

import com.livefyre.api.entity.TimelineCursor;
import com.livefyre.api.entity.Topic;
import com.livefyre.core.LfCore;
import com.livefyre.core.Network;

public class CursorFactory {
    public static TimelineCursor getTopicStreamCursor(LfCore core, Topic topic, Integer limit, Date date) {
        String resource = topic.getId() + ":topicStream";
        return new TimelineCursor(core, resource, limit, date);
    }
    
    public static TimelineCursor getPersonalStreamCursor(Network network, String user, Integer limit, Date date) {
        String resource = network.getUserUrn(user) + ":personalStream";
        return new TimelineCursor(network, resource, limit, date);
    }
}
