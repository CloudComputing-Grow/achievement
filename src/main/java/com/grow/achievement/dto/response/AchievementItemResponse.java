package com.grow.achievement.dto.response;

import com.grow.achievement.entity.enums.AchievementCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AchievementItemResponse {

    private Long itemId;
    private String name;
    private AchievementCategory category;
    private String imageUrl;
    private boolean isAchieved;
}