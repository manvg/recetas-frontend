package com.appweb.recetas.controller.api;

import com.appweb.recetas.model.dto.AuthResponse;
import com.appweb.recetas.model.dto.LoginDto;
import com.appweb.recetas.service.AuthenticationService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


@Controller
@RequestMapping("/authentication")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public String login(@ModelAttribute LoginDto loginDto, RedirectAttributes redirectAttributes) {

        AuthResponse response = authenticationService.authenticate(loginDto);

        if (response.isStatus()) {
            redirectAttributes.addFlashAttribute("token", response.getToken());
            return "redirect:/home";
        } else {
            redirectAttributes.addFlashAttribute("error", response.getMessage());
            return "redirect:/login";
        }
    }
}