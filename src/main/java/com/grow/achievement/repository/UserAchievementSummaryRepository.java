package com.grow.achievement.repository;

import com.grow.achievement.entity.UserAchievementSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAchievementSummaryRepository
        extends JpaRepository<UserAchievementSummary, Long> {
}