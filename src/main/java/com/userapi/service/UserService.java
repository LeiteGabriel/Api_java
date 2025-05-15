package com.userapi.service;

import com.userapi.dto.UserDTO;
import com.userapi.entity.User;
import com.userapi.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO create(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        User user = modelMapper.map(userDTO, User.class);

        if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
            throw new RuntimeException("A senha é obrigatória");
        }
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user = userRepository.save(user);
        
        UserDTO responseDTO = modelMapper.map(user, UserDTO.class);
        responseDTO.setPassword(null); // Nunca retorna a senha
        return responseDTO;
    }

    public UserDTO update(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Optional<User> existingEmail = userRepository.findByEmail(userDTO.getEmail());
        if (existingEmail.isPresent() && !existingEmail.get().getId().equals(id)) {
            throw new RuntimeException("Email já cadastrado");
        }

        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        user = userRepository.save(user);
        UserDTO responseDTO = modelMapper.map(user, UserDTO.class);
        responseDTO.setPassword(null);
        return responseDTO;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
