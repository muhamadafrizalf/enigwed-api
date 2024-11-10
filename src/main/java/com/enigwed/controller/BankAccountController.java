package com.enigwed.controller;

import com.enigwed.constant.SPathApi;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.BankAccountRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.security.JwtUtil;
import com.enigwed.service.BankAccountService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "For customer to get list of bank account by wedding_organizer_id (MOBILE)")
    @GetMapping(SPathApi.PUBLIC_BANK_ACCOUNT)
    public ResponseEntity<?> getBankAccounts(
            @RequestParam String weddingOrganizerId
    ) {
        ApiResponse<?> response = bankAccountService.customerFindAllBankAccountsByWeddingOrganizer(weddingOrganizerId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "For wedding organizer to get bank account information by bank_account_id [WO] (WEB)")
    @PreAuthorize("hasRole('WO')")
    @GetMapping(SPathApi.PROTECTED_BANK_ACCOUNT_ID)
    public ResponseEntity<?> getBankAccountById(
            @PathVariable String id
    ) {
        ApiResponse<?> response = bankAccountService.findOwnBankAccountById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "For wedding organizer to get list of bank account owned by wedding organizer [WO] (WEB)")
    @PreAuthorize("hasRole('WO')")
    @GetMapping(SPathApi.PROTECTED_BANK_ACCOUNT)
    public ResponseEntity<?> getOwnBankAccounts(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = bankAccountService.findOwnBankAccounts(userInfo);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "For wedding organizer to edit bank account information [WO] (WEB)")
    @PreAuthorize("hasRole('WO')")
    @PutMapping(SPathApi.PROTECTED_BANK_ACCOUNT)
    public ResponseEntity<?> updateBankAccount(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody BankAccountRequest bankAccountRequest
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = bankAccountService.updateBankAccount(userInfo, bankAccountRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "For wedding organizer to create bank account information [WO] (WEB)")
    @PreAuthorize("hasRole('WO')")
    @PostMapping(SPathApi.PROTECTED_BANK_ACCOUNT)
    public ResponseEntity<?> createBankAccount(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody BankAccountRequest bankAccountRequest
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = bankAccountService.createBankAccount(userInfo, bankAccountRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "For wedding organizer to delete bank account information by bank_account_id [WO] (WEB)")
    @PreAuthorize("hasRole('WO')")
    @DeleteMapping(SPathApi.PROTECTED_BANK_ACCOUNT_ID)
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
