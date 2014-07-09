package com.livefyre.api.entity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import com.livefyre.api.client.PersonalizedStreamsClient;
import com.livefyre.core.LfCore;
import com.livefyre.exceptions.LivefyreException;

/**
 * TimelineCursor is an entity that keeps track of positions in timelines for different resources.
 * When created via a Network object, it will keep track of timelines at the network level,
 * and likewise at the site level if created via a Site object.
 */
public class TimelineCursor {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private LfCore core;
    private String resource;
    private Date cursorTime;
    private boolean next = false;
    private boolean previous = false;
    private int limit;
    
    public TimelineCursor(LfCore core, String resource, int limit, Date startTime) {
        this.core = core;
        this.resource = resource;
        this.limit = limit;
        this.cursorTime = startTime;
    }
    
    public String next() {
        return next(limit);
    }
    
    public String next(int limit) {
        String data = PersonalizedStreamsClient.getTimelineStream(core, resource, limit, null, DATE_FORMAT.format(cursorTime));
        JSONObject cursor = new JSONObject(data).getJSONObject("meta").getJSONObject("cursor");
        
        next = cursor.getBoolean("hasNext");
        previous = !cursor.isNull("next"); 
        
        try {
            cursorTime = previous ? DATE_FORMAT.parse(cursor.getString("next")) : cursorTime;
        } catch (ParseException e) {
            throw new LivefyreException("Chronos: Date is not in the correct format.");
        }
        
        return new JSONObject(data).getJSONObject("data").toString();
    }
    
    public String previous() {
        return previous(limit);
    }
    
    public String previous(int limit) {
        String data = PersonalizedStreamsClient.getTimelineStream(core, resource, limit, DATE_FORMAT.format(cursorTime), null);
        JSONObject cursor = new JSONObject(data).getJSONObject("meta").getJSONObject("cursor");
        
        previous = cursor.getBoolean("hasPrev");
        next = !cursor.isNull("prev");
        
        try {
            cursorTime = next ? DATE_FORMAT.parse(cursor.getString("prev")) : cursorTime;
        } catch (ParseException e) {
            throw new LivefyreException("Chronos: Date is not in the correct format.");
        }
        
        return data;
    }

    /* Getters/Setters */
    public String getResource() {
        return resource;
    }
    public Date getCursorTime() {
        return cursorTime;
    }
    public void setCursorTime(Date newTime) {
        this.cursorTime = newTime;
    }
    public boolean hasPrevious() {
        return previous;
    }
    public boolean hasNext() {
        return next;
    }
    public int getLimit() {
        return limit;
    }
    public void setLimit(int limit) {
        this.limit = limit;
    }
}
