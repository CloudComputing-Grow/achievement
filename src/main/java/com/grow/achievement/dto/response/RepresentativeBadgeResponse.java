package com.grow.achievement.dto.response;

import com.grow.achievement.entity.enums.AchievementCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RepresentativeBadgeResponse {

    private Long badgeId;
    private String name;
    private AchievementCategory category;
}