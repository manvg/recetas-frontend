package com.appweb.recetas.controller.api;

import com.appweb.recetas.model.dto.AuthResponse;
import com.appweb.recetas.model.dto.LoginDto;
import com.appweb.recetas.service.AuthenticationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api/authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginDto loginDto, RedirectAttributes redirectAttributes, HttpServletResponse response) {
        try {
            //Autenticación
            AuthResponse authResponse = authenticationService.authenticate(loginDto, response);

            if (authResponse.isStatus()) {
                return "redirect:/home";
            } else {
                redirectAttributes.addFlashAttribute("error", authResponse.getMessage());
                return "redirect:/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ha ocurrido un error al intentar iniciar sesión.");
            return "redirect:/login";
        }
    }

    @GetMapping("/login")
    public String loginPage(RedirectAttributes redirectAttributes) {
        //Redireccionar si el usuario está autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");

        if (isAuthenticated) {
            return "redirect:/home";
        }

        return "login";
    }
}
