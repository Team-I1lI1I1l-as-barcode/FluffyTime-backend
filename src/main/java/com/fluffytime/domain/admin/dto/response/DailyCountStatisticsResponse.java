package com.fluffytime.domain.admin.dto.response;

import java.time.LocalDate;
import java.util.HashMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyCountStatisticsResponse {
    private HashMap<LocalDate, Long> dailyCountsStatistics;
}
