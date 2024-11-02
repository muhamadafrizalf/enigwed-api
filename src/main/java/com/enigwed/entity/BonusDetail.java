package com.enigwed.entity;

import com.enigwed.constant.PathDb;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = PathDb.BONUS_DETAIL)
public class BonusDetail extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "wedding_package_id")
    private WeddingPackage weddingPackage;

    @ManyToOne
    @JoinColumn(name = "bonus_package_id")
    private BonusPackage bonusPackage;

    private int quantity;

    private boolean adjustable;


}
