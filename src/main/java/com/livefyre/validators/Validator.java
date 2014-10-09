package com.livefyre.validators;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;

import com.livefyre.model.CollectionData;
import com.livefyre.model.NetworkData;
import com.livefyre.model.SiteData;

public class Validator {
    public static NetworkData validate(NetworkData data) {
        StringBuilder reason = new StringBuilder();
        if (StringUtils.isBlank(data.getName())) {
            reason.append("\n Network name is null or blank.");
        }
        if (StringUtils.isBlank(data.getKey())) {
            reason.append("\n Network key is null or blank.");
        }
        if (!data.getName().endsWith(".fyre.co")) {
            reason.append("\n Network name should end with '.fyre.co'.");
        }

        if (reason.length() > 0) {
            throw new IllegalArgumentException(reason.insert(0, "Problems with your network input:").toString());
        }
        return data;
    }

    public static SiteData validate(SiteData data) {
        StringBuilder reason = new StringBuilder();
        if (StringUtils.isBlank(data.getId())) {
            reason.append("\n Site id is null or blank.");
        }
        if (StringUtils.isBlank(data.getKey())) {
            reason.append("\n Site key is null.");
        }

        if (reason.length() > 0) {
            throw new IllegalArgumentException(reason.insert(0, "Problems with your site input:").toString());
        }
        return data;
    }

    public static CollectionData validate(CollectionData data) {
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
        } else if (isValidFullUrl(data.getUrl())) {
            reason.append("\n URL is not a valid url. see http://www.ietf.org/rfc/rfc2396.txt");
        }
        if (data.getType() == null) {
            reason.append("\n Type is null.");
        }
        
        if (reason.length() > 0) {
            throw new IllegalArgumentException(reason.insert(0, "Problems with your collection input:").toString());
        }
        return data;
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
