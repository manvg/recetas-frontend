package com.appweb.recetas.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenStore tokenStore;

    public JwtAuthenticationFilter(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException 
    {
        String token = tokenStore.getToken(request);

        if (StringUtils.hasText(token) && validateToken(token)) {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(Constants.getSecretKey().getBytes()) // Obtener clave secreta
                .build()
                .parseClaimsJws(token)
                .getBody();

            String username = claims.getSubject();

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username, null, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    private boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(Constants.getSecretKey().getBytes()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
