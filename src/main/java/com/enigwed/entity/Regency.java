package com.enigwed.entity;

import com.enigwed.constant.SPathDb;
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
@Table(name = SPathDb.REGENCY)
public class Regency {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "province_id")
    private Province province;

    private String name;
}
