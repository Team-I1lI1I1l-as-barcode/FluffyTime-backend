package com.fluffytime.domain.admin.dto;

import java.time.LocalDateTime;

public interface DailyCount {
    LocalDateTime getDate();
    Long getCount();
}
