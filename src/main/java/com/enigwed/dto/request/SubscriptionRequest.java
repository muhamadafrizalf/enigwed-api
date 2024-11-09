package com.enigwed.dto.request;

import com.enigwed.constant.SConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import static com.enigwed.constant.SConstraint.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionRequest {

    @NotBlank(message = SUBSCRIPTION_PRICE_ID_BLANK)
    private String subscriptionPriceId;

    @NotNull(message = PAYMENT_IMAGE_NULL)
    private MultipartFile paymentImage;

    @Email(message = EMAIL_INVALID)
    private String email;
}
