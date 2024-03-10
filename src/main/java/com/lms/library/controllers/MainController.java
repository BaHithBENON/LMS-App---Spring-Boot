package com.lms.library.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lms.library.models.ModelBook;
import com.lms.library.models.ModelUser;
import com.lms.library.services.BookService;
import com.lms.library.services.UserDetailsServiceImpl;
import com.lms.library.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class MainController {
	@Autowired
    private UserService userService;

	@Autowired
    private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private BookService bookService;
	
	
	@RequestMapping(value = "/back", method = RequestMethod.GET)
    public String retour(HttpServletRequest request) {
        return "redirect:" + request.getHeader("Referer");
    }
	
	@GetMapping("/")
    public String homePage(Model model, Principal principal) {
		if(principal != null) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
			ModelUser u = userService.findByUsernameOrEmail(userDetails.getUsername(), userDetails.getUsername());
			model.addAttribute("userDeatils", userDetails);
			model.addAttribute("user", u);
		}
		

		List<ModelBook> books = bookService.findAll();

		model.addAttribute("books", books);
		
        return "guest/index"; // 
    }
}
