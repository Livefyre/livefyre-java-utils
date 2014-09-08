package com.livefyre.entity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONObject;

import com.livefyre.api.client.PersonalizedStream;
import com.livefyre.core.LfCore;

/**
 * TimelineCursor is an entity that keeps track of positions in timelines for different resources.
 * When created via a Network object, it will keep track of timelines at the network level, and
 * likewise at the site level if created via a Site object.
 */
public class TimelineCursor {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private LfCore core;
    private String resource;
    private String cursorTime;
    private boolean next = false;
    private boolean previous = false;
    private int limit;

    public TimelineCursor() { }

    public TimelineCursor(LfCore core, String resource, int limit, Date startTime) {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.core = core;
        this.resource = resource;
        this.limit = limit;
        this.cursorTime = DATE_FORMAT.format(startTime);
    }

    public JSONObject next() {
        return next(limit);
    }
    
    /**
     * Returns the next set of events in the timeline from the cursorTime.

     * @return JSONObject
     */
    public JSONObject next(int limit) {
        JSONObject data = PersonalizedStream.getTimelineStream(core, resource, limit, null, cursorTime);
        JSONObject cursor = data.getJSONObject("meta").getJSONObject("cursor");

        next = cursor.getBoolean("hasNext");
        previous = !cursor.isNull("next");
        cursorTime = previous ? cursor.getString("next") : cursorTime;

        return data;
    }

    public JSONObject previous() {
        return previous(limit);
    }

    /**
     * Returns the previous set of events in the timeline from the cursorTime.

     * @return JSONObject
     */
    public JSONObject previous(int limit) {
        JSONObject data = PersonalizedStream.getTimelineStream(core, resource, limit, cursorTime, null);
        JSONObject cursor = data.getJSONObject("meta").getJSONObject("cursor");

        previous = cursor.getBoolean("hasPrev");
        next = !cursor.isNull("prev");
        cursorTime = next ? cursor.getString("prev") : cursorTime;

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

    /**
     * It is preferable to use the constructor to set the resource.
     */
    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getCursorTime() {
        return cursorTime;
    }

    public void setCursorTime(String newTime) {
        this.cursorTime = newTime;
    }

    public void setCursorTime(Date newTime) {
        this.cursorTime = DATE_FORMAT.format(newTime);
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
