package com.legitnews.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.List;

@Configuration
public class CorsConfig {

  @Value("${app.cors.allowed-origins:*}")
  private List<String> allowed;

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public CorsFilter corsFilter() {
    CorsConfiguration c = new CorsConfiguration();

    // ✅ safer: use patterns instead of raw origins
    if (allowed.contains("*")) {
      c.addAllowedOriginPattern("*");
    } else {
      c.setAllowedOrigins(allowed);
    }

    c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    c.setAllowedHeaders(List.of("*"));
    c.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
    s.registerCorsConfiguration("/**", c);
    return new CorsFilter(s);
  }
}
