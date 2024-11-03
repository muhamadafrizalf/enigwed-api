package com.enigwed.entity;

import com.enigwed.constant.EStatus;
import com.enigwed.constant.PathDb;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = PathDb.ORDER)
public class Order extends BaseEntity{

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "book_code", unique = true, nullable = false)
    private String bookCode;

    @Column(name = "wedding_date")
    private LocalDateTime weddingDate;

    @Column(name = "wedding_package_base_price")
    private double weddingPackageBasePrice;

    private EStatus status;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "wedding_organizer_id")
    private WeddingOrganizer weddingOrganizer;

    @ManyToOne
    @JoinColumn(name = "wedding_package_id")
    private WeddingPackage weddingPackage;

    @ManyToOne
    @JoinColumn(name = "payment_image_id")
    private Image paymentImage;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        transactionDate = LocalDateTime.now();
        status = EStatus.PENDING;
    }
}
