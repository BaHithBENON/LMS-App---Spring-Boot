package com.lms.library.controllers;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lms.library.models.ModelNotification;
import com.lms.library.models.ModelUser;
import com.lms.library.services.EmailService;
import com.lms.library.services.NotificationService;
import com.lms.library.services.UserDetailsServiceImpl;
import com.lms.library.services.UserService;

@Controller
@RequestMapping("/notifications")
public class NotificationController {
	
	@Autowired
	private EmailService emailService;

	@Autowired
    private UserService userService;
	
	@Autowired
    private NotificationService notificationService;
	
	@Autowired
    private UserDetailsServiceImpl userDetailsService;
	
	private String globalMailSubject;

	@Secured("ROLE_ADMIN")
	@Transactional
	@PostMapping("/send")
	public ModelAndView sendNotification(
			@RequestParam("id") Long id, 
			@RequestParam("content") String content, 
			Model model, 
			Principal principal, 
			RedirectAttributes redirectAttributes
	){
		UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
		redirectAttributes.addFlashAttribute("user", userDetails);
		
		// Ajoutez des attributs à RedirectAttributes si nécessaire
		
        ModelUser reader = userService.findById(id);
        redirectAttributes.addAttribute("id", reader.getProfile().getId());
       
		// Vérification des valeurs (vides ou pas )
		if(reader == null || content.isBlank()) {
			// Ajoutez un message d'erreur au modèle
			redirectAttributes.addFlashAttribute("notificationerror", "Vous avez des données non valides ou non renseignées.");
            // Renvoyez le nom de la vue actuelle pour rester sur la même page
			return new ModelAndView("redirect:/admin/user_details");
		}
        
        ModelNotification notification = new ModelNotification();
        reader.getNotifications().add(notification);
        notification.setUser(reader);
        notification.setMessage(content);
        notification.setSentDate(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
        
   		userService.save(reader);
   		createNotification(notification);
   		
   		try {
   			globalMailSubject = "AsLibrary | Notification";
			emailService.sendSimpleMessage(reader.getEmail(), globalMailSubject, notification.getMessage());
		} catch (Exception e) {
			
		}
   		
   		redirectAttributes.addFlashAttribute("notificationstate", "Notification envoyé avec succès!");
		return new ModelAndView("redirect:/admin/user_details");
	}

    @GetMapping
    public List<ModelNotification> getAllNotifications() {
        return notificationService.findAll();
    }

    @PostMapping
    public ModelNotification createNotification(@RequestBody ModelNotification notification) {
        return notificationService.save(notification);
    }

    @GetMapping("/{id}")
    public ModelNotification getNotificationById(@PathVariable Long id) {
        return notificationService.findById(id);
    }

    @PutMapping("/{id}")
    public ModelNotification updateNotification(@PathVariable Long id, @RequestBody ModelNotification notificationDetails) {
        // Implémentez la logique de mise à jour  ici
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteById(id);
    }
}
