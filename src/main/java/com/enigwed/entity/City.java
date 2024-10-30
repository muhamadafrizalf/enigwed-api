package com.enigwed.entity;

import com.enigwed.constant.PathDb;
import jakarta.persistence.*;
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

    @Column(length = 1000)
    private String description;

    @OneToOne
    @JoinColumn(name = "thumbnail_id")
    Image thumbnail;
}
