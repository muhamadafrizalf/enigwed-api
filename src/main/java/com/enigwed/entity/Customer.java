package com.enigwed.entity;

import com.enigwed.constant.SPathDb;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = SPathDb.CUSTOMER)
public class Customer extends BaseEntity {

    private String name;

    private String phone;

    private String email;

    @Column(length = 1000)
    private String address;
}
