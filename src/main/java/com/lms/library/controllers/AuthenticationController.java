package com.lms.library.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationController {
	
	@GetMapping("/login")
    public String loginPage() {
        return "auth/login"; // Chemin vers votre fichier login.html dans /resources/templates/auth
    }
}
