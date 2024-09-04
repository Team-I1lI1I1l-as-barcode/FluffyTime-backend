package com.fluffytime.domain.admin.util.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum StatisticsDateRange {
    USER_DAILY_DATE(31),
    CONTENT_DAILY_DATE(31);

    private final int date;
}
