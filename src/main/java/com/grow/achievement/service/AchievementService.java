package com.grow.achievement.service;

import com.grow.achievement.dto.request.HarvestEventRequest;
import com.grow.achievement.dto.response.*;
import com.grow.achievement.entity.AchievementItem;
import com.grow.achievement.entity.UserAchievement;
import com.grow.achievement.entity.UserAchievementSummary;
import com.grow.achievement.entity.enums.AchievementType;
import com.grow.achievement.repository.AchievementItemRepository;
import com.grow.achievement.repository.UserAchievementRepository;
import com.grow.achievement.repository.UserAchievementSummaryRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

                    boolean achieved =
                            userAchievementRepository
                                    .existsByUserIdAndAchievementItem_Id(
                                            userId,
                                            item.getId()
                                    );

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
                (int) userAchievementRepository
                        .countByUserIdAndIsAchievedTrue(userId);

        double completionRate =
                totalCount == 0
                        ? 0
                        : BigDecimal.valueOf(
                                (achievedCount * 100.0) / totalCount
                        )
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();

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

        if (request.getType() != AchievementType.HARVEST) {

            return SuccessResponse.builder()
                    .success(false)
                    .build();
        }

        AchievementItem item =
                achievementItemRepository.findById(request.getItemId())
                        .orElseThrow(
                                () -> new RuntimeException("아이템 없음")
                        );

        boolean alreadyExists =
                userAchievementRepository
                        .existsByUserIdAndAchievementItem_Id(
                                request.getUserId(),
                                item.getId()
                        );

        if (!alreadyExists) {

            UserAchievement achievement =
                    UserAchievement.builder()
                            .userId(request.getUserId())
                            .achievementItem(item)
                            .isAchieved(true)
                            .type(AchievementType.HARVEST)
                            .achievedAt(LocalDateTime.now())
                            .build();

            userAchievementRepository.save(achievement);
        }

        long achievedCount =
                userAchievementRepository
                        .countByUserIdAndIsAchievedTrue(
                                request.getUserId()
                        );

        long totalCount =
                achievementItemRepository.count();

        double completionRate =
                totalCount == 0
                        ? 0
                        : ((double) achievedCount / totalCount) * 100;

        UserAchievementSummary summary =
                summaryRepository.findById(request.getUserId())
                        .orElse(
                                UserAchievementSummary.builder()
                                        .userId(request.getUserId())
                                        .build()
                        );

        summary.setAchievedCount((int) achievedCount);

        summary.setTotalCount((int) totalCount);

        summary.setCompletionRate(
                BigDecimal.valueOf(completionRate)
                        .setScale(2, RoundingMode.HALF_UP)
        );

        summary.setUpdatedAt(LocalDateTime.now());

        summaryRepository.save(summary);

        return SuccessResponse.builder()
                .success(true)
                .build();
    }
}