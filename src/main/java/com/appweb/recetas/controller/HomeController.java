package com.appweb.recetas.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.appweb.recetas.controller.api.ApiRecetaController;
import com.appweb.recetas.model.dto.Receta;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

    private final ApiRecetaController apiRecetaController;

    public HomeController(ApiRecetaController apiRecetaController) {
        this.apiRecetaController = apiRecetaController;
    }

    @GetMapping("/home")
    public String home(@RequestParam(name = "name", required = false, defaultValue = "Recetas") String name,@RequestParam(name = "nombre", required = false) String nombre,@RequestParam(name = "descripcion", required = false) String descripcion,@RequestParam(name = "tipoCocina", required = false) String tipoCocina,@RequestParam(name = "paisOrigen", required = false) String paisOrigen,@RequestParam(name = "dificultad", required = false) String dificultad,HttpServletRequest request,Model model) {

        model.addAttribute("name", name);

        List<Receta> recetas = apiRecetaController.getAllRecetas(nombre, descripcion, tipoCocina, paisOrigen, dificultad, request);
        if (recetas == null) {
            recetas = Collections.emptyList();
        }
        setAuthenticationStatus(model);
        model.addAttribute("recetas", recetas);

        return "home";
    }

    @GetMapping("/")
    public String root(
        @RequestParam(name = "name", required = false, defaultValue = "Recetas") String name,
        @RequestParam(name = "nombre", required = false) String nombre,
        @RequestParam(name = "descripcion", required = false) String descripcion,
        @RequestParam(name = "tipoCocina", required = false) String tipoCocina,
        @RequestParam(name = "paisOrigen", required = false) String paisOrigen,
        @RequestParam(name = "dificultad", required = false) String dificultad,
        HttpServletRequest request,
        Model model
    ) {
        return home(name, nombre, descripcion, tipoCocina, paisOrigen, dificultad, request, model);
    }
    

    private void setAuthenticationStatus(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");
        model.addAttribute("authenticated", isAuthenticated);
    }
}
