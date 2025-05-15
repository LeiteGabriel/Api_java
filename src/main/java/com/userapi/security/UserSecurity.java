package com.userapi.security;

import com.userapi.entity.User;
import com.userapi.repository.AddressRepository;
import com.userapi.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserSecurity {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public UserSecurity(UserRepository userRepository, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    public boolean canAccessUser(Long userId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(currentUserEmail)
                .map(user -> user.getId().equals(userId))
                .orElse(Boolean.FALSE);
    }

    public boolean canAccessAddress(Long addressId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userRepository.findByEmail(currentUserEmail)
                .map(user -> user.getId())
                .orElse(null);

        if (userId == null) return false;

        return addressRepository.findById(addressId)
                .map(address -> address.getUser().getId().equals(userId))
                .orElse(false);
    }
}
