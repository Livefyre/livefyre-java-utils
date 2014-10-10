package com.livefyre.validator;

import org.apache.commons.lang.StringUtils;

import com.livefyre.model.SiteData;

public class SiteValidator implements Validator<SiteData> {
    public String validate(SiteData data) {
        StringBuilder reason = new StringBuilder();
        if (StringUtils.isBlank(data.getId())) {
            reason.append("\n Site id is null or blank.");
        }
        if (StringUtils.isBlank(data.getKey())) {
            reason.append("\n Site key is null.");
        }

        if (reason.length() > 0) {
             return reason.insert(0, "Problems with your site input:").toString();
        }
        return null;
    }
}
