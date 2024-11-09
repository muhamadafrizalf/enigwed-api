package com.enigwed.dto.request;

import com.enigwed.constant.SConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.enigwed.constant.SConstraint.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccountRequest {

    private String id;

    @Pattern(regexp = "^[0-9]+$", message = BANK_CODE_INVALID)
    @NotBlank(message = BANK_CODE_BLANK)
    private String bankCode;

    @NotBlank(message = BANK_NAME_BLANK)
    private String bankName;

    @Pattern(regexp = "^[0-9]+$", message = SConstraint.ACCOUNT_NUMBER_INVALID)
    @NotBlank(message = ACCOUNT_NUMBER_BLANK)
    private String accountNumber;

    @NotBlank(message = ACCOUNT_NAME_BLANK)
    private String accountName;
}
