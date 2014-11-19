package com.livefyre.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CursorData {
    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    
    private String resource;
    private String cursorTime;
    private Boolean next = false;
    private Boolean previous = false;
    private Integer limit;
    
    public CursorData(String resource, Integer limit, Date startTime) {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.resource = resource;
        this.limit = limit;
        this.cursorTime = startTime == null ? null: DATE_FORMAT.format(startTime);
    }

    public String getResource() {
        return resource;
    }

    public CursorData setResource(String resource) {
        this.resource = resource;
        return this;
    }

    public String getCursorTime() {
        return cursorTime;
    }

    public CursorData setCursorTime(String newTime) {
        this.cursorTime = newTime;
        return this;
    }

    public CursorData setCursorTime(Date newTime) {
        this.cursorTime = DATE_FORMAT.format(newTime);
        return this;
    }

    public Boolean isPrevious() {
        return previous;
    }

    public CursorData setPrevious(Boolean previous) {
        this.previous = previous;
        return this;
    }

    public Boolean isNext() {
        return next;
    }

    public CursorData setNext(Boolean next) {
        this.next = next;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public CursorData setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }
}
