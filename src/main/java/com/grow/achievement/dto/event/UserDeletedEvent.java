package com.grow.achievement.dto.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserDeletedEvent {
    private String eventType;
    private Long userId;
}

