package com.grow.achievement.controller;

import com.grow.achievement.dto.request.HarvestEventRequest;
import com.grow.achievement.dto.request.RepresentativeBadgeRequest;

import com.grow.achievement.dto.response.*;

import com.grow.achievement.service.AchievementService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;

    @GetMapping("/achievements")
    public AchievementListResponse getAchievements() {

        Long userId = 1L;

        return achievementService.getAchievements(userId);
    }

    @GetMapping("/achievements/summary")
    public AchievementSummaryResponse getSummary() {

        Long userId = 1L;

        return achievementService.getSummary(userId);
    }

    @PostMapping("/internal/achievements")
    public ResponseEntity<SuccessResponse> updateAchievement(
            @RequestBody HarvestEventRequest request
    ) {

        return ResponseEntity.ok(
                achievementService.updateAchievement(request)
        );
    }

    @GetMapping("/achievements/badges")
    public BadgeListResponse getBadges(
            @RequestHeader("X-User-Id") Long userId
    ) {

        return achievementService.getBadges(userId);
    }

    @GetMapping("/achievements/badges/representative")
    public RepresentativeBadgeResponse getRepresentativeBadge(
            @RequestHeader("X-User-Id") Long userId
    ) {

        return achievementService.getRepresentativeBadge(userId);
    }

    @PutMapping("/achievements/badges/representative")
    public RepresentativeBadgeUpdateResponse updateRepresentativeBadge(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody RepresentativeBadgeRequest request
    ) {

        return achievementService.updateRepresentativeBadge(
                userId,
                request
        );
    }

    @GetMapping("/achievements/message")
    public MessageResponse getMessage(
            @RequestHeader("X-User-Id") Long userId
    ) {

        return achievementService.getEncouragementMessage(userId);
    }
}