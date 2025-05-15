package com.userapi.controller;

import com.userapi.entity.User;
import com.userapi.dto.UserDTO;
import com.userapi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@Controller
public class WebController {

    private static final Logger log = LoggerFactory.getLogger(WebController.class);
    private final UserService userService;

    public WebController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        log.info("Accessing dashboard for user: {}", userDetails.getUsername());
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> {
                    log.error("User not found: {}", userDetails.getUsername());
                    return new RuntimeException("Usuário não encontrado");
                });
            
            log.info("Found user: {}", user.getName());
            
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setName(user.getName());
            userDTO.setEmail(user.getEmail());
            
            model.addAttribute("user", userDTO);
            return "dashboard";
        } catch (Exception e) {
            log.error("Error loading dashboard: ", e);
            model.addAttribute("error", "Erro ao carregar o painel: " + e.getMessage());
            return "redirect:/login?error";
        }
    }

    @GetMapping("/endereco")
public String enderecos() {
    return "enderecos";
}

    @PostMapping("/api/profile/update")
    @ResponseBody
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Validated Map<String, String> updates) {
        log.info("Updating profile for user: {}", userDetails.getUsername());
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> {
                    log.error("User not found during profile update: {}", userDetails.getUsername());
                    return new RuntimeException("Usuário não encontrado");
                });

            String newName = updates.get("name");
            if (newName == null || newName.trim().isEmpty()) {
                log.error("Invalid name provided for update: {}", newName);
                return ResponseEntity.badRequest().body(Map.of(
                    "message", "Nome não pode estar vazio"
                ));
            }

            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setName(newName.trim());
            userDTO.setEmail(user.getEmail());
            
            UserDTO updatedUser = userService.update(user.getId(), userDTO);
            log.info("Profile updated successfully for user: {}", userDetails.getUsername());
            
            return ResponseEntity.ok(Map.of(
                "message", "Perfil atualizado com sucesso",
                "user", updatedUser
            ));
        } catch (Exception e) {
            log.error("Error updating profile for user {}: {}", userDetails.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "message", "Erro ao atualizar perfil: " + e.getMessage()
            ));
        }
    }
}
