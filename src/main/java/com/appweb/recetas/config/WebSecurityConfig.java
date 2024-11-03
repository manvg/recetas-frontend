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
                .requestMatchers("/", "/home", "/login", "/authentication/login", "/**.css", "/images/**").permitAll()
                .requestMatchers("/receta/detalles/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(tokenStore), UsernamePasswordAuthenticationFilter.class)
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable())
            .logout((logout) -> logout.permitAll());

        return http.build();
    }

    //#region [CÓDIGO COMENTADO -NO SE UTILIZA ALMACENAMIENTO EN MEMORIA]
    // @Bean
    // @Description("Permite hacer login con datos del usuario almacenados de forma local")
    // public UserDetailsService users() {
    //     UserDetails user = User.builder()
    //         .username("user")
    //         .password(passwordEncoder().encode("password"))
    //         .roles("USER")
    //         .build();
    //     UserDetails admin = User.builder()
    //         .username("admin")
    //         .password(passwordEncoder().encode("password"))
    //         .roles("USER", "ADMIN")
    //         .build();
    //     return new InMemoryUserDetailsManager(user, admin);
    // }

    // @Bean
    // public PasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    // }
    //#endregion
}
