package com.grow.achievement.dto.request;

import com.grow.achievement.entity.enums.AchievementType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HarvestEventRequest {

    private Long userId;

    private Long itemId;

    private AchievementType type;
}