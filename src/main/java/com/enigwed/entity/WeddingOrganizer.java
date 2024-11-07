package com.enigwed.entity;

import com.enigwed.constant.PathDb;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cascade;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = PathDb.WEDDING_ORGANIZER)
public class WeddingOrganizer extends AuditEntity{

    private String name;

    private String npwp;

    private String nib;

    private String phone;

    @Column(length = 1000)
    private String description;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "province_id")
    private Province province;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "regency_id")
    private Regency regency;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "district_id")
    private District district;

    @Column(length = 1000)
    private String address;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "avatar_id")
    private Image avatar;

    @OneToMany(mappedBy = "weddingOrganizer")
    List<WeddingPackage> weddingPackages = new ArrayList<>();

    @OneToMany(mappedBy = "weddingOrganizer")
    List<Review> reviews = new ArrayList<>();

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_credential_id")
    private UserCredential userCredential;
}
