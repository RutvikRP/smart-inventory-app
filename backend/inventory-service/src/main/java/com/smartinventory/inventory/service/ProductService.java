package com.smartinventory.inventory.service;

import com.smartinventory.inventory.dto.ProductRequestDTO;
import com.smartinventory.inventory.dto.ProductResponseDTO;
import com.smartinventory.inventory.entity.Product;
import com.smartinventory.inventory.entity.UnitOfMeasure;
import com.smartinventory.inventory.exception.DuplicateSkuException;
import com.smartinventory.inventory.exception.ProductNotFoundException;
import com.smartinventory.inventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public ProductResponseDTO addProduct(ProductRequestDTO productRequestDTO) {
        if (productRepository.existsBySku(productRequestDTO.sku())) {
            throw new DuplicateSkuException("SKU " + productRequestDTO.sku() + " already exists");
        }
        Product product = mapToEntity(productRequestDTO);
        return mapToResponseDTO(productRepository.save(product));
    }

    public ProductResponseDTO getProductById(long id) {
        return productRepository.findById(id).map(this::mapToResponseDTO).orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
    }

    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    public List<ProductResponseDTO> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword).stream().map(this::mapToResponseDTO).toList();
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id " + id));

        product.setActive(false);
        product.setDeletedAt(LocalDateTime.now());

        productRepository.save(product);
    }


    public ProductResponseDTO updateProduct(long id,ProductRequestDTO productRequestDTO) {
        Product product = productRepository.findById(id).orElseThrow(()->new ProductNotFoundException("Product with id " + id + " not found"));
        if (!product.getSku().equals(productRequestDTO.sku()) && productRepository.existsBySku(productRequestDTO.sku())) {
            throw new DuplicateSkuException("SKU " + productRequestDTO.sku() + " already exists");
        }
        product.setName(productRequestDTO.name());
        product.setDescription(productRequestDTO.description());
        product.setPrice(productRequestDTO.price());
        product.setQuantity(productRequestDTO.quantity());
        product.setSku(productRequestDTO.sku());
        product.setUom(UnitOfMeasure.valueOf(productRequestDTO.uom()));
        return mapToResponseDTO(productRepository.save(product));
    }

    public Product mapToEntity(ProductRequestDTO productRequestDTO) {
        Product product = new Product();
        product.setName(productRequestDTO.name());
        product.setPrice(productRequestDTO.price());
        product.setDescription(productRequestDTO.description());
        product.setQuantity(productRequestDTO.quantity());
        product.setSku(productRequestDTO.sku());
        product.setUom(UnitOfMeasure.valueOf(productRequestDTO.uom()));
        return product;
    }

    public ProductResponseDTO mapToResponseDTO(Product product) {
        return new ProductResponseDTO(product.getId(),
                product.getName(),
                product.getQuantity(),
                product.getPrice(),
                product.getSku(),
                product.getDescription(),
                product.getUom().name(),
                product.getCreatedAt(),
                product.getUpdatedAt());
    }
    public void restoreProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id " + id));

        product.setActive(true);
        product.setDeletedAt(null);

        productRepository.save(product);
    }

}
