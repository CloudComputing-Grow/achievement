package com.grow.achievement.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grow.achievement.config.RabbitMQConfig;
import com.grow.achievement.dto.event.UserDeletedEvent;
import com.grow.achievement.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final AchievementService achievementService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConfig.USER_EVENTS_QUEUE)
    public void handleUserDeleted(String message) {
        try {
            UserDeletedEvent event =
                    objectMapper.readValue(message, UserDeletedEvent.class);

            if (!"UserDeleted".equals(event.getEventType())) {
                return;
            }

            Long userId = event.getUserId();

            System.out.println(
                    "[Achievement 서비스] 유저 "
                            + userId
                            + " 회원탈퇴 -> achievement 데이터 정리"
            );

            achievementService.deleteUserAchievementData(userId);

            System.out.println(
                    "[Achievement 서비스] 유저 "
                            + userId
                            + " achievement 데이터 삭제 완료"
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
