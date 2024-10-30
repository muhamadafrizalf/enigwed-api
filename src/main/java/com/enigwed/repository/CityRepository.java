package com.enigwed.repository;

import com.enigwed.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, String> {
    Optional<City> findByIdAndDeletedAtIsNull(String id);
    Optional<City> findByNameAndDeletedAtIsNull(String name);
    List<City> findByDeletedAtIsNull();
}
