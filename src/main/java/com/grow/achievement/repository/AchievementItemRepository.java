package com.grow.achievement.repository;

import com.grow.achievement.entity.AchievementItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementItemRepository extends JpaRepository<AchievementItem, Long> {
}