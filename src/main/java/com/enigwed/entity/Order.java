package com.enigwed.entity;

import com.enigwed.constant.EStatus;
import com.enigwed.constant.SPathDb;
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
@Table(name = SPathDb.ORDER)
public class Order extends BaseEntity{

    @Column(name = "book_code", unique = true, nullable = false)
    private String bookCode;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private EStatus status;

    @Column(name = "transaction_finish_date")
    private LocalDateTime transactionFinishDate;

    @Column(name = "wedding_date")
    private LocalDateTime weddingDate;

    @Column(name = "base_price")
    private double basePrice;

    @Column(name = "total_price")
    private double totalPrice;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "review_id")
    private Review review;

    private boolean reviewed;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "payment_image_id")
    private Image paymentImage;

    @ManyToOne
    @JoinColumn(name = "wedding_organizer_id")
    private WeddingOrganizer weddingOrganizer;

    @ManyToOne
    @JoinColumn(name = "wedding_package_id")
    private WeddingPackage weddingPackage;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        transactionDate = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        status = EStatus.PENDING;
        reviewed = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
