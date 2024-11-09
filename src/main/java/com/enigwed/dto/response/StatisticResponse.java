package com.enigwed.dto.response;

import com.enigwed.entity.WeddingOrganizer;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatisticResponse {
    private Map<String, Double> income;
    private WeddingOrganizerResponse weddingOrganizer;
    private Map<String, Integer> countByStatus;
    private List<WeddingOrganizerResponse> weddingOrganizerList;

    public static StatisticResponse wo(WeddingOrganizer weddingOrganizer, Map<String, Double> income) {
        StatisticResponse response = new StatisticResponse();
        response.setIncome(income);
        response.setWeddingOrganizer(WeddingOrganizerResponse.simple(weddingOrganizer));
        return response;
    }

    public static StatisticResponse admin(List<WeddingOrganizer> woList, Map<String, Integer> countByStatus, Map<String, Double> income) {
        StatisticResponse response = new StatisticResponse();
        response.setWeddingOrganizerList(woList.stream().map(WeddingOrganizerResponse::simpleAdmin).toList());
        response.setCountByStatus(countByStatus);
        response.setIncome(income);
        return response;
    }
}
