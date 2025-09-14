package com.smartinventory.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "active = true")
@EntityListeners(AuditingEntityListener.class) //  Enables auditing
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Product name cannot be blank")
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    @NotBlank(message = "SKU is required")
    private String sku; // unique stock keeping unit

    @Column(nullable = false, precision = 10, scale = 2)
    @DecimalMin(value = "0.00", message = "Price must be >= 0")
    private BigDecimal price;

    @Column(nullable = false)
    @Min(value = 0, message = "Quantity must be >= 0")
    private BigDecimal quantity=BigDecimal.ZERO;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UnitOfMeasure uom; // e.g. PCS, KG, LITER

    //  Auditing fields
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Column(nullable = false)
    private boolean active = true;  // Default: active

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    // optimistic locking for quantity updates
    @Version
    private Long version;

}
