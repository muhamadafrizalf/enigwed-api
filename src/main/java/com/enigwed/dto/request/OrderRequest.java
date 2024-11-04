package com.enigwed.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.enigwed.constant.Constraint.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {

    private CustomerRequest customer;

    @NotNull(message = WEDDING_DATE_NULL)
    @Future(message = WEDDING_DATE_FUTURE)
    private LocalDateTime weddingDate;

    @NotBlank(message = WEDDING_PACKAGE_ID_BLANK)
    private String weddingPackageId;

    private List<OrderDetailRequest> orderDetails;
}
