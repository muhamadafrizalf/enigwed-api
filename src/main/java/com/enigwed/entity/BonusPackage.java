package com.enigwed.entity;

import com.enigwed.constant.PathDb;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = PathDb.BONUS_PACKAGE)
public class BonusPackage extends AuditEntity{

    private String name;

    @Column(length = 1000)
    private String description;

    private double price;

    @Column(name = "min_quantity")
    private int minQuantity;

    @Column(name = "max_quantity")
    private int maxQuantity;

    @ManyToOne
    @JoinColumn(name = "wedding_organizer_id")
    private WeddingOrganizer weddingOrganizer;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "bonus_package_image",
            joinColumns = @JoinColumn(name = "bonus_package_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    private List<Image> images;
}
