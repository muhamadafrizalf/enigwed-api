package com.enigwed.service;

import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.BankAccountRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.BankAccountResponse;

import java.util.List;

public interface BankAccountService {
    // Customer
    ApiResponse<List<BankAccountResponse>> customerFindAllBankAccountsByWeddingOrganizer(String weddingOrganizerId);
    // Wedding organizer
    ApiResponse<List<BankAccountResponse>> findOwnBankAccounts(JwtClaim userInfo);
    ApiResponse<BankAccountResponse> findOwnBankAccountById(String id);
    ApiResponse<BankAccountResponse> createBankAccount(JwtClaim userInfo, BankAccountRequest bankAccountRequest);
    ApiResponse<BankAccountResponse> updateBankAccount(JwtClaim userInfo, BankAccountRequest bankAccountRequest);
    ApiResponse<?> deleteBankAccount(JwtClaim userInfo, String id);
}
