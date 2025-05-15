package com.userapi.config;

import com.userapi.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.userapi.security.JwtAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.core.annotation.Order; // Adicionado

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter; // Adicionado

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // Cadeia de Filtros para API (JWT, Stateless)
    @Bean
    @Order(1) // Define a precedência desta cadeia
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**") // Esta cadeia de filtros se aplica apenas a caminhos sob /api/
            .csrf(csrf -> csrf.disable()) // Desabilitar CSRF para APIs stateless é comum
            .cors(cors -> cors.disable()) // Considere uma configuração CORS mais granular para produção
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // Autenticação da API é pública
                .requestMatchers("/api/profile/**", "/api/addresses/**").authenticated()
                .anyRequest().authenticated() // Outras rotas /api/** exigem autenticação
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // Cadeia de Filtros para Web (FormLogin, Stateful)
    @Bean
    @Order(2) // Define a precedência, menor valor tem maior precedência
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF habilitado por padrão para form login, mas ignorando para API que já foi tratado
            // .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**")) // Opcional se o matcher acima for suficiente
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/webjars/**").permitAll()
                .requestMatchers("/dashboard", "/endereco").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .permitAll()
            )
            .rememberMe(remember -> remember
                .rememberMeServices(rememberMeServices(this.userDetailsService)) // Passando userDetailsService
                .key("aVeryStrongRememberMeKeyForMyApp")
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)); // Sessões para UI web
        return http.build();
    }

    @Bean
    public RememberMeServices rememberMeServices(CustomUserDetailsService customUserDetailsService) { // Modificado para aceitar o service
        TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices(
            "springSecurityRememberMeKey", 
            customUserDetailsService
        );
        rememberMe.setAlwaysRemember(true);
        return rememberMe;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
