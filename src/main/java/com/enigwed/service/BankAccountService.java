package com.enigwed.service;

import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.BankAccountRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.BankAccountResponse;

import java.util.List;

public interface BankAccountService {
    // Customer
    ApiResponse<List<BankAccountResponse>> getBankAccountsByWeddingOrganizerId(String weddingOrganizerId);
    // Wedding organizer
    ApiResponse<BankAccountResponse> findBankAccountById(String id);
    ApiResponse<List<BankAccountResponse>> getOwnBankAccount(JwtClaim userInfo);
    ApiResponse<BankAccountResponse> createBankAccount(JwtClaim userInfo, BankAccountRequest bankAccountRequest);
    ApiResponse<BankAccountResponse> updateBankAccount(JwtClaim userInfo, BankAccountRequest bankAccountRequest);
    ApiResponse<?> deleteBankAccount(JwtClaim userInfo, String id);
    Integer countBankAccountsByWeddingOrganizerId(String weddingOrganizerId);
}
