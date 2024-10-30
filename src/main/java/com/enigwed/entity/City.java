package com.enigwed.entity;

import com.enigwed.constant.PathDb;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = PathDb.CITY)
public class City extends AuditEntity{

    private String name;

    private String description;

    @OneToOne
    @JoinColumn(name = "thumbnail_id")
    Image thumbnail;
}
