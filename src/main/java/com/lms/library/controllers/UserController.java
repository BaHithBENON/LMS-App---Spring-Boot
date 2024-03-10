package com.lms.library.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lms.library.enums.UserRole;
import com.lms.library.models.ModelProfile;
import com.lms.library.models.ModelUser;
import com.lms.library.requests.ReaderRequest;
import com.lms.library.services.UserService;

@Controller
@RequestMapping("/guest")
public class UserController {
	
	@Autowired
    private UserService userService;
	
	@Autowired
    private UserDetailsService userDetailsService;
	
	/*
	 * Routes de pages
	 */
	
	@GetMapping("/dashboard")
	public String readerDashboardPage (Model model, Principal principal) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
		model.addAttribute("user", userDetails);
		

		ModelUser reader = userService.findByUsernameOrEmail(userDetails.getUsername(), userDetails.getUsername());
		model.addAttribute("reader", reader);
		return "dashboard/user_dashboard";
	}
	
	
	/*
	 * Routes pour les données
	 */
	
   
    @GetMapping
    public List<ModelUser> getAllUsers() {
        return userService.findAll();
    }

    @PostMapping
    public ModelUser createUser(@RequestBody ModelUser user) {
        return userService.save(user);
    }

    @GetMapping("/{id}")
    public ModelUser getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PutMapping("/{id}")
    public ModelUser updateUser(@PathVariable Long id, @RequestBody ModelUser userDetails) {
        // Implémentez la logique de mise à jour  ici
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
    }
}
