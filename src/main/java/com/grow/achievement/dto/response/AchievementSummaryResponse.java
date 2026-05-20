package com.grow.achievement.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AchievementSummaryResponse {

    private int totalCount;
    private int achievedCount;
    private double completionRate;
}