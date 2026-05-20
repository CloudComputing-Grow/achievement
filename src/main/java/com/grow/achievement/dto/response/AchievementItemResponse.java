package com.grow.achievement.dto.response;

import com.grow.achievement.entity.enums.AchievementCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AchievementItemResponse {

    private Long itemId;

    private String name;

    private String fruitName;

    private AchievementCategory category;

    private Integer harvestLevel;

    private String imageUrl;

    private boolean isAchieved;
}