package com.minimarket.security.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/** Genera y valida tokens JWT firmados para usuarios autenticados */
@Component
public class JwtUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    @Value("${app.jwt.secret:minimarket-plus-secreto-desarrollo}")
    private String secret;

    @Value("${app.jwt.expiration-seconds:3600}")
    private long expirationSeconds;

    public String generateToken(UserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        long now = Instant.now().getEpochSecond();

        return createToken(Map.of("alg", "HS256", "typ", "JWT"), Map.of(
                "sub", userDetails.getUsername(),
                "roles", roles,
                "iat", now,
                "exp", now + expirationSeconds
        ));
    }

    public String extractUsername(String token) {
        return String.valueOf(readPayload(token).get("sub"));
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        Map<String, Object> payload = readPayload(token);
        Number expiration = (Number) payload.get("exp");

        return userDetails.getUsername().equals(payload.get("sub"))
                && expiration != null
                && expiration.longValue() > Instant.now().getEpochSecond()
                && verifySignature(token);
    }

    private String createToken(Map<String, Object> header, Map<String, Object> payload) {
        try {
            String encodedHeader = encode(OBJECT_MAPPER.writeValueAsBytes(header));
            String encodedPayload = encode(OBJECT_MAPPER.writeValueAsBytes(payload));
            String signature = sign(encodedHeader + "." + encodedPayload);

            return encodedHeader + "." + encodedPayload + "." + signature;
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo generar el token JWT", ex);
        }
    }

    private Map<String, Object> readPayload(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3 || !verifySignature(token)) {
                throw new IllegalArgumentException("Token JWT invalido");
            }

            byte[] payload = Base64.getUrlDecoder().decode(parts[1]);
            return OBJECT_MAPPER.readValue(payload, new TypeReference<>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException("Token JWT invalido", ex);
        }
    }

    private boolean verifySignature(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return false;
        }

        return sign(parts[0] + "." + parts[1]).equals(parts[2]);
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return encode(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo firmar el token JWT", ex);
        }
    }

    private String encode(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }
}
