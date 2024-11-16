package com.appweb.recetas.controller.api;

import com.appweb.recetas.model.dto.ResponseModel;
import com.appweb.recetas.model.dto.Usuario;
import com.appweb.recetas.service.UsuarioService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api/usuarios")
public class ApiUsuarioController {

    private final UsuarioService usuarioService;

    public ApiUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/create")
    public String createUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
        try {
            ResponseModel responseModel = usuarioService.create(usuario, request, response);

            if (responseModel.getStatus()) {
                return "redirect:/login";
            } else {
                redirectAttributes.addFlashAttribute("error", responseModel.getMessage());
                return "redirect:/home";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ha ocurrido un error al intentar crear el usuario.");
            return "redirect:/register";
        }
    }

    @GetMapping("/create")
    public String createPage(RedirectAttributes redirectAttributes) {
        // Redirige al usuario si está autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");

        if (isAuthenticated) {
            return "redirect:/home";
        }

        return "register";
    }
}

