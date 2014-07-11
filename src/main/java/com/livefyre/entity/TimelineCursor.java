package com.livefyre.entity;

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
    
    public TimelineCursor() {}
    
    public TimelineCursor(LfCore core, String resource, int limit, Date startTime) {
        this.core = core;
        this.resource = resource;
        this.limit = limit;
        this.cursorTime = startTime;
    }
    
    public JSONObject next() {
        return next(limit);
    }
    
    public JSONObject next(int limit) {
        JSONObject data = PersonalizedStreamsClient.getTimelineStream(core, resource, limit, null, DATE_FORMAT.format(cursorTime));
        JSONObject cursor = data.getJSONObject("meta").getJSONObject("cursor");
        
        next = cursor.getBoolean("hasNext");
        previous = !cursor.isNull("next"); 
        
        try {
            cursorTime = previous ? DATE_FORMAT.parse(cursor.getString("next")) : cursorTime;
        } catch (ParseException e) {
            throw new LivefyreException("Chronos: Date is not in the correct format.");
        }
        
        return data;
    }
    
    public JSONObject previous() {
        return previous(limit);
    }
    
    public JSONObject previous(int limit) {
        JSONObject data = PersonalizedStreamsClient.getTimelineStream(core, resource, limit, DATE_FORMAT.format(cursorTime), null);
        JSONObject cursor = data.getJSONObject("meta").getJSONObject("cursor");
        
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
    public LfCore getCore() {
        return core;
    }
    public void setCore(LfCore core) {
        this.core = core;
    }
    public String getResource() {
        return resource;
    }
    /** This method is only here for serialization. Replacing the current resource with a different one will invalidate this cursor. */
    public void setResource(String resource) {
        this.resource = resource;
    }
    public Date getCursorTime() {
        return cursorTime;
    }
    public void setCursorTime(Date newTime) {
        this.cursorTime = newTime;
    }
    public boolean hasPrevious() {
        return isPrevious();
    }
    public boolean isPrevious() {
        return previous;
    }
    public void setPrevious(boolean previous) {
        this.previous = previous;
    }
    public boolean hasNext() {
        return isNext();
    }
    public boolean isNext() {
        return next;
    }
    public void setNext(boolean next) {
        this.next = next;
    }
    public int getLimit() {
        return limit;
    }
    public void setLimit(int limit) {
        this.limit = limit;
    }
}