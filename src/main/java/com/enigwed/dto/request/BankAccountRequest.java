package com.enigwed.dto.request;

import com.enigwed.constant.Constraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccountRequest {

    private String id;

    @Pattern(regexp = "^[0-9]+$", message = Constraint.INVALID_BANK_CODE)
    private String bankCode;

    @NotBlank(message = Constraint.ACCOUNT_NUMBER_BLANK)
    @Pattern(regexp = "^[0-9]+$", message = Constraint.INVALID_ACCOUNT_NUMBER)
    private String accountNumber;

    @NotBlank(message = Constraint.BANK_NAME_BLANK)
    private String bankName;
}
