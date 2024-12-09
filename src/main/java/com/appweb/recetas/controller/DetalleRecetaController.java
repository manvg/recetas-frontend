package com.appweb.recetas.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.appweb.recetas.controller.api.ApiRecetaController;
import com.appweb.recetas.model.dto.Receta;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class DetalleRecetaController {

    private final ApiRecetaController apiRecetaController;

    public DetalleRecetaController(ApiRecetaController apiRecetaController) {
        this.apiRecetaController = apiRecetaController;
    }

    @GetMapping("/detalle-receta/{id}")
    public String detalleReceta(@PathVariable("id") Integer id, Model model, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
    
        Receta receta = apiRecetaController.getRecetaById(id, request);
    
        if (receta != null) {
            receta.setIngredientesList(Arrays.asList(receta.getIngredientes().split(";")));
            receta.setInstruccionesList(Arrays.asList(receta.getInstrucciones().split(";")));
        }
    
        model.addAttribute("authenticated", isAuthenticated);
        model.addAttribute("receta", receta);
    
        return "detalle-receta";
    }
}
