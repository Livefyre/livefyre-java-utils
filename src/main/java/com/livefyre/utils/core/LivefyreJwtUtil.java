package com.livefyre.utils.core;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import net.oauth.jsontoken.Checker;
import net.oauth.jsontoken.JsonToken;
import net.oauth.jsontoken.JsonTokenParser;
import net.oauth.jsontoken.crypto.HmacSHA256Signer;
import net.oauth.jsontoken.crypto.HmacSHA256Verifier;
import net.oauth.jsontoken.crypto.SignatureAlgorithm;
import net.oauth.jsontoken.crypto.Verifier;
import net.oauth.jsontoken.discovery.VerifierProvider;
import net.oauth.jsontoken.discovery.VerifierProviders;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

public class LivefyreJwtUtil {

    private LivefyreJwtUtil() {}

    public static String getJwtUserAuthToken(String networkName, String networkSecret, String userId, String displayName,
            double expires) throws InvalidKeyException, SignatureException {
        HmacSHA256Signer signer = new HmacSHA256Signer(null, null, networkSecret.getBytes());
        JsonToken mToken = new JsonToken(signer);
        JsonObject tokenJSON = mToken.getPayloadAsJsonObject();
        tokenJSON.addProperty("domain", networkName);
        tokenJSON.addProperty("user_id", userId);
        tokenJSON.addProperty("display_name", displayName);
        tokenJSON.addProperty("expires", getExpiryInSeconds(expires));
        return mToken.serializeAndSign();
    }

    public static String getJwtCollectionMetaToken(String siteSecret, String title, String tags, String url,
            String articleId, StreamType stream) throws InvalidKeyException, SignatureException {
        HmacSHA256Signer signer = new HmacSHA256Signer(null, null, siteSecret.getBytes());
        JsonToken mToken = new JsonToken(signer);
        JsonObject tokenJSON = mToken.getPayloadAsJsonObject();
        tokenJSON.addProperty("title", title);
        tokenJSON.addProperty("url", url);
        tokenJSON.addProperty("tags", tags);
        tokenJSON.addProperty("articleId", articleId);
        if (stream != StreamType.NONE) {
            tokenJSON.addProperty("type", stream.toString());
        }
        return mToken.serializeAndSign();
    }

    public static JsonToken decodeJwt(String secret, String jwt) throws InvalidKeyException {
        final Verifier hmacVerifier = new HmacSHA256Verifier(secret.getBytes());
        VerifierProvider hmacLocator = new VerifierProvider() {
            public List<Verifier> findVerifier(String id, String key) {
                return Lists.newArrayList(hmacVerifier);
            }
        };
        VerifierProviders locators = new VerifierProviders();
        locators.setVerifierProvider(SignatureAlgorithm.HS256, hmacLocator);

        JsonTokenParser parser = new JsonTokenParser(locators, new Checker() {
            public void check(JsonObject payload) {}
        });
        try {
            return parser.verifyAndDeserialize(jwt);
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static long getExpiryInSeconds(double secTillExpire) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.add(Calendar.SECOND, (int) secTillExpire);
        return cal.getTimeInMillis() / 1000L;
    }
}
