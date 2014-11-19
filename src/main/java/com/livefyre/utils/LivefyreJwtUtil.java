package com.livefyre.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.livefyre.repackaged.apache.commons.Base64;
import com.livefyre.repackaged.apache.commons.StringUtils;

public class LivefyreJwtUtil {

    private static final String ALGORITHM = "HmacSHA256";

    /* Prevent instantiation */
    private LivefyreJwtUtil() { }
    
    public static JsonObject decodeLivefyreJwt(String secret, String jwt) throws InvalidKeyException {
        try {
            final SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), ALGORITHM);
            final Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(secret_key);
            
            String[] split = jwt.split("\\.");
            
            byte[] sig = mac.doFinal(new StringBuilder(split[0]).append(".").append(split[1]).toString().getBytes());
            if (!Arrays.equals(sig, Base64.decodeBase64(split[2]))) {
                throw new InvalidKeyException("signature verification failed");
            }
            Gson gson = new Gson();
            return gson.fromJson(new String(Base64.decodeBase64(split[1])), JsonObject.class);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("This error should never occur: ", e);
        }
    }
    
    public static String serializeAndSign(String key, Map<String, Object> data) throws InvalidKeyException {
        try {
            Map<String, Object> header = ImmutableMap.<String, Object>of("alg", "HS256", "typ", "JWT");
            
            final SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), ALGORITHM);
            final Mac sha256_HMAC = Mac.getInstance(ALGORITHM);
            sha256_HMAC.init(secret_key);

            Gson gson = new Gson();
            String jwtHeader = Base64.encodeBase64URLSafeString(StringUtils.getBytesUtf8(gson.toJson(header)));
            String jwtClaims = Base64.encodeBase64URLSafeString(StringUtils.getBytesUtf8(gson.toJson(data)));
            String jwtBase = jwtHeader+"."+jwtClaims;

            String jwtSignature = Base64.encodeBase64URLSafeString(sha256_HMAC.doFinal(StringUtils.getBytesUsAscii((jwtBase))));
            String jwtToken = jwtBase+"."+jwtSignature;
            
            return jwtToken;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("This error should never occur: ", e);
        }
    }
}
