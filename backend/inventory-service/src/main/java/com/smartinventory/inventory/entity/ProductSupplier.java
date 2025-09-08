package com.smartinventory.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product_supplier", uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "supplier_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSupplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;
    @DecimalMin(value = "0.00", message = "Price must be >= 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal supplierPrice;
    @Min(value = 0, message = "Lead time must be >= 0")
    @Column(nullable = false)
    private Integer leadTimeDays; // e.g., delivery time from supplier
    private Boolean preferredSupplier = false;

}
