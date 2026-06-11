package com.grow.achievement.repository;

import com.grow.achievement.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserBadgeRepository
        extends JpaRepository<UserBadge, Long> {

    List<UserBadge> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    Optional<UserBadge> findByUserIdAndIsRepresentativeTrue(
            Long userId
    );

    boolean existsByUserIdAndBadge_Id(
            Long userId,
            Long badgeId
    );

    Optional<UserBadge> findByUserIdAndBadge_Id(
            Long userId,
            Long badgeId
    );
}
