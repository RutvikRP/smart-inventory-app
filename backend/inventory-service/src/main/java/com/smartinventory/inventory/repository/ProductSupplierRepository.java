package com.smartinventory.inventory.repository;

import com.smartinventory.inventory.entity.Product;
import com.smartinventory.inventory.entity.ProductSupplier;
import com.smartinventory.inventory.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSupplierRepository extends JpaRepository<ProductSupplier,Long> {
    List<ProductSupplier> findByProductId(Long productId);
    List<ProductSupplier> findBySupplierId(Long supplierId);
}