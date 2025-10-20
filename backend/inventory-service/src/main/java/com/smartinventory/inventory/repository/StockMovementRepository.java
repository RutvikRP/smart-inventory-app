package com.smartinventory.inventory.repository;

import com.smartinventory.inventory.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByProductId(Long productId);
    List<StockMovement> findByReferenceTypeAndReferenceId(String referenceType, Long referenceId);
}
