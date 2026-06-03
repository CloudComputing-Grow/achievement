package com.grow.achievement.service;

import com.grow.achievement.dto.request.HarvestEventRequest;
import com.grow.achievement.dto.request.RepresentativeBadgeRequest;
import com.grow.achievement.dto.response.*;
import com.grow.achievement.entity.*;
import com.grow.achievement.entity.enums.AchievementCategory;
import com.grow.achievement.entity.enums.AchievementType;
import com.grow.achievement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementItemRepository achievementItemRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserAchievementSummaryRepository summaryRepository;

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;

    private final EncouragementMessageRepository encouragementMessageRepository;

    @Transactional(readOnly = true)
    public AchievementListResponse getAchievements(Long userId) {

        List<AchievementItem> allItems =
                achievementItemRepository.findAll();

        List<AchievementItemResponse> items =
                allItems.stream()
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
                                    .achieved(achieved)
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
    public SuccessResponse updateAchievement(
            HarvestEventRequest request
    ) {

        if (request.getType() != AchievementType.HARVEST) {

            return SuccessResponse.builder()
                    .success(false)
                    .build();
        }

        if (request.getItemId() == 1L) {

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

        updateSummary(request.getUserId());

        grantBadgeIfEligible(request.getUserId());

        return SuccessResponse.builder()
                .success(true)
                .build();
    }

    private void updateSummary(Long userId) {

        int totalCount =
                (int) achievementItemRepository.count();

        int achievedCount =
                (int) userAchievementRepository
                        .countByUserIdAndIsAchievedTrue(userId);

        BigDecimal completionRate =
                totalCount == 0
                        ? BigDecimal.ZERO
                        : BigDecimal.valueOf(
                        (achievedCount * 100.0) / totalCount
                ).setScale(2, RoundingMode.HALF_UP);

        UserAchievementSummary summary =
                summaryRepository.findById(userId)
                        .orElse(
                                UserAchievementSummary.builder()
                                        .userId(userId)
                                        .build()
                        );

        summary.setTotalCount(totalCount);
        summary.setAchievedCount(achievedCount);
        summary.setCompletionRate(completionRate);
        summary.setUpdatedAt(LocalDateTime.now());

        summaryRepository.save(summary);
    }

    private void grantBadgeIfEligible(Long userId) {

        long normalCount =
                userAchievementRepository
                        .countByUserIdAndIsAchievedTrueAndAchievementItem_Category(
                                userId,
                                AchievementCategory.NORMAL
                        );

        long goldCount =
                userAchievementRepository
                        .countByUserIdAndIsAchievedTrueAndAchievementItem_Category(
                                userId,
                                AchievementCategory.GOLD
                        );

        if (normalCount >= 8) {
            grantBadge(userId, 1L);
        }

        if (goldCount >= 8) {
            grantBadge(userId, 2L);
        }
    }

    private void grantBadge(Long userId, Long badgeId) {

        boolean alreadyOwned =
                userBadgeRepository.existsByUserIdAndBadge_Id(
                        userId,
                        badgeId
                );

        if (alreadyOwned) {
            return;
        }

        Badge badge =
                badgeRepository.findById(badgeId)
                        .orElseThrow(
                                () -> new RuntimeException("휘장 없음")
                        );

        UserBadge userBadge =
                UserBadge.builder()
                        .userId(userId)
                        .badge(badge)
                        .isRepresentative(false)
                        .achievedAt(LocalDateTime.now())
                        .build();

        userBadgeRepository.save(userBadge);
    }

    @Transactional(readOnly = true)
    public BadgeListResponse getBadges(Long userId) {

        List<UserBadge> userBadges =
                userBadgeRepository.findByUserId(userId);

        List<BadgeResponse> badges =
                userBadges.stream()
                        .map(userBadge -> {

                            Badge badge = userBadge.getBadge();

                            return BadgeResponse.builder()
                                    .badgeId(badge.getId())
                                    .name(badge.getName())
                                    .category(badge.getCategory())
                                    .achievedAt(
                                            userBadge.getAchievedAt()
                                    )
                                    .build();
                        })
                        .toList();

        return BadgeListResponse.builder()
                .badges(badges)
                .build();
    }

    @Transactional(readOnly = true)
    public RepresentativeBadgeResponse getRepresentativeBadge(
            Long userId
    ) {

        return userBadgeRepository
                .findByUserIdAndIsRepresentativeTrue(userId)
                .map(userBadge -> {

                    Badge badge = userBadge.getBadge();

                    return RepresentativeBadgeResponse.builder()
                            .badgeId(badge.getId())
                            .name(badge.getName())
                            .category(badge.getCategory())
                            .build();
                })
                .orElse(
                        RepresentativeBadgeResponse.builder()
                                .badgeId(0L)
                                .name(null)
                                .category(null)
                                .build()
                );
    }

    @Transactional
    public RepresentativeBadgeUpdateResponse updateRepresentativeBadge(
            Long userId,
            RepresentativeBadgeRequest request
    ) {

        UserBadge newRepresentativeBadge =
                userBadgeRepository
                        .findByUserIdAndBadge_Id(
                                userId,
                                request.getBadgeId()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "보유하지 않은 휘장"
                                )
                        );

        userBadgeRepository
                .findByUserIdAndIsRepresentativeTrue(userId)
                .ifPresent(oldBadge -> {

                    oldBadge.setRepresentative(false);

                    userBadgeRepository.save(oldBadge);
                });

        newRepresentativeBadge.setRepresentative(true);

        userBadgeRepository.save(newRepresentativeBadge);

        return RepresentativeBadgeUpdateResponse.builder()
                .badgeId(request.getBadgeId())
                .message("대표 휘장이 설정되었습니다.")
                .build();
    }

    @Transactional(readOnly = true)
    public MessageResponse getEncouragementMessage(Long userId) {

        UserAchievementSummary summary =
                summaryRepository.findById(userId)
                        .orElseThrow();

        int completionRate =
                summary.getCompletionRate().intValue();

        List<EncouragementMessage> messages =
                encouragementMessageRepository
                        .findByMinRateLessThanEqualAndMaxRateGreaterThanEqual(
                                completionRate,
                                completionRate
                        );

        if (messages.isEmpty()) {

            throw new RuntimeException("문구 없음");
        }

        EncouragementMessage randomMessage =
                messages.get(
                        new Random().nextInt(messages.size())
                );

        return MessageResponse.builder()
                .message(randomMessage.getMessage())
                .build();
    }

    @Transactional(readOnly = true)
    public MessageResponse getMessage(Long userId) {

        UserAchievementSummary summary =
                summaryRepository.findById(userId)
                        .orElseThrow(
                                () -> new RuntimeException("요약 정보 없음")
                        );

        int rate = summary.getCompletionRate().intValue();

        List<EncouragementMessage> messages =
                encouragementMessageRepository
                        .findByMinRateLessThanEqualAndMaxRateGreaterThanEqual(
                                rate,
                                rate
                        );

        if (messages.isEmpty()) {
            throw new RuntimeException("문구 없음");
        }

        int randomIndex =
                (int) (Math.random() * messages.size());

        EncouragementMessage message =
                messages.get(randomIndex);

        return MessageResponse.builder()
                .message(message.getMessage())
                .build();
    }
}