package com.grow.achievement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_badge")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

    @Column(name = "is_representative", nullable = false)
    private boolean isRepresentative;

    @Column(name = "achieved_at")
    private LocalDateTime achievedAt;

    @PrePersist
    protected void onCreate() {
        this.achievedAt = LocalDateTime.now();
    }
}