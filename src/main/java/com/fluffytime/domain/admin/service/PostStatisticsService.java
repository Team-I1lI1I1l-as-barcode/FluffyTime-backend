package com.fluffytime.domain.admin.service;

import static com.fluffytime.domain.admin.util.constants.StatisticsDateRange.CONTENT_DAILY_DATE;
import static com.fluffytime.domain.admin.util.convertor.StatisticsConvertor.convertToDate;

import com.fluffytime.domain.admin.dto.response.DailyCountStatisticsResponse;
import com.fluffytime.domain.board.repository.PostRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostStatisticsService {

    private final PostRepository postRepository;

    // 일일 콘텐츠 등록 통계 불러오기 (31일)
    @Transactional
    public DailyCountStatisticsResponse getDailyPostCount() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(CONTENT_DAILY_DATE.getDate());

        return DailyCountStatisticsResponse.builder()
            .dailyCountsStatistics(convertToDate(postRepository.findPostCountByCreatedAtBetween(startDate, endDate)))
            .build();
    }
}
