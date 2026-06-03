package com.grow.achievement.repository;

import com.grow.achievement.entity.UserAchievement;
import com.grow.achievement.entity.enums.AchievementCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

    List<UserAchievement> findByUserId(Long userId);

    Optional<UserAchievement> findByUserIdAndAchievementItem_Id(
            Long userId,
            Long achievementItemId
    );

    boolean existsByUserIdAndAchievementItem_Id(
            Long userId,
            Long achievementItemId
    );

    long countByUserIdAndIsAchievedTrue(Long userId);

    long countByUserIdAndIsAchievedTrueAndAchievementItem_Category(
            Long userId,
            AchievementCategory category
    );
}