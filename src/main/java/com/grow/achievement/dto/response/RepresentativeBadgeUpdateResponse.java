package com.grow.achievement.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RepresentativeBadgeUpdateResponse {

    private Long badgeId;
    private String message;
}