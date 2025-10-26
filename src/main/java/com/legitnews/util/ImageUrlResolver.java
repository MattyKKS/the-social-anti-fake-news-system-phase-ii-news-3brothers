// src/main/java/com/legitnews/util/ImageUrlResolver.java
package com.legitnews.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class ImageUrlResolver {

  // Use the Firebase REST API base (no trailing slash)
  // e.g. https://firebasestorage.googleapis.com/v0/b/legitnews-1a6f1.firebasestorage.app/o
  @Value("${app.images.firebase.apiBase}")
  private String apiBase;

  public String toFirebaseUrl(String stored) {
    if (stored == null || stored.isBlank()) return stored;
    if (stored.startsWith("http://") || stored.startsWith("https://")) return stored;

    // strip leading slash
    String path = stored.startsWith("/") ? stored.substring(1) : stored;

    // convert + back to space (your DB stored folder names with '+')
    path = path.replace('+', ' ');

    // IMPORTANT: for /o/<object> the WHOLE path must be url-encoded, including slashes -> %2F
    String encoded = java.net.URLEncoder.encode(path, StandardCharsets.UTF_8)
        .replace("+", "%20"); // spaces should be %20

    String base = apiBase.endsWith("/") ? apiBase.substring(0, apiBase.length()-1) : apiBase;
    return base + "/" + encoded + "?alt=media";
  }
}
