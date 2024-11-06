package com.enigwed.entity;

import com.enigwed.constant.PathDb;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = PathDb.DISTRICT)
public class District {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "regency_id")
    private Regency regency;

    private String name;
}
