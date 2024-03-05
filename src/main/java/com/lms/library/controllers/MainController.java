package com.lms.library.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class MainController {
	
	@RequestMapping(value = "/back", method = RequestMethod.GET)
    public String retour(HttpServletRequest request) {
        return "redirect:" + request.getHeader("Referer");
    }
}
