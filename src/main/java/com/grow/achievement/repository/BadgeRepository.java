package com.grow.achievement.repository;

import com.grow.achievement.entity.Badge;
import com.grow.achievement.entity.enums.AchievementCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    List<Badge> findByCategory(AchievementCategory category);
}