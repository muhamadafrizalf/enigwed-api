package com.enigwed.service;

import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.Map;

public interface StatisticService {
    ApiResponse<Map<String, Double>> getStatisticsIncome(JwtClaim userInfo, LocalDateTime from, LocalDateTime to);
}
