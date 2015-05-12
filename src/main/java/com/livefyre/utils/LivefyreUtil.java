package com.livefyre.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.livefyre.core.Collection;
import com.livefyre.core.LfCore;
import com.livefyre.core.Network;
import com.livefyre.core.Site;
import com.livefyre.exceptions.TokenException;

public class LivefyreUtil {

    private LivefyreUtil() { }
    
    public static JsonObject stringToJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, JsonObject.class);
    }
    
    public static String mapToJsonString(Map<String, Object> map) {
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    public static Network getNetworkFromCore(LfCore core) {
        if (core.getClass().equals(Network.class)) {
            return (Network) core;
        } else if (core.getClass().equals(Site.class)) {
            return ((Site) core).getNetwork();
        } else {
            return ((Collection) core).getSite().getNetwork();
        }
    }

    public static boolean isValidFullUrl(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }

    public static String serializeAndSign(Map<String, Object> claims, String key) {
        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(new Gson().toJson(claims));
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
        jws.setHeader("typ", "JWT");
        jws.setKey(new HmacKey(Arrays.copyOf(key.getBytes(), 32)));

        try {
            return jws.getCompactSerialization();
        } catch (JoseException e) {
            throw new TokenException(e);
        }
    }
    
    public static JsonObject decodeJwt(String jwt, String key) {
        JwtConsumer jwtConsumer;
        try {
            jwtConsumer = new JwtConsumerBuilder()
                    .setVerificationKey(new HmacKey(Arrays.copyOf(key.getBytes(), 32)))
                    .build();
            return new Gson().fromJson(jwtConsumer.processToClaims(jwt).toJson(), JsonObject.class);
        } catch (InvalidJwtException e) {
            throw new TokenException(e);
        }
    }
}
