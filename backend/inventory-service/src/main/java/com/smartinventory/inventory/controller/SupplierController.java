package com.smartinventory.inventory.controller;

import com.smartinventory.inventory.dto.SupplierRequestDTO;
import com.smartinventory.inventory.dto.SupplierResponseDTO;
import com.smartinventory.inventory.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/supplier")
public class SupplierController {
    @Autowired
    private SupplierService supplierService;
    @PostMapping
    public ResponseEntity<SupplierResponseDTO> addSupplier(@Valid @RequestBody SupplierRequestDTO supplierRequestDTO){
        return ResponseEntity.ok(supplierService.addSupplier(supplierRequestDTO));
    }
    @GetMapping
    public ResponseEntity<List<SupplierResponseDTO>> getAllSupplier(Pageable pageable){
        return ResponseEntity.ok(supplierService.getAllSupplier(pageable));
    }
    @GetMapping("/{id}")
    public  ResponseEntity<SupplierResponseDTO> getSupplierById(@PathVariable Long id){
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> updateSupplier(@PathVariable long id,@Valid @RequestBody SupplierRequestDTO supplierRequestDTO){
        return ResponseEntity.ok(supplierService.updateSupplier(id,supplierRequestDTO));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> updateSomeField(@PathVariable Long id, @RequestBody Map<String,String> updateMap){
        SupplierResponseDTO supplierResponseDTO=supplierService.updateSomeField(id,updateMap);
        return  ResponseEntity.ok(supplierResponseDTO);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable long id){
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}
