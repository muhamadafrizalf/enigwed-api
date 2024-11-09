package com.enigwed.controller;

import com.enigwed.constant.PathApi;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.BankAccountRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.security.JwtUtil;
import com.enigwed.service.BankAccountService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class BankAccountController {
    private final BankAccountService bankAccountService;
    private final JwtUtil jwtUtil;

    @PreAuthorize("hasRole('WO')")
    @GetMapping(PathApi.PROTECTED_BANK_ACCOUNT_ID)
    public ResponseEntity<?> getBankAccountById(
            @PathVariable String id
    ) {
        ApiResponse<?> response = bankAccountService.findBankAccountById(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('WO')")
    @GetMapping(PathApi.PROTECTED_BANK_ACCOUNT)
    public ResponseEntity<?> getOwnBankAccounts(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = bankAccountService.getOwnBankAccount(userInfo);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('WO')")
    @PutMapping(PathApi.PROTECTED_BANK_ACCOUNT)
    public ResponseEntity<?> updateBankAccount(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody BankAccountRequest bankAccountRequest
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = bankAccountService.updateBankAccount(userInfo, bankAccountRequest);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('WO')")
    @PostMapping(PathApi.PROTECTED_BANK_ACCOUNT)
    public ResponseEntity<?> createBankAccount(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody BankAccountRequest bankAccountRequest
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = bankAccountService.createBankAccount(userInfo, bankAccountRequest);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('WO')")
    @DeleteMapping(PathApi.PROTECTED_BANK_ACCOUNT_ID)
    public ResponseEntity<?> deleteBankAccount(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = bankAccountService.deleteBankAccount(userInfo, id);
        return ResponseEntity.ok(response);
    }

}