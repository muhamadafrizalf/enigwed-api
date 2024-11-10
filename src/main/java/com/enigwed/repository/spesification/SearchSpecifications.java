package com.enigwed.repository.spesification;

import com.enigwed.entity.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class SearchSpecifications {

    public static Specification<Order> searchByKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(keyword)) {
                String likePattern = "%" + keyword.toLowerCase() + "%";

                return criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("bookCode")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("customer").get("name")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("customer").get("phone")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("customer").get("email")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("weddingOrganizer").get("name")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("weddingPackage").get("name")), likePattern)
                );
            }
            return criteriaBuilder.conjunction();
        };
    }
}
