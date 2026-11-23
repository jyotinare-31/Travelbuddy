package com.smarttourism.smart_tourism_backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JwtUtil {

    @Value("${jwt.secret:SmartTourSuperSecretSecurityKey2026SolapurMaharashtraIndia}")
    private String secret;

    private static final long EXPIRATION_MS = 7L * 24 * 60 * 60 * 1000; // 7 days

    public String generateToken(Integer userId, String email, String fullName) {
        try {
            long now = System.currentTimeMillis() / 1000;
            long exp = now + (EXPIRATION_MS / 1000);

            String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            String safeEmail = escapeJson(email != null ? email : "");
            String safeName = escapeJson(fullName != null ? fullName : "");
            String payloadJson = "{\"sub\":\"" + userId + "\",\"userId\":" + userId + ",\"email\":\"" + safeEmail + "\",\"name\":\"" + safeName + "\",\"iat\":" + now + ",\"exp\":" + exp + "}";

            String headerEncoded = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
            String payloadEncoded = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));

            String dataToSign = headerEncoded + "." + payloadEncoded;
            String signature = sign(dataToSign, secret);

            return dataToSign + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    public Map<String, Object> validateAndExtractClaims(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return null;
            }
            if (token.startsWith("Bearer ") || token.startsWith("bearer ")) {
                token = token.substring(7).trim();
            }

            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }

            String headerEncoded = parts[0];
            String payloadEncoded = parts[1];
            String signatureProvided = parts[2];

            String dataToSign = headerEncoded + "." + payloadEncoded;
            String expectedSignature = sign(dataToSign, secret);

            if (!expectedSignature.equals(signatureProvided)) {
                return null;
            }

            byte[] payloadBytes = Base64.getUrlDecoder().decode(payloadEncoded);
            String payloadJson = new String(payloadBytes, StandardCharsets.UTF_8);

            Map<String, Object> claims = parseSimpleJson(payloadJson);

            if (claims.containsKey("exp")) {
                long exp = ((Number) claims.get("exp")).longValue();
                if (System.currentTimeMillis() / 1000 > exp) {
                    return null; // Expired
                }
            }

            return claims;
        } catch (Exception e) {
            return null;
        }
    }

    public Integer extractUserId(String token) {
        Map<String, Object> claims = validateAndExtractClaims(token);
        if (claims != null && claims.containsKey("userId")) {
            return ((Number) claims.get("userId")).intValue();
        }
        return null;
    }

    private String sign(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return base64UrlEncode(rawHmac);
    }

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private Map<String, Object> parseSimpleJson(String json) {
        Map<String, Object> map = new HashMap<>();
        if (json == null) return map;
        String trimmed = json.trim();
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }
        Pattern pattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(\"[^\"]*\"|\\d+)");
        Matcher matcher = pattern.matcher(trimmed);
        while (matcher.find()) {
            String key = matcher.group(1);
            String val = matcher.group(2);
            if (val.startsWith("\"") && val.endsWith("\"")) {
                map.put(key, val.substring(1, val.length() - 1));
            } else {
                try {
                    map.put(key, Long.parseLong(val));
                } catch (NumberFormatException nfe) {
                    map.put(key, val);
                }
            }
        }
        return map;
    }
}

