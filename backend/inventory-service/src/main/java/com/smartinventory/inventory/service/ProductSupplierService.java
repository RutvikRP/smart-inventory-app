package com.smartinventory.inventory.service;

import com.smartinventory.inventory.dto.ProductSupplierRequestDTO;
import com.smartinventory.inventory.dto.ProductSupplierResponseDTO;
import com.smartinventory.inventory.entity.Product;
import com.smartinventory.inventory.entity.ProductSupplier;
import com.smartinventory.inventory.entity.Supplier;
import com.smartinventory.inventory.exception.ProductNotFoundException;
import com.smartinventory.inventory.exception.SupplierNotFoundException;
import com.smartinventory.inventory.repository.ProductRepository;
import com.smartinventory.inventory.repository.ProductSupplierRepository;
import com.smartinventory.inventory.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductSupplierService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private ProductSupplierRepository productSupplierRepository;

    public ProductSupplierResponseDTO addProductSupplier(ProductSupplierRequestDTO productSupplierRequestDTO) {
        Product product = productRepository.findById(productSupplierRequestDTO.productId()).orElseThrow(() -> new ProductNotFoundException("Product not found"));
        Supplier supplier = supplierRepository.findById(productSupplierRequestDTO.supplierId()).orElseThrow(() -> new SupplierNotFoundException("Supplier not found"));
        ProductSupplier productSupplier = ProductSupplier.builder().product(product).supplier(supplier).supplierPrice(productSupplierRequestDTO.supplierPrice()).leadTimeDays(productSupplierRequestDTO.leadTimeDays()).preferredSupplier(productSupplierRequestDTO.preferredSupplier()).build();
        ProductSupplier saved = productSupplierRepository.save(productSupplier);
        return mapToDTO(saved);
    }

    private ProductSupplierResponseDTO mapToDTO(ProductSupplier ps) {
        return new ProductSupplierResponseDTO(ps.getId(), ps.getProduct().getId(), ps.getProduct().getName(), ps.getSupplier().getId(), ps.getSupplier().getName(), ps.getSupplierPrice(), ps.getLeadTimeDays(), ps.getPreferredSupplier());
    }

    public List<ProductSupplierResponseDTO> getSuppliersForProduct(Long id) {
        return productSupplierRepository.findByProductId(id).stream().map(this::mapToDTO).toList();
    }
}
