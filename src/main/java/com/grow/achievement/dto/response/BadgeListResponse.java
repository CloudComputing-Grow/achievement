package com.grow.achievement.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BadgeListResponse {

    private List<BadgeResponse> badges;
}