package com.enigwed.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionResponse {
    private String id;
    private WeddingOrganizerResponse weddingOrganizer;
    private ImageResponse paymentImage;
    private Boolean accepted;
}
