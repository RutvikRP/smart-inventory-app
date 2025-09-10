package com.smartinventory.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "purchase_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false,unique = true)
    private String orderNumber;
    @ManyToOne
    @JoinColumn(name = "supplier_id",referencedColumnName = "id",nullable = false)
    private Supplier supplier;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus=OrderStatus.DRAFT;
    @NotNull
    private LocalDateTime orderDate;
    private LocalDateTime expectedDate;
    @Column(precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "purchaseOrder" ,cascade = CascadeType.ALL , orphanRemoval = true)
    private List<PurchaseOrderLine> purchaseOrderLineList=new ArrayList<>();

}
