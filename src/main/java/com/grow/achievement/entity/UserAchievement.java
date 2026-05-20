package com.grow.achievement.entity;

import com.grow.achievement.entity.enums.AchievementType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_achievement",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "achievement_item_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_item_id", nullable = false)
    private AchievementItem achievementItem;

    @Column(name = "is_achieved", nullable = false)
    private boolean isAchieved;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementType type;

    @Column(name = "achieved_at")
    private LocalDateTime achievedAt;
}