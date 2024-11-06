package com.enigwed.dto.request;

import com.enigwed.constant.EStatus;
import com.enigwed.constant.EUserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterRequest {
    private String weddingOrganizerId;
    private EUserStatus userStatus;
    private String provinceId;
    private String regencyId;
    private String districtId;
    private Double minPrice;
    private Double maxPrice;
    private String weddingPackageId;
    private EStatus orderStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
