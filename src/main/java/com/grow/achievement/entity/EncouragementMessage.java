package com.grow.achievement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "encouragement_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncouragementMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String message;

    @Column(name = "min_rate", nullable = false)
    private int minRate;

    @Column(name = "max_rate", nullable = false)
    private int maxRate;
}