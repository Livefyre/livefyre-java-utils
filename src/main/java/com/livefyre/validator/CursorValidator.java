package com.livefyre.validator;

import com.livefyre.model.CursorData;
import com.livefyre.utils.LivefyreUtil;

public class CursorValidator implements Validator<CursorData> {
    public String validate(CursorData data) {
        StringBuilder reason = new StringBuilder();
        if (LivefyreUtil.isBlank(data.getResource())) {
            reason.append("\n Resource is null or blank.");
        }
        
        if (data.getLimit() == null) {
            reason.append("\n Limit is null.");
        }
        
        if (LivefyreUtil.isBlank(data.getCursorTime())) {
            reason.append("\n Cursor time is null or blank");
        }
        
        if (reason.length() > 0) {
            return reason.insert(0, "Problems with your cursor input:").toString();
        }
        return null;
    }
}
