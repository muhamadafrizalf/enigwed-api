package com.enigwed.dto.response;

import com.enigwed.entity.BankAccount;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankAccountResponse {
    private String id;
    private String weddingOrganizerId;
    private String weddingOrganizerName;
    private String bankName;
    private String bankCode;
    private String accountNumber;
    private String accountName;

    public static BankAccountResponse from(BankAccount bankAccount) {
        BankAccountResponse response = new BankAccountResponse();
        response.setId(bankAccount.getId());
        response.setWeddingOrganizerId(bankAccount.getWeddingOrganizer().getId());
        response.setWeddingOrganizerName(bankAccount.getWeddingOrganizer().getName());
        response.setBankName(bankAccount.getBankName());
        response.setBankCode(bankAccount.getBankCode());
        response.setAccountNumber(bankAccount.getAccountNumber());
        response.setAccountName(bankAccount.getAccountName());
        return response;
    }
}
