package com.lms.library.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lms.library.models.ModelProfile;
import com.lms.library.requests.ReaderRequest;
import com.lms.library.services.EmailService;
import com.lms.library.services.ProfileService;
import com.lms.library.services.UserDetailsServiceImpl;
import com.lms.library.services.UserService;

@Controller
@RequestMapping("/profiles")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

	@Autowired
    private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
    private UserService userService;

	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
    private EmailService emailService;
    
    /*
	 * Routes pour les données
	 */
	
	@Secured("ROLE_ADMIN")
	@Transactional
	@PostMapping("/update_reader")
	public String save_reader(
			@RequestParam("id") Long id, 
			@ModelAttribute ReaderRequest reader, 
			Model model, 
			Principal principal, 
			BindingResult result,
			RedirectAttributes redirectAttributes
	){
		UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
		model.addAttribute("user", userDetails);
		
		// emailService.sendSimpleMessage("asistemcdls@gmail.com", "Test Email", "This is a test email.");
		
		// Ajoutez des attributs à RedirectAttributes si nécessaire
        redirectAttributes.addAttribute("id", id);
		//System.out.println(reader.toString());
        ModelProfile newreader = getProfileById(id);
		model.addAttribute("reader", newreader);
		
		// Vérification des valeurs (vides ou pas )
		if(reader == null || (
				reader.getUsername().isBlank() ||
				reader.getEmail().isBlank() ||
				reader.getGender().isBlank() || 
				reader.getAddress().isBlank() ||
				reader.getTelephone().isBlank()
		)) {
			// Ajoutez un message d'erreur au modèle
            model.addAttribute("updateerror", "Vous avez des données non valides ou non renseignées.");
            // Renvoyez le nom de la vue actuelle pour rester sur la même page
            return "dashboard/user_details";
		}
		
		//System.out.println(reader.getUsername());
		// Vérifiez si le formulaire est valide
        if (result.hasErrors()) {
            // Ajoutez un message d'erreur au modèle
            model.addAttribute("updateerror", "Erreur lors de la sauvegarde du lecteur.");
            // Renvoyez le nom de la vue actuelle pour rester sur la même page
            return "dashboard/user_details";
        }
        
        
        
        newreader.getUser().setEmail(reader.getEmail());
        newreader.getUser().setUsername(reader.getUsername());
        
        if(reader.getPassword() != null && !reader.getPassword().isBlank()) {
        	newreader.getUser().setPassword(passwordEncoder.encode(reader.getPassword()));        	
        }
   		
   		newreader.setAddress(reader.getAddress());
   		newreader.setPhoneNumber(reader.getTelephone());
   		newreader.setGender(reader.getGender());
   		newreader.setFirstname(reader.getFirstname());
   		newreader.setLastname(reader.getLastname());
   		   		
   		// Sauvegarde
   		profileService.save(newreader);
   		
		model.addAttribute("updatestate", "Lecteur mis à jour avec succès!");
        return "redirect:/admin/user_details"; // 
	}

    @GetMapping
    public List<ModelProfile> getAllProfiles() {
        return profileService.findAll();
    }

    @PostMapping
    public ModelProfile createProfile(@RequestBody ModelProfile profile) {
        return profileService.save(profile);
    }

    @GetMapping("/{id}")
    public ModelProfile getProfileById(@PathVariable Long id) {
        return profileService.findById(id);
    }

    @PutMapping("/{id}")
    public ModelProfile updateProfile(@PathVariable Long id, @RequestBody ModelProfile profileDetails) {
        profileService.save(profileDetails);
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteProfile(@PathVariable Long id) {
        profileService.deleteById(id);
    }
}
