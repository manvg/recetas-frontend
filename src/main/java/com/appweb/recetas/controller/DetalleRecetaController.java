package com.appweb.recetas.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.appweb.recetas.controller.api.ApiRecetaController;
import com.appweb.recetas.model.dto.Receta;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class DetalleRecetaController {

    private final ApiRecetaController apiRecetaController;

    public DetalleRecetaController(ApiRecetaController apiRecetaController) {
        this.apiRecetaController = apiRecetaController;
    }

    @GetMapping("/detalle-receta")
    public String detalleReceta(Model model, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");

        //Obtener recetas desde backend
        List<Receta> recetas = apiRecetaController.getAllRecetas(request);

        recetas.forEach(receta -> {
            receta.setIngredientesList(Arrays.asList(receta.getIngredientes().split(";")));
            receta.setInstruccionesList(Arrays.asList(receta.getInstrucciones().split(";")));
        });

        model.addAttribute("authenticated", isAuthenticated);
        model.addAttribute("recetas", recetas);

        return "detalle-receta";
    }
}
