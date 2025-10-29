// src/main/java/com/legitnews/dto/CreateNewsRequest.java
package com.legitnews.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateNewsRequest {
  private String category;     // e.g. "Business News"
  private String headline;
  private String details;
  private String reporter;     // free text / media name
  private String imageUrl;     // store RELATIVE path: /images/<Category>/<file>
  private String dateTime;     
  private Long createdById;      
}
