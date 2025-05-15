package com.userapi.controller;

import com.userapi.dto.AuthRequestDTO;
import com.userapi.dto.AuthResponseDTO;
import com.userapi.dto.UserDTO;
import com.userapi.security.JwtProvider;
import com.userapi.service.UserService;
import jakarta.servlet.http.HttpSession; // Adicionado
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder; // Adicionado
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserService userService;

    // Removida a injeção de HttpSession do construtor para usar como parâmetro de método
    public AuthController(AuthenticationManager authenticationManager, JwtProvider jwtProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request, HttpSession session) { // HttpSession injetado
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Define a autenticação no contexto de segurança atual
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Garante que a sessão seja criada e a autenticação seja armazenada nela
        // O Spring Security geralmente faz isso automaticamente com HttpSessionSecurityContextRepository
        // quando SecurityContextHolder é populado e a política de sessão é IF_REQUIRED/ALWAYS.
        // Adicionar um atributo pode forçar a criação da sessão se ela ainda não existir.
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        String token = jwtProvider.generateToken(authentication);
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.create(userDTO));
    }
}
