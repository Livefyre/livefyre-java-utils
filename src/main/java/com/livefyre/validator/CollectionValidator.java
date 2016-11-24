package com.livefyre.validator;

import com.livefyre.model.CollectionData;
import com.livefyre.utils.LivefyreUtil;

public class CollectionValidator implements Validator<CollectionData> {
    public String validate(CollectionData data) {
        StringBuilder reason = new StringBuilder();
        if (!LivefyreUtil.isNotBlank(data.getArticleId())) {
            reason.append("\n Article id is null or blank.");
        }
        if (!LivefyreUtil.isNotBlank(data.getTitle())) {
            reason.append("\n Title is null or blank.");
        } else if (data.getTitle().length() > 255) {
            reason.append("\n Title is longer than 255 characters.");
        }
        if (!LivefyreUtil.isNotBlank(data.getUrl())) {
            reason.append("\n URL is null or blank.");
        } else if (!LivefyreUtil.isValidFullUrl(data.getUrl())) {
            reason.append("\n URL is not a valid url. see http://www.ietf.org/rfc/rfc2396.txt");
        }
        if (data.getType() == null) {
            reason.append("\n Type is null.");
        }
        
        if (reason.length() > 0) {
            return reason.insert(0, "Problems with your collection input:").toString();
        }
        return null;
    }
}
