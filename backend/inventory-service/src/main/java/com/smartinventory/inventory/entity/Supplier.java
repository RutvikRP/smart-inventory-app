package com.smartinventory.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "supplier")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Supplier name is required")
    @Size(min = 2, max = 100, message = "Supplier name must be between 2 and 100 characters")
    private String name;

    @Column(name = "email", unique = true)
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Column(name = "phone", nullable = false, unique = true)
    private String phone;
//    private PurchaseOrder purchaseOrder;

    @NotBlank(message = "Address is required")
    private String address;
    @Column(nullable = false)
    private boolean active = true;  // Default: active

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}
