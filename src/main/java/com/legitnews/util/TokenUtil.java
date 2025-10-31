package com.legitnews.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

public class TokenUtil {
  private static final ObjectMapper M = new ObjectMapper();

  public static String issue(String secret, long userId, String email, String role, long expiresMinutes) {
    try {
      String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
      long now = Instant.now().getEpochSecond();
      long exp = now + expiresMinutes*60;
      Map<String,Object> payload = Map.of("sub", userId, "email", email, "role", role, "iat", now, "exp", exp);
      String header = b64url(headerJson.getBytes(StandardCharsets.UTF_8));
      String body   = b64url(M.writeValueAsBytes(payload));
      String sig    = sign(header + "." + body, secret);
      return header + "." + body + "." + sig;
    } catch (Exception e) { throw new RuntimeException(e); }
  }

  private static String sign(String data, String secret) throws Exception {
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
    return b64url(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
  }

  private static String b64url(byte[] b){ return Base64.getUrlEncoder().withoutPadding().encodeToString(b); }
}
