package com.grow.achievement.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AchievementListResponse {

    private int totalCount;
    private int achievedCount;
    private double completionRate;
    private List<AchievementItemResponse> items;
}