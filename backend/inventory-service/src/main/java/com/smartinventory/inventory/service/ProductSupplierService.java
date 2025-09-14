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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductSupplierService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private ProductSupplierRepository productSupplierRepository;

    @Transactional
    public ProductSupplierResponseDTO addProductSupplier(ProductSupplierRequestDTO productSupplierRequestDTO) {
        Product product = productRepository.findById(productSupplierRequestDTO.productId()).orElseThrow(() -> new ProductNotFoundException("Product not found"));
        Supplier supplier = supplierRepository.findById(productSupplierRequestDTO.supplierId()).orElseThrow(() -> new SupplierNotFoundException("Supplier not found"));
        if (productSupplierRepository.findByProductIdAndSupplierId(productSupplierRequestDTO.productId(), productSupplierRequestDTO.supplierId()).isPresent()) {
            throw new IllegalArgumentException("Product already mapped with supplier");
        }
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

    public List<ProductSupplierResponseDTO> getProductsForSupplier(Long supplierId) {
        return productSupplierRepository.findBySupplierId(supplierId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public ProductSupplierResponseDTO updateProductSupplier(Long id, ProductSupplierRequestDTO dto) {
        ProductSupplier ps = productSupplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mapping not found with id " + id));

        if (dto.supplierPrice() != null) ps.setSupplierPrice(dto.supplierPrice());
        if (dto.leadTimeDays() != null) ps.setLeadTimeDays(dto.leadTimeDays());
        if (dto.preferredSupplier() != null) ps.setPreferredSupplier(dto.preferredSupplier());

        return mapToDTO(productSupplierRepository.save(ps));
    }

    @Transactional
    public void deleteProductSupplier(Long id) {
        if (!productSupplierRepository.existsById(id)) {
            throw new IllegalArgumentException("Mapping not found with id " + id);
        }
        productSupplierRepository.deleteById(id);
    }
}
