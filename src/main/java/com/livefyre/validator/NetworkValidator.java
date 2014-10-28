package com.livefyre.validator;

import org.apache.commons.lang.StringUtils;

import com.livefyre.model.NetworkData;

public class NetworkValidator implements Validator<NetworkData> {
    public String validate(NetworkData data) {
        StringBuilder reason = new StringBuilder();
        if (StringUtils.isBlank(data.getName())) {
            reason.append("\n Name is null or blank.");
        }
        if (StringUtils.isBlank(data.getKey())) {
            reason.append("\n Key is null or blank.");
        }

        if (reason.length() > 0) {
            return reason.insert(0, "Problems with your network input:").toString();
        }
        return null;
    }
}
