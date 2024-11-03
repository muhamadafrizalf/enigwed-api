package com.enigwed.entity;

import com.enigwed.constant.PathDb;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = PathDb.WEDDING_PACKAGE)
public class WeddingPackage extends AuditEntity {

    private String name;

    @Column(length = 10000)
    private String description;

    @Column(name = "base_price")
    private double basePrice;

    @Column(name = "order_count")
    private int orderCount = 0;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "wedding_organizer_id")
    private WeddingOrganizer weddingOrganizer;

    @OneToMany(mappedBy = "weddingPackage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BonusDetail> bonusDetails = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "wedding_package_image",
            joinColumns = @JoinColumn(name = "wedding_package_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    private List<Image> images = new ArrayList<>();
}