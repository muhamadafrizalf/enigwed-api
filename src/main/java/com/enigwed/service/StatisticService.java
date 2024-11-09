package com.enigwed.service;

import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.StatisticResponse;

import java.time.LocalDateTime;

public interface StatisticService {
    ApiResponse<StatisticResponse> getStatisticsIncome(JwtClaim userInfo, LocalDateTime from, LocalDateTime to);
}
