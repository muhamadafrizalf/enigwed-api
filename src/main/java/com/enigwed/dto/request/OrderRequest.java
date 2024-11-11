package com.enigwed.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.enigwed.constant.SConstraint.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {

    @Future(message = WEDDING_DATE_INVALID)
    @NotNull(message = WEDDING_DATE_NULL)
    private LocalDateTime weddingDate;

    @NotBlank(message = WEDDING_PACKAGE_ID_BLANK)
    private String weddingPackageId;

    @Valid
    @NotNull(message = CUSTOMER_NULL)
    private CustomerRequest customer;

    @Valid
    private List<AdditionalProduct> additionalProducts;
}
