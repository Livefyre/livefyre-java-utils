package com.livefyre.validator;

import org.apache.commons.lang.StringUtils;

import com.livefyre.model.CursorData;

public class CursorValidator implements Validator<CursorData> {
    public String validate(CursorData data) {
        StringBuilder reason = new StringBuilder();
        if (StringUtils.isBlank(data.getResource())) {
            reason.append("\n Resource is null or blank.");
        }
        //these two should never ever happen.
        if (data.getLimit() == null) {
            reason.append("\n Limit is null.");
        }
        if (StringUtils.isBlank(data.getCursorTime())) {
            reason.append("\n Time is null or blank.");
        }
        
        if (reason.length() > 0) {
            return reason.insert(0, "Problems with your collection input:").toString();
        }
        return null;
    }
}
