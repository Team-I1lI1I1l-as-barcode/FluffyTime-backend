package com.fluffytime.domain.admin.controller.api;

import com.fluffytime.domain.admin.dto.response.DailyCountStatisticsResponse;
import com.fluffytime.domain.admin.service.PostStatisticsService;
import com.fluffytime.domain.admin.service.UserStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/statistics")
public class StatisticsRestController {

    private final UserStatisticsService userStatisticsService;
    private final PostStatisticsService postStatisticsService;

    @PostMapping("/user")
    public ResponseEntity<DailyCountStatisticsResponse> userStatistics() {
        DailyCountStatisticsResponse dailyUserCountsResponse = userStatisticsService.getDailyUserCounts();
        return ResponseEntity.status(HttpStatus.OK).body(dailyUserCountsResponse);
    }

    @PostMapping("/contents")
    public ResponseEntity<DailyCountStatisticsResponse> contentsStatistics() {
        DailyCountStatisticsResponse dailyCountStatisticsResponse = postStatisticsService.getDailyPostCount();
        return ResponseEntity.status(HttpStatus.OK).body(dailyCountStatisticsResponse);
    }
}
