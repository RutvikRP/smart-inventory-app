package com.smartinventory.inventory.service;

import com.smartinventory.inventory.dto.SupplierRequestDTO;
import com.smartinventory.inventory.dto.SupplierResponseDTO;
import com.smartinventory.inventory.entity.Product;
import com.smartinventory.inventory.entity.Supplier;
import com.smartinventory.inventory.exception.DuplicateSupplierException;
import com.smartinventory.inventory.exception.ProductNotFoundException;
import com.smartinventory.inventory.exception.SupplierNotFoundException;
import com.smartinventory.inventory.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SupplierService {
    @Autowired
    private SupplierRepository supplierRepository;

    public SupplierResponseDTO addSupplier(SupplierRequestDTO supplierRequestDTO) {
        if (supplierRepository.findByEmail(supplierRequestDTO.email()).isPresent()) {
            throw new DuplicateSupplierException("Duplicate Supplier found with email :" + supplierRequestDTO.email());
        }
        Supplier supplier = supplierRepository.save(mapToEntity(supplierRequestDTO));
        return entityToDto(supplier);
    }

    private SupplierResponseDTO entityToDto(Supplier supplier) {
        return new SupplierResponseDTO(supplier.getId(),
                supplier.getName(),
                supplier.getEmail(),
                supplier.getPhone(),
                supplier.getAddress());
    }

    private Supplier mapToEntity(SupplierRequestDTO supplierRequestDTO) {
        Supplier supplier = new Supplier();
        supplier.setName(supplierRequestDTO.name());
        supplier.setEmail(supplierRequestDTO.email());
        supplier.setPhone(supplierRequestDTO.phone());
        supplier.setAddress(supplierRequestDTO.address());
        return supplier;
    }

    public List<SupplierResponseDTO> getAllSupplier(Pageable pageable) {
        return supplierRepository.findAll(pageable).map(this::entityToDto).stream().toList();
    }

    public SupplierResponseDTO getSupplierById(Long id) {
        return supplierRepository.findById(id).map(this::entityToDto).orElseThrow(() -> new SupplierNotFoundException("Supplier with id " + id + " is not found"));
    }

    public SupplierResponseDTO updateSupplier(long id, SupplierRequestDTO supplierRequestDTO) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " is not found"));
        supplier.setName(supplierRequestDTO.name());
        supplier.setEmail(supplierRequestDTO.email());
        supplier.setPhone(supplierRequestDTO.phone());
        supplier.setAddress(supplierRequestDTO.address());
        Supplier saved=supplierRepository.save(supplier);
        return entityToDto(saved);
    }

    public SupplierResponseDTO updateSomeField(Long id, Map<String, String> updateMap) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> new SupplierNotFoundException("Supplier with id " + id + " is not found"));

        updateMap.forEach((k, v) -> {
                    switch (k) {
                        case "name":
                            supplier.setName(v);
                            break;
                        case "email":
                            supplier.setEmail(v);
                            break;
                        case "phone":
                            supplier.setPhone(v);
                            break;
                        case "address":
                            supplier.setAddress(v);
                            break;
                        default:
                            throw new IllegalArgumentException("Field " + k + " not supported");

                    }
                }
        );
        return entityToDto(supplierRepository.save(supplier));
    }

    public void deleteSupplier(long id) {
        supplierRepository.findById(id).orElseThrow(()->new SupplierNotFoundException("Supplier not found for id : "+id));
        supplierRepository.deleteById(id);
    }
}
