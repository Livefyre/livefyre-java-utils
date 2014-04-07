package com.livefyre.utils.core;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;

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
            String articleId, String stream) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        HmacSHA256Signer signer = new HmacSHA256Signer(null, null, siteSecret.getBytes());
        JsonToken mToken = new JsonToken(signer);
        JsonObject tokenJSON = mToken.getPayloadAsJsonObject();
        
        tokenJSON.addProperty("url", url);
        tokenJSON.addProperty("tags", tags);
        tokenJSON.addProperty("title", title);
        
        String checksum = Hex.encodeHexString(MessageDigest.getInstance("MD5").digest(tokenJSON.toString().getBytes()));

        tokenJSON.addProperty("checksum", checksum);
        tokenJSON.addProperty("articleId", articleId);
        if (!StringUtils.isEmpty(stream)) {
            tokenJSON.addProperty("type", stream);
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
