package com.livefyre.cursor;

import java.util.Date;

import com.google.gson.JsonObject;
import com.livefyre.api.PersonalizedStream;
import com.livefyre.core.LfCore;
import com.livefyre.model.CursorData;
import com.livefyre.validator.ReflectiveValidator;

/**
 * TimelineCursor is an entity that keeps track of positions in timelines for different resources.
 * When created via a Network object, it will keep track of timelines at the network level, and
 * likewise at the site level if created via a Site object.
 */
public class TimelineCursor {
    private LfCore core;
    private CursorData data;

    public TimelineCursor(LfCore core, CursorData data) {
        this.core = core;
        this.data = data;
    }
    
    public static TimelineCursor init(LfCore core, String resource, Integer limit, Date startTime) {
        CursorData data = new CursorData(resource, limit, startTime);
        return new TimelineCursor(core, ReflectiveValidator.validate(data));
    }

    /**
     * Returns the next set of events in the timeline from the cursorTime.

     * @return JSONObject
     */
    public JsonObject next() {
        JsonObject responseData = PersonalizedStream.getTimelineStream(this, true);
        JsonObject cursor = responseData.getAsJsonObject("meta").getAsJsonObject("cursor");

        data.setNext(cursor.get("hasNext").getAsBoolean());
        data.setPrevious(!cursor.get("next").isJsonNull());
        if (data.isPrevious()) {
            data.setCursorTime(cursor.get("next").getAsString());
        }
        return responseData;
    }

    /**
     * Returns the previous set of events in the timeline from the cursorTime.

     * @return JSONObject
     */
    public JsonObject previous() {
        JsonObject responseData = PersonalizedStream.getTimelineStream(this, false);
        JsonObject cursor = responseData.getAsJsonObject("meta").getAsJsonObject("cursor");

        data.setPrevious(cursor.get("hasPrev").getAsBoolean());
        data.setNext(!cursor.get("prev").isJsonNull());
        if (data.isNext()) {
            data.setCursorTime(cursor.get("prev").getAsString());
        }
        return responseData;
    }
    
    public LfCore getCore() {
        return core;
    }

    public void setCore(LfCore core) {
        this.core = core;
    }

    public CursorData getData() {
        return data;
    }

    public void setData(CursorData data) {
        this.data = data;
    }
}
