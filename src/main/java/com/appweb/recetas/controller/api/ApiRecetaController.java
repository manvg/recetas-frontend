package com.appweb.recetas.controller.api;

import com.appweb.recetas.config.Constants;
import com.appweb.recetas.model.dto.Receta;
import com.appweb.recetas.model.dto.ResponseModel;
import com.appweb.recetas.service.RecetaService;

import java.io.File;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/recetas")
public class ApiRecetaController {

    private final RecetaService recetaService;

    public ApiRecetaController(RecetaService recetaService) {
        this.recetaService = recetaService;
    }

    @PostMapping("/create")
    public String createReceta(@ModelAttribute Receta receta, @RequestParam(value = "foto", required = false) MultipartFile foto, @RequestParam(value = "video", required = false) MultipartFile video, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) 
    {
        try {
            if (foto != null && !foto.isEmpty()) {
                String pathImg = new File(Constants.ABSOLUTE_PATH_IMAGES).getAbsolutePath();
                
                File directory = new File(pathImg);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                String fotoPath = pathImg + File.separator + foto.getOriginalFilename();
            
                foto.transferTo(new File(fotoPath));

                String urlPath = Constants.PATH_SAVE_IMAGES + foto.getOriginalFilename();
                receta.setUrlImagen(urlPath);
            }

            if (video != null && !video.isEmpty()) {
                String pathVideo = new File(Constants.ABSOLUTE_PATH_VIDEOS).getAbsolutePath();
            
                File directory = new File(pathVideo);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                String videoPath = pathVideo + File.separator + video.getOriginalFilename();
            
                video.transferTo(new File(videoPath));
            
                String urlPath = Constants.PATH_SAVE_VIDEOS + video.getOriginalFilename();
                receta.setUrlVideo(urlPath);
            }

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

    @GetMapping("/all")
    public List<Receta> getAllRecetas(@RequestParam(name = "nombre", required = false) String nombre,@RequestParam(name = "descripcion", required = false) String descripcion,@RequestParam(name = "tipoCocina", required = false) String tipoCocina,@RequestParam(name = "paisOrigen", required = false) String paisOrigen,@RequestParam(name = "dificultad", required = false) String dificultad) 
    {
        List<Receta> recetas = recetaService.getAllRecetas(nombre, descripcion, tipoCocina, paisOrigen, dificultad);
        if (recetas == null) {
            throw new RuntimeException("Error al obtener las recetas. Verifique la configuración del backend.");
        }
        return recetas;
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Receta getRecetaById(@PathVariable("id") Integer id, HttpServletRequest request) {
        return recetaService.getRecetaById(id, request);
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
