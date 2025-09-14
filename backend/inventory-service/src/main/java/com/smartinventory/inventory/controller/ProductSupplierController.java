package com.smartinventory.inventory.controller;

import com.smartinventory.inventory.dto.ProductSupplierRequestDTO;
import com.smartinventory.inventory.dto.ProductSupplierResponseDTO;
import com.smartinventory.inventory.service.ProductSupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-supplier")
public class ProductSupplierController {
    @Autowired
    private ProductSupplierService productSupplierService;
    @PostMapping
    public ResponseEntity<ProductSupplierResponseDTO> addProductSupplier(@RequestBody ProductSupplierRequestDTO productSupplierRequestDTO){
        return ResponseEntity.ok(productSupplierService.addProductSupplier(productSupplierRequestDTO));

    }
    @GetMapping("/product/{id}")
    public ResponseEntity<List<ProductSupplierResponseDTO>> getSuppliersForProduct(@PathVariable Long id){
        return ResponseEntity.ok(productSupplierService.getSuppliersForProduct(id));
    }
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<ProductSupplierResponseDTO>> getProductsForSupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(productSupplierService.getProductsForSupplier(supplierId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductSupplierResponseDTO> updateProductSupplier(@PathVariable Long id,
                                                                            @RequestBody ProductSupplierRequestDTO dto) {
        return ResponseEntity.ok(productSupplierService.updateProductSupplier(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductSupplier(@PathVariable Long id) {
        productSupplierService.deleteProductSupplier(id);
        return ResponseEntity.noContent().build();
    }

}
