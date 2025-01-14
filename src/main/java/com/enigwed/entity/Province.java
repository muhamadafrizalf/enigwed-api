package com.enigwed.entity;

import com.enigwed.constant.SPathDb;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = SPathDb.PROVINCE)
public class Province {

    @Id
    private String id;

    private String name;
}
