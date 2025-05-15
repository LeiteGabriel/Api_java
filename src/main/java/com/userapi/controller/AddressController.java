package com.userapi.controller;

import com.userapi.dto.AddressDTO;
import com.userapi.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AddressDTO> create(@RequestBody @Valid AddressDTO addressDTO) {
        return ResponseEntity.ok(addressService.create(addressDTO));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<AddressDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(addressService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@userSecurity.canAccessAddress(#id)")
    public ResponseEntity<AddressDTO> findById(@PathVariable Long id) {
        return addressService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("@userSecurity.canAccessAddress(#id)")
    public ResponseEntity<AddressDTO> update(@PathVariable Long id, @RequestBody @Valid AddressDTO addressDTO) {
        return ResponseEntity.ok(addressService.update(id, addressDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@userSecurity.canAccessAddress(#id)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        addressService.delete(id);
        return ResponseEntity.ok().build();
    }
}
