package com.enigwed.dto.request;

import com.enigwed.constant.Constraint;
import com.enigwed.constant.ESubscriptionLength;
import jakarta.validation.constraints.Email;
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

    @NotNull(message = Constraint.SUBSCRIPTION_LENGTH_NULL)
    private ESubscriptionLength subscriptionLength;

    @Email(message = Constraint.EMAIL_VALID)
    private String email;

    @NotNull(message = Constraint.PAYMENT_IMAGE_NULL)
    private MultipartFile paymentImage;
}
