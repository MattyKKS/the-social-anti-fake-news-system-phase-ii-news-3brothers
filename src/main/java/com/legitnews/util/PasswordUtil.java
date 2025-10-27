package com.legitnews.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {
  private static final int ITER = 120000;
  private static final int KEYLEN = 256;

  public static String hash(String password) {
    try {
      byte[] salt = new byte[16];
      new SecureRandom().nextBytes(salt);
      byte[] dk = pbkdf2(password.toCharArray(), salt, ITER, KEYLEN);
      return "pbkdf2$" + ITER + "$" + b64(salt) + "$" + b64(dk);
    } catch (Exception e) { throw new RuntimeException(e); }
  }

  public static boolean verify(String password, String stored) {
    try {
      if (stored == null || !stored.startsWith("pbkdf2$")) return false;
      String[] parts = stored.split("\\$");
      int it = Integer.parseInt(parts[1]);
      byte[] salt = b64d(parts[2]);
      byte[] expected = b64d(parts[3]);
      byte[] test = pbkdf2(password.toCharArray(), salt, it, expected.length*8);
      int diff = 0; for (int i=0;i<test.length;i++) diff |= test[i]^expected[i];
      return diff == 0;
    } catch (Exception e) { return false; }
  }

  private static byte[] pbkdf2(char[] pwd, byte[] salt, int iter, int keyLen) throws Exception {
    PBEKeySpec spec = new PBEKeySpec(pwd, salt, iter, keyLen);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    return skf.generateSecret(spec).getEncoded();
  }
  private static String b64(byte[] b){ return Base64.getEncoder().encodeToString(b); }
  private static byte[] b64d(String s){ return Base64.getDecoder().decode(s); }
}
