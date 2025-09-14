package com.smartinventory.inventory.repository;

import com.smartinventory.inventory.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByProductId(Long productId);
    List<StockMovement> findByReferenceTypeAndReferenceId(String referenceType, Long referenceId);
}
