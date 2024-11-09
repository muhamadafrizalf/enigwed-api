package com.enigwed.service.impl;

import com.enigwed.constant.ErrorMessage;
import com.enigwed.constant.Message;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.BankAccountRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.BankAccountResponse;
import com.enigwed.entity.BankAccount;
import com.enigwed.entity.WeddingOrganizer;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.repository.BankAccountRepository;
import com.enigwed.service.BankAccountService;
import com.enigwed.service.WeddingOrganizerService;
import com.enigwed.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankAccountImpl implements BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final WeddingOrganizerService weddingOrganizerService;
    private final ValidationUtil validationUtil;

    private BankAccount findByIdOrThrow(String id) {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.BANK_ACCOUNT_ID_IS_REQUIRED);
        return bankAccountRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, ErrorMessage.BANK_ACCOUNT_NOT_FOUND));
    }

    private void validateUserAccess(JwtClaim userInfo, BankAccount bankAccount) throws AccessDeniedException {
        String userCredentialId = bankAccount.getWeddingOrganizer().getUserCredential().getId();
        if (userInfo.getUserId().equals(userCredentialId)) {
            return;
        }
        throw new AccessDeniedException(ErrorMessage.ACCESS_DENIED);
    }

    @Override
    public ApiResponse<BankAccountResponse> findBankAccountById(String id) {
        BankAccount bankAccount =  findByIdOrThrow(id);
        BankAccountResponse response = BankAccountResponse.from(bankAccount);
        return ApiResponse.success(response, Message.BANK_ACCOUNT_FOUND);
    }

    @Override
    public ApiResponse<List<BankAccountResponse>> getOwnBankAccount(JwtClaim userInfo) {
        WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());
        List<BankAccount> bankAccountList = bankAccountRepository.findByWeddingOrganizerIdAndDeletedAtIsNull(wo.getId());
        if (bankAccountList == null || bankAccountList.isEmpty())
            return ApiResponse.success(new ArrayList<>(), Message.NO_BANK_ACCOUNT_FOUND);
        List<BankAccountResponse> responses = bankAccountList.stream().map(BankAccountResponse::from).toList();
        return ApiResponse.success(responses, Message.BANK_ACCOUNTS_FOUND);
    }

    @Override
    public ApiResponse<BankAccountResponse> createBankAccount(JwtClaim userInfo, BankAccountRequest bankAccountRequest) {
        validationUtil.validateAndThrow(bankAccountRequest);

        WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());

        BankAccount bankAccount = BankAccount.builder()
                .weddingOrganizer(wo)
                .bankCode(bankAccountRequest.getBankCode())
                .bankName(bankAccountRequest.getBankName())
                .accountNumber(bankAccountRequest.getAccountNumber())
                .accountName(bankAccountRequest.getAccountName())
                .accountName(bankAccountRequest.getAccountName())
                .build();

        bankAccount = bankAccountRepository.save(bankAccount);

        BankAccountResponse response = BankAccountResponse.from(bankAccount);
        return ApiResponse.success(response, Message.BANK_ACCOUNT_CREATED);
    }

    @Override
    public ApiResponse<BankAccountResponse> updateBankAccount(JwtClaim userInfo, BankAccountRequest bankAccountRequest) {
        try {
            validationUtil.validateAndThrow(bankAccountRequest);

            BankAccount bankAccount = findByIdOrThrow(bankAccountRequest.getId());

            validateUserAccess(userInfo, bankAccount);

            bankAccount.setBankCode(bankAccountRequest.getBankCode());
            bankAccount.setBankName(bankAccountRequest.getBankName());
            bankAccount.setAccountNumber(bankAccountRequest.getAccountNumber());

            bankAccount = bankAccountRepository.save(bankAccount);

            BankAccountResponse response = BankAccountResponse.from(bankAccount);
            return ApiResponse.success(response, Message.BANK_ACCOUNT_UPDATED);
        } catch (AccessDeniedException e) {
            throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.UPDATE_FAILED, e.getMessage());
        }
    }

    @Override
    public ApiResponse<?> deleteBankAccount(JwtClaim userInfo, String id) {
        try {
            BankAccount bankAccount = findByIdOrThrow(id);

            validateUserAccess(userInfo, bankAccount);

            bankAccount.setDeletedAt(LocalDateTime.now());

            bankAccountRepository.save(bankAccount);

            return ApiResponse.success(Message.BANK_ACCOUNT_DELETED);
        } catch (AccessDeniedException e) {
            throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.DELETE_FAILED, e.getMessage());
        }
    }

    @Override
    public Integer countBankAccountsByWeddingOrganizerId(String weddingOrganizerId) {
        return bankAccountRepository.countByWeddingOrganizerIdAndDeletedAtIsNull(weddingOrganizerId);
    }
}
