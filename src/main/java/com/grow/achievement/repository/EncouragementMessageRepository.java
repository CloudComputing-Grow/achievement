package com.grow.achievement.repository;

import com.grow.achievement.entity.EncouragementMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EncouragementMessageRepository
        extends JpaRepository<EncouragementMessage, Long> {

    List<EncouragementMessage> findByMinRateLessThanEqualAndMaxRateGreaterThanEqual(
            int rate,
            int rate2
    );
}