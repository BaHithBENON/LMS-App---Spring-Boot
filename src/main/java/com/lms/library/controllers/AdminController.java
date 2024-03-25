package com.lms.library.controllers;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lms.library.enums.UserRole;
import com.lms.library.models.ModelProfile;
import com.lms.library.models.ModelUser;
import com.lms.library.requests.ReaderRequest;
import com.lms.library.services.EmailService;
import com.lms.library.services.ProfileService;
import com.lms.library.services.UserDetailsServiceImpl;
import com.lms.library.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
@Secured("ROLE_ADMIN")
public class AdminController {

	@Autowired
    private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
    private UserService userService;
	
	@Autowired
    private ProfileService profileService;

	@Autowired
	private EmailService emailService;
	
	private String globalMailSubject;
	
	List<ModelProfile> readers = new ArrayList<>();

	
	/*
	 * Routes vers les pages
	 */
	
	@GetMapping("dashboard")
    public String adminDashboardPage(Model model, Principal principal) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
		model.addAttribute("user", userDetails);
		
		// Récupérer les utilisateurs
	    List<ModelProfile> allProfiles = profileService.findAllByUserRoleUser();

	    // AJout de données
	 	model.addAttribute("readers", allProfiles);
	    
		//
        return "dashboard/dashboard"; // 
    }
	
	@GetMapping("add_reader")
    public String addReaderPage(Model model, Principal principal) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
		model.addAttribute("user", userDetails);
        return "dashboard/add_reader"; // 
    }
	
	@GetMapping("user_details")
    public String userDetailsPage(@RequestParam("id") Long id, Model model, Principal principal) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
		model.addAttribute("user", userDetails);
		
		System.out.println("ID" + id);
		ModelProfile reader = profileService.findById(id);
		
		// AJout de données
	 	model.addAttribute("reader", reader);
		
        return "dashboard/user_details"; // 
    }
	
	@Secured("ROLE_ADMIN")
	@Transactional
	@PostMapping("/reader/delete")
	public ModelAndView deleteReader(
			@RequestParam("id") Long id,
			Model model,
			Principal principal,
	        RedirectAttributes redirectAttributes,
	        HttpServletRequest request
	) {
		
		ModelUser user = userService.findById(id);
		
		if(user == null) {
			// Obtenez l'URL de référence
		    String refererUrl = request.getHeader("Referer");
		    
			redirectAttributes.addFlashAttribute("reservationvalidationerror", 
				"Echec de suppression!");
			return new ModelAndView("redirect:" + refererUrl);
		}
		
		userService.deleteById(id);
		
		return new ModelAndView("redirect:/admin/dashboard");
	}
	
	
	/*
	 * Routes pour les données
	 */
	
	@Transactional
	@PostMapping("/save_reader")
	public String save_reader(@ModelAttribute ReaderRequest reader, Model model, Principal principal, BindingResult result) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
		
		//System.out.println(reader.toString());
		
		// Vérification des valeurs (vides ou pas )
		if(reader == null || reader.isEmpty()) {
			// Ajoutez un message d'erreur au modèle
            model.addAttribute("error", "Vous avez des données non valides ou non renseignées.");
            // Renvoyez le nom de la vue actuelle pour rester sur la même page
            return "dashboard/add_reader";
		}
		
		//System.out.println(reader.getUsername());
		// Vérifiez si le formulaire est valide
        if (result.hasErrors()) {
            // Ajoutez un message d'erreur au modèle
            model.addAttribute("error", "Erreur lors de la sauvegarde du lecteur.");
            // Renvoyez le nom de la vue actuelle pour rester sur la même page
            return "dashboard/add_reader";
        }
        
        //  private String username;
        ModelUser user = new ModelUser();
        user.setEmail(reader.getEmail());
        user.setUsername(reader.getUsername());
   		user.setPassword(passwordEncoder.encode(reader.getPassword()));
   		user.setPenalty(false);
   		user.setRole(UserRole.USER);
   		
   		ModelProfile profile = new ModelProfile();
   	    profile.setAddress(reader.getAddress());
   	    profile.setPhoneNumber(reader.getTelephone());
   	    profile.setGender(reader.getGender());
   	    profile.setFirstname(reader.getFirstname());
   	    profile.setLastname(reader.getLastname());
   		
   	    profile.setUser(user);
   		user.setProfile(profile);
   		
   		// Sauvegarde
   		userService.save(user);
   		profileService.save(profile);
   		
   		String readerStateMessage = "Bonjour ! \n" +
   				"Votre compte AsLibrary a été créer avec succès ! \n" +
   				"Prennez note de vos identifiants [ \n" +
   				"\t Nom d'utilisateur : " + reader.getEmail() +
   				"\t Mot de passe : " + reader.getPassword() + 
   				"]\n\n" + 
   				"Vous pouvez vous connectez sur le site en ligne de la bibliothèque " + 
   				"pour faire vos réservations et consulter vos status d'emprunts. \n\n" +
   				"AsLibrary | Votre bibliothèque préférée !!!";
		
   		try {
   			globalMailSubject = "AsLibrary | Reservation";
			emailService.sendSimpleMessage(reader.getEmail(), globalMailSubject, readerStateMessage);
		} catch (Exception e) {
			
		}
   		
		model.addAttribute("user", userDetails);
		model.addAttribute("reader", "Lecteur ajouté avec succès!");
        return "redirect:/admin/add_reader"; // 
	}
}
