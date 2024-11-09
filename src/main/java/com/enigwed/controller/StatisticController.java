package com.enigwed.controller;

import com.enigwed.constant.SPathApi;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.security.JwtUtil;
import com.enigwed.service.StatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class StatisticController {
    private final StatisticService statisticService;
    private final JwtUtil jwtUtil;

    @Operation(
            summary = "For admin and wedding organizer to get statistic [ADMIN, WO] (WEB)",
            description = "Admin get income statistic from confirmed subscription and list of wedding organizer status. Wedding organizer get income statistic from finished order and information of wedding packages"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @GetMapping(SPathApi.PROTECTED_STATISTIC)
    public ResponseEntity<?> getStatistic(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate
    ) {
        if (startDate == null) startDate = LocalDateTime.now().minusMonths(6);
        if (endDate == null) endDate = LocalDateTime.now();

        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = statisticService.getStatisticsIncome(userInfo, startDate, endDate);;
        return ResponseEntity.ok(response);
    }
}
