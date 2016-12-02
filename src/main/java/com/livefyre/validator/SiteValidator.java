package com.livefyre.validator;

import com.livefyre.utils.LivefyreUtil;
import com.livefyre.model.SiteData;

public class SiteValidator implements Validator<SiteData> {
    public String validate(SiteData data) {
        StringBuilder reason = new StringBuilder();
        if (LivefyreUtil.isBlank(data.getId())) {
            reason.append("\n ID is null or blank.");
        }
        if (LivefyreUtil.isBlank(data.getKey())) {
            reason.append("\n Key is null or blank.");
        }

        if (reason.length() > 0) {
             return reason.insert(0, "Problems with your site input:").toString();
        }
        return null;
    }
}
