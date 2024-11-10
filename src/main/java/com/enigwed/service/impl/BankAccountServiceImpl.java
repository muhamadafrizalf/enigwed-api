package com.enigwed.service.impl;

import com.enigwed.constant.SErrorMessage;
import com.enigwed.constant.SMessage;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.BankAccountRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.BankAccountResponse;
import com.enigwed.entity.BankAccount;
import com.enigwed.entity.WeddingOrganizer;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.exception.ValidationException;
import com.enigwed.repository.BankAccountRepository;
import com.enigwed.service.BankAccountService;
import com.enigwed.service.WeddingOrganizerService;
import com.enigwed.util.AccessValidationUtil;
import com.enigwed.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final WeddingOrganizerService weddingOrganizerService;
    private final ValidationUtil validationUtil;
    private final AccessValidationUtil accessValidationUtil;

    private BankAccount findByIdOrThrow(String id) {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, SErrorMessage.BANK_ACCOUNT_ID_IS_REQUIRED);
        return bankAccountRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.FETCHING_FAILED, SErrorMessage.BANK_ACCOUNT_NOT_FOUND(id)));
    }

    private ApiResponse<List<BankAccountResponse>> getListApiResponse(WeddingOrganizer wo, List<BankAccount> bankAccountList) {
        if (bankAccountList == null || bankAccountList.isEmpty())
            return ApiResponse.success(new ArrayList<>(), SMessage.NO_BANK_ACCOUNT_FOUND(wo.getName()));
        List<BankAccountResponse> responses = bankAccountList.stream().map(BankAccountResponse::all).toList();
        return ApiResponse.success(responses, SMessage.BANK_ACCOUNTS_FOUND(wo.getName(), responses.size()));
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<BankAccountResponse>> getBankAccountsByWeddingOrganizerId(String weddingOrganizerId) {
        try {
            /* LOAD WEDDING ORGANIZER */
            // ErrorResponse //
            WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerById(weddingOrganizerId);

            /* FIND BANK ACCOUNTS */
            List<BankAccount> bankAccountList = bankAccountRepository.findByWeddingOrganizerIdAndDeletedAtIsNull(weddingOrganizerId);

            /* GET RESPONSE */
            return getListApiResponse(wo, bankAccountList);

        } catch (ErrorResponse e) {
            log.error("Error while loading bank accounts by wedding organizer ID: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<BankAccountResponse>> getOwnBankAccount(JwtClaim userInfo) {
        try {
            /* LOAD WEDDING ORGANIZER */
            // ErrorResponse //
            WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());

            /* FIND BANK ACCOUNTS */
            List<BankAccount> bankAccountList = bankAccountRepository.findByWeddingOrganizerIdAndDeletedAtIsNull(wo.getId());

            /* GET RESPONSE */
            return getListApiResponse(wo, bankAccountList);

        } catch (ErrorResponse e) {
            log.error("Error while loading own bank accounts: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<BankAccountResponse> findBankAccountById(String id) {
        try {
            /* FIND BANK ACCOUNTS */
            // ErrorResponse //
            BankAccount bankAccount =  findByIdOrThrow(id);

            /* GET RESPONSE */
            BankAccountResponse response = BankAccountResponse.all(bankAccount);
            return ApiResponse.success(response, SMessage.BANK_ACCOUNT_FOUND(bankAccount.getId()));

        }catch (ErrorResponse e) {
            log.error("Error while loading bank accounts by ID: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<BankAccountResponse> createBankAccount(JwtClaim userInfo, BankAccountRequest bankAccountRequest) {
        try {
            /* VALIDATE INPUT */
            // ValidationException //
            validationUtil.validateAndThrow(bankAccountRequest);

            /* LOAD WEDDING ORGANIZER */
            // ErrorResponse //
            WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());

            /* CREATE AND SAVE BANK ACCOUNT */
            BankAccount bankAccount = BankAccount.builder()
                    .weddingOrganizer(wo)
                    .bankCode(bankAccountRequest.getBankCode())
                    .bankName(bankAccountRequest.getBankName())
                    .accountNumber(bankAccountRequest.getAccountNumber())
                    .accountName(bankAccountRequest.getAccountName())
                    .build();
            bankAccount = bankAccountRepository.save(bankAccount);

            /* MAP RESPONSE */
            BankAccountResponse response = BankAccountResponse.all(bankAccount);
            return ApiResponse.success(response, SMessage.BANK_ACCOUNT_CREATED(bankAccount.getId()));

        } catch (ValidationException e) {
            log.error("Validation error while creating bank accounts: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.REGISTER_FAILED, e.getErrors().get(0));
        } catch (ErrorResponse e) {
            log.error("Error while creating bank accounts: {}", e.getError());
            throw e;
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<BankAccountResponse> updateBankAccount(JwtClaim userInfo, BankAccountRequest bankAccountRequest) {
        try {
            /* LOAD BANK ACCOUNT */
            // ErrorResponse //
            BankAccount bankAccount = findByIdOrThrow(bankAccountRequest.getId());

            /* VALIDATE ACCESS */
            // AccessDeniedException //
            accessValidationUtil.validateUser(userInfo, bankAccount.getWeddingOrganizer());

            /* VALIDATE INPUT */
            // ValidationException //
            validationUtil.validateAndThrow(bankAccountRequest);

            /* UPDATE AND SAVE BANK ACCOUNT */
            bankAccount.setBankCode(bankAccountRequest.getBankCode());
            bankAccount.setBankName(bankAccountRequest.getBankName());
            bankAccount.setAccountNumber(bankAccountRequest.getAccountNumber());
            bankAccount.setAccountName(bankAccountRequest.getAccountName());
            bankAccount = bankAccountRepository.save(bankAccount);

            /* MAP RESPONSE */
            BankAccountResponse response = BankAccountResponse.all(bankAccount);
            return ApiResponse.success(response, SMessage.BANK_ACCOUNT_UPDATED(bankAccount.getId()));

        } catch (ValidationException e) {
            log.error("Validation error while updating bank accounts: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.REGISTER_FAILED, e.getErrors().get(0));
        } catch (AccessDeniedException e) {
            log.error("Access denied while updating bank accounts: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while updating bank accounts: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<?> deleteBankAccount(JwtClaim userInfo, String id) {
        try {
            /* LOAD BANK ACCOUNT */
            // ErrorResponse //
            BankAccount bankAccount = findByIdOrThrow(id);

            /* VALIDATE ACCESS */
            // AccessDeniedException //
            accessValidationUtil.validateUser(userInfo, bankAccount.getWeddingOrganizer());

            /* SET DELETED AT AND SAVE BANK ACCOUNT */
            bankAccount.setDeletedAt(LocalDateTime.now());
            bankAccountRepository.save(bankAccount);

            /* MAP RESPONSE */
            return ApiResponse.success(SMessage.BANK_ACCOUNT_DELETED(bankAccount.getId()));

        } catch (AccessDeniedException e) {
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.DELETE_FAILED, e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Integer countBankAccountsByWeddingOrganizerId(String weddingOrganizerId) {
        return bankAccountRepository.countByWeddingOrganizerIdAndDeletedAtIsNull(weddingOrganizerId);
    }
}
