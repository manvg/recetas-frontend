package com.appweb.recetas.controller.api;

import com.appweb.recetas.model.dto.Receta;
import com.appweb.recetas.model.dto.ResponseModel;
import com.appweb.recetas.service.RecetaService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api/recetas")
public class ApiRecetaController {

    private final RecetaService recetaService;

    public ApiRecetaController(RecetaService recetaService) {
        this.recetaService = recetaService;
    }

    @PostMapping("/create")
    public String createReceta(@ModelAttribute Receta receta, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
        try {
            ResponseModel responseModel = recetaService.create(receta, request, response);

            if (responseModel.getStatus()) {
                return "redirect:/home";
            } else {
                redirectAttributes.addFlashAttribute("error", responseModel.getMessage());
                return "redirect:/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ha ocurrido un error al intentar crear la receta.");
            return "redirect:/login";
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

        return "nueva-receta";
    }
}
