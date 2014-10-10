package com.livefyre.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CursorData {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    
    private String resource;
    private String cursorTime;
    private boolean next = false;
    private boolean previous = false;
    private Integer limit;
    
    public CursorData(String resource, Integer limit, Date startTime) {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.resource = resource;
        this.limit = limit;
        this.cursorTime = DATE_FORMAT.format(startTime);
    }

    public String getResource() {
        return resource;
    }

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

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
