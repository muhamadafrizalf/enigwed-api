package com.enigwed.dto.response;

import com.enigwed.entity.WeddingOrganizer;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatisticResponse {
    WeddingOrganizerResponse weddingOrganizer;
    private Map<String, Double> income;

    public static StatisticResponse from(WeddingOrganizer weddingOrganizer, Map<String, Double> income) {
        StatisticResponse response = new StatisticResponse();
        response.setIncome(income);
        response.setWeddingOrganizer(WeddingOrganizerResponse.simple(weddingOrganizer));
        return response;
    }
}
