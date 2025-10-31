// src/main/java/com/legitnews/dto/AdminNewsDTO.java
package com.legitnews.dto;

import java.time.LocalDateTime;

public class AdminNewsDTO {
    private Long id;
    private String category;
    private String headline;
    private String details;
    private String reporter;
    private LocalDateTime dateTime;
    private String imageUrl;
    private String status;
    private int votesReal;
    private int votesFake;
    private boolean hidden;

    // Getters and Setters
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public String getCategory() { 
        return category; 
    }
    
    public void setCategory(String category) { 
        this.category = category; 
    }
    
    public String getHeadline() { 
        return headline; 
    }
    
    public void setHeadline(String headline) { 
        this.headline = headline; 
    }
    
    public String getDetails() { 
        return details; 
    }
    
    public void setDetails(String details) { 
        this.details = details; 
    }
    
    public String getReporter() { 
        return reporter; 
    }
    
    public void setReporter(String reporter) { 
        this.reporter = reporter; 
    }
    
    public LocalDateTime getDateTime() { 
        return dateTime; 
    }
    
    public void setDateTime(LocalDateTime dateTime) { 
        this.dateTime = dateTime; 
    }
    
    public String getImageUrl() { 
        return imageUrl; 
    }
    
    public void setImageUrl(String imageUrl) { 
        this.imageUrl = imageUrl; 
    }
    
    public String getStatus() { 
        return status; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }
    
    public int getVotesReal() { 
        return votesReal; 
    }
    
    public void setVotesReal(int votesReal) { 
        this.votesReal = votesReal; 
    }
    
    public int getVotesFake() { 
        return votesFake; 
    }
    
    public void setVotesFake(int votesFake) { 
        this.votesFake = votesFake; 
    }
    
    public boolean isHidden() { 
        return hidden; 
    }
    
    public void setHidden(boolean hidden) { 
        this.hidden = hidden; 
    }
}