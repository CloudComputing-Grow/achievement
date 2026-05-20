package com.grow.achievement.dto.response;

import com.grow.achievement.entity.enums.AchievementCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BadgeResponse {

    private Long badgeId;
    private String name;
    private AchievementCategory category;
    private LocalDateTime achievedAt;
}