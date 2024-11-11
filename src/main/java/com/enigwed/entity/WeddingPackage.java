package com.enigwed.entity;

import com.enigwed.constant.SPathDb;
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
@Table(name = SPathDb.WEDDING_PACKAGE)
public class WeddingPackage extends AuditEntity {

    private String name;

    @Column(length = 10000)
    private String description;

    private double price;

    @Column(name = "order_count")
    private int orderCount = 0;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "province_id")
    private Province province;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "regency_id")
    private Regency regency;

    @ManyToOne
    @JoinColumn(name = "wedding_organizer_id")
    private WeddingOrganizer weddingOrganizer;

    @OneToMany(mappedBy = "weddingPackage")
    List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "weddingPackage", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<BonusDetail> bonusDetails = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "wedding_package_image",
            joinColumns = @JoinColumn(name = "wedding_package_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    private List<Image> images = new ArrayList<>();
}
