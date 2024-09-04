package com.fluffytime.domain.admin.service;

import static com.fluffytime.domain.admin.util.constants.StatisticsDateRange.USER_DAILY_DATE;
import static com.fluffytime.domain.admin.util.convertor.StatisticsConvertor.convertToDate;

import com.fluffytime.domain.admin.dto.response.DailyCountStatisticsResponse;
import com.fluffytime.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserStatisticsService {

    private final UserRepository userRepository;

    // 일일 유저 가입자 수 불러오기 (31일)
    @Transactional
    public DailyCountStatisticsResponse getDailyUserCounts() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(USER_DAILY_DATE.getDate());

        return DailyCountStatisticsResponse.builder()
            .dailyCountsStatistics(
                convertToDate(userRepository
                    .findUserCountByRegistrationAtBetween(startDate, endDate)))
            .build();
    }
}
