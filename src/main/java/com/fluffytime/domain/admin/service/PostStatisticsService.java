package com.fluffytime.domain.admin.service;

import static com.fluffytime.domain.admin.util.convertor.StatisticsConvertor.convertToDate;

import com.fluffytime.domain.admin.dto.response.DailyCountStatisticsResponse;
import com.fluffytime.domain.board.repository.PostRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostStatisticsService {

    private final PostRepository postRepository;

    public DailyCountStatisticsResponse getDailyPostCount() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(31);

        return DailyCountStatisticsResponse.builder()
            .dailyCountsStatistics(convertToDate(postRepository.findPostCountByCreatedAtBetween(startDate, endDate)))
            .build();
    }
}
