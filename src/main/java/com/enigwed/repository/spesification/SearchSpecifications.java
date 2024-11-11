package com.enigwed.repository.spesification;

import com.enigwed.entity.Order;
import com.enigwed.entity.Product;
import com.enigwed.entity.WeddingOrganizer;
import com.enigwed.entity.WeddingPackage;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class SearchSpecifications {

    public static Specification<Order> searchOrder(String keyword) {
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

    public static Specification<WeddingPackage> searchWeddingPackage(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(keyword)) {
                String likePattern = "%" + keyword.toLowerCase() + "%";

                return criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("province").get("name")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("regency").get("name")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("weddingOrganizer").get("name")), likePattern)
                );
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<WeddingOrganizer> searchWeddingOrganizer(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(keyword)) {
                String likePattern = "%" + keyword.toLowerCase() + "%";

                return criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("province").get("name")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("regency").get("name")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("district").get("name")), likePattern)
                );
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Product> searchProduct(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(keyword)) {
                String likePattern = "%" + keyword.toLowerCase() + "%";

                return criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern)
                );
            }
            return criteriaBuilder.conjunction();
        };
    }

}
