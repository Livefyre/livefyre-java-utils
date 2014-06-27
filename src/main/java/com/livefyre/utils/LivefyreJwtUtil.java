package com.livefyre.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;

import com.livefyre.repackaged.apache.commons.Base64;

public class LivefyreJwtUtil {

    private static final String ALGORITHM = "HmacSHA256";

    /* Prevent instantiation */
    private LivefyreJwtUtil() { }

    public static String encodeLivefyreJwt(String key, Map<String, Object> data) throws InvalidKeyException {
        return serializeAndSign(key, new JSONObject(data));
    }

    public static JSONObject decodeLivefyreJwt(String secret, String jwt) throws InvalidKeyException {
        try {
            final SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), ALGORITHM);
            final Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(secret_key);
            
            String[] split = jwt.split("\\.");
            
            byte[] sig = mac.doFinal(new StringBuilder(split[0]).append(".").append(split[1]).toString().getBytes());
            if (!Arrays.equals(sig, Base64.decodeBase64(split[2]))) {
                throw new InvalidKeyException("signature verification failed");
            }

            return new JSONObject(new String(Base64.decodeBase64(split[1])));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("This error should never occur: ", e);
        }
    }
    
    private static String serializeAndSign(String key, JSONObject data) throws InvalidKeyException {
        try {
            JSONObject header = new JSONObject();
            header.put("alg", "HS256");
            
            final SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), ALGORITHM);
            final Mac sha256_HMAC = Mac.getInstance(ALGORITHM);
            sha256_HMAC.init(secret_key);

            String jwtHeader = Base64.encodeBase64URLSafeString(header.toString().getBytes());
            String jwtClaims = Base64.encodeBase64URLSafeString(data.toString().getBytes());
            String jwtBase = jwtHeader.trim()+"."+jwtClaims.trim();

            String jwtSignature = Base64.encodeBase64URLSafeString(sha256_HMAC.doFinal(jwtBase.getBytes()));
            String jwtToken = jwtBase+"."+jwtSignature;
            
            return jwtToken;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("This error should never occur: ", e);
        }
    }
}
