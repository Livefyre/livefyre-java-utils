package com.livefyre.validator;

import com.livefyre.model.NetworkData;
import com.livefyre.utils.LivefyreUtil;

public class NetworkValidator implements Validator<NetworkData> {
    public String validate(NetworkData data) {
        StringBuilder reason = new StringBuilder();
        if (!LivefyreUtil.isNotBlank(data.getName())) {
            reason.append("\n Name is null or blank.");
        } else if (!data.getName().endsWith(".fyre.co")) {
            reason.append("\n Network name must end with '.fyre.co'.");
        }
        if (!LivefyreUtil.isNotBlank(data.getKey())) {
            reason.append("\n Key is null or blank.");
        }

        if (reason.length() > 0) {
            return reason.insert(0, "Problems with Network data, if testing please ensure /livefyre-java-utils/src/test/java/com/livefyre/config/LfTest.java is populated. Values found:").toString();
        }
        return null;
    }
}
