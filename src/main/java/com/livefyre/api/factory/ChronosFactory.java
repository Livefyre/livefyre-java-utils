package com.livefyre.api.factory;

import java.util.Date;

import com.livefyre.api.entity.Chronos;
import com.livefyre.api.entity.Topic;
import com.livefyre.core.LfCore;
import com.livefyre.core.Network;

public class ChronosFactory {
    public static Chronos getTopicStreamChronos(LfCore core, Topic topic, Integer limit, Date date) {
        String resource = topic.getId() + ":topicStream";
        return new Chronos(core, resource, limit, date);
    }
    
    public static Chronos getPersonalStreamChronos(Network network, String user, Integer limit, Date date) {
        String resource = network.getUserUrn(user) + ":personalStream";
        return new Chronos(network, resource, limit, date);
    }
}
