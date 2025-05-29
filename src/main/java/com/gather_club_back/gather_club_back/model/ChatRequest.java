package com.gather_club_back.gather_club_back.model;

import lombok.Data;
import java.util.List;

@Data
public class ChatRequest {
    private String name;
    private Boolean isGroup;
    private Integer themeId;
    private Integer meetupId;
    private List<Integer> participantIds;
} 