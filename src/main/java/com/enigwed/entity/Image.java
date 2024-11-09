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
@Table(name = SPathDb.IMAGE)
public class Image extends BaseEntity{

    @Column
    String name;

    @Column
    String path;

    @Column(name = "conten_type")
    String contentType;

    @Column
    Long size;
}
