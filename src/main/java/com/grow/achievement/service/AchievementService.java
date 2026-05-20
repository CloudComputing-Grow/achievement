package com.grow.achievement.service;

import com.grow.achievement.dto.request.HarvestEventRequest;
import com.grow.achievement.dto.response.*;
import com.grow.achievement.entity.AchievementItem;
import com.grow.achievement.entity.UserAchievement;
import com.grow.achievement.entity.UserAchievementSummary;
import com.grow.achievement.repository.AchievementItemRepository;
import com.grow.achievement.repository.UserAchievementRepository;
import com.grow.achievement.repository.UserAchievementSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementItemRepository achievementItemRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserAchievementSummaryRepository summaryRepository;

    @Transactional(readOnly = true)
    public AchievementListResponse getAchievements(Long userId) {

        List<AchievementItem> allItems = achievementItemRepository.findAll();

        List<AchievementItemResponse> items = allItems.stream()
                .map(item -> {

                    boolean achieved = userAchievementRepository
                            .existsByUserIdAndAchievementItem_Id(userId, item.getId());

                    return AchievementItemResponse.builder()
                            .itemId(item.getId())
                            .name(item.getName())
                            .fruitName(item.getFruitName())
                            .category(item.getCategory())
                            .harvestLevel(item.getHarvestLevel())
                            .imageUrl(item.getImageUrl())
                            .isAchieved(achieved)
                            .build();
                })
                .toList();

        int totalCount = allItems.size();

        int achievedCount =
                (int) userAchievementRepository.countByUserIdAndIsAchievedTrue(userId);

        double completionRate =
                totalCount == 0
                        ? 0
                        : (achievedCount * 100.0) / totalCount;

        return AchievementListResponse.builder()
                .totalCount(totalCount)
                .achievedCount(achievedCount)
                .completionRate(completionRate)
                .items(items)
                .build();
    }

    @Transactional(readOnly = true)
    public AchievementSummaryResponse getSummary(Long userId) {

        UserAchievementSummary summary =
                summaryRepository.findById(userId)
                        .orElse(
                                UserAchievementSummary.builder()
                                        .userId(userId)
                                        .totalCount(0)
                                        .achievedCount(0)
                                        .completionRate(BigDecimal.ZERO)
                                        .build()
                        );

        return AchievementSummaryResponse.builder()
                .totalCount(summary.getTotalCount())
                .achievedCount(summary.getAchievedCount())
                .completionRate(summary.getCompletionRate().doubleValue())
                .build();
    }

    @Transactional
    public SuccessResponse updateAchievement(HarvestEventRequest request) {

        boolean exists =
                userAchievementRepository.existsByUserIdAndAchievementItem_Id(
                        request.getUserId(),
                        request.getItemId()
                );

        if (exists) {
            return SuccessResponse.builder()
                    .success(true)
                    .build();
        }

        AchievementItem item =
                achievementItemRepository.findById(request.getItemId())
                        .orElseThrow();

        UserAchievement achievement =
                UserAchievement.builder()
                        .userId(request.getUserId())
                        .achievementItem(item)
                        .isAchieved(true)
                        .type(request.getType())
                        .achievedAt(LocalDateTime.now())
                        .build();

        userAchievementRepository.save(achievement);

        updateSummary(request.getUserId());

        return SuccessResponse.builder()
                .success(true)
                .build();
    }

    private void updateSummary(Long userId) {

        int totalCount =
                (int) achievementItemRepository.count();

        int achievedCount =
                (int) userAchievementRepository.countByUserIdAndIsAchievedTrue(userId);

        BigDecimal completionRate =
                totalCount == 0
                        ? BigDecimal.ZERO
                        : BigDecimal.valueOf(
                        (achievedCount * 100.0) / totalCount
                ).setScale(2, RoundingMode.HALF_UP);

        UserAchievementSummary summary =
                UserAchievementSummary.builder()
                        .userId(userId)
                        .totalCount(totalCount)
                        .achievedCount(achievedCount)
                        .completionRate(completionRate)
                        .build();

        summaryRepository.save(summary);
    }
}