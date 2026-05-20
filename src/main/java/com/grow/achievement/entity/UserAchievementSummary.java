package com.grow.achievement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_achievement_summary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAchievementSummary {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "total_count", nullable = false)
    private int totalCount;

    @Column(name = "achieved_count", nullable = false)
    private int achievedCount;

    @Column(name = "completion_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal completionRate;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}