package com.appweb.recetas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final TokenStore tokenStore;

    public WebSecurityConfig(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/", "/home", "/login", "/register", "/api/authentication/login", "/api/usuarios/create", "/**.css", "/images/**", "/static/**").permitAll()
                .requestMatchers("/detalle-receta", "/nueva-receta").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(tokenStore), UsernamePasswordAuthenticationFilter.class)
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .addLogoutHandler((request, response, auth) -> tokenStore.clearToken(response))
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
