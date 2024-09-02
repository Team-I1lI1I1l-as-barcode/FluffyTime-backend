package com.fluffytime.domain.admin.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class DailyContentsCount implements DailyCount{
    private LocalDateTime date;
    private Long count;
}
