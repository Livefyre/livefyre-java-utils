package com.livefyre.validator;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;

import com.livefyre.model.CollectionData;

public class CollectionValidator implements Validator<CollectionData> {
    public String validate(CollectionData data) {
        StringBuilder reason = new StringBuilder();
        if (StringUtils.isBlank(data.getArticleId())) {
            reason.append("\n Article id is null or blank.");
        }
        if (StringUtils.isBlank(data.getTitle())) {
            reason.append("\n Title is null or blank.");
        } else if (data.getTitle().length() > 255) {
            reason.append("\n Title is longer than 255 characters.");
        }
        if (StringUtils.isBlank(data.getUrl())) {
            reason.append("\n URL is null or blank.");
        } else if (!isValidFullUrl(data.getUrl())) {
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

    private static boolean isValidFullUrl(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }
}
