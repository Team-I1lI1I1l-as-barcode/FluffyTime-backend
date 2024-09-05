package com.fluffytime.domain.admin.util.convertor;

import com.fluffytime.domain.admin.dto.DailyCount;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public class StatisticsConvertor {
    // LocalDateTime -> LocalDate 변경 메서드
    public static HashMap<LocalDate, Long> convertToDate(List<DailyCount> dailyCounts) {
        HashMap<LocalDate, Long> convertedData = new HashMap<>();
        dailyCounts.forEach(dailyCount -> {
                LocalDate convertToLocalDate = dailyCount.getDate().toLocalDate();
                if(convertedData.containsKey(convertToLocalDate)) {
                    Long currentCount = convertedData.get(convertToLocalDate);
                    convertedData.put(dailyCount.getDate().toLocalDate(),currentCount + 1);
                } else {
                    convertedData.put(dailyCount.getDate().toLocalDate(),dailyCount.getCount());
                }
            }
        );
        return convertedData;
    }
}
