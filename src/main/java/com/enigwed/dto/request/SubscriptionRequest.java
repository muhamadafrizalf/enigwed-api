package com.enigwed.dto.request;

import com.enigwed.constant.Constraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionRequest {

    @NotBlank(message = Constraint.SUBSCRIPTION_PRICE_ID_BLANK)
    private String subscriptionPriceId;

    @Email(message = Constraint.EMAIL_VALID)
    private String email;

    @NotNull(message = Constraint.PAYMENT_IMAGE_NULL)
    private MultipartFile paymentImage;
}
