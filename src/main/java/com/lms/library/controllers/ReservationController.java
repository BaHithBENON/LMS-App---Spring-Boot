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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lms.library.models.ModelBook;
import com.lms.library.models.ModelReservation;
import com.lms.library.models.ModelUser;
import com.lms.library.services.BookService;
import com.lms.library.services.EmailService;
import com.lms.library.services.ReservationService;
import com.lms.library.services.UserDetailsServiceImpl;
import com.lms.library.services.UserService;

@Controller
public class ReservationController {
	
	@Autowired
    private ReservationService reservationService;

	@Autowired
    private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private BookService bookService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private EmailService emailService;
	
	private String globalMailSubject;
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/admin/reservations")
    public String reservationsPage(Model model, Principal principal) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
		model.addAttribute("user", userDetails);
		

		List<ModelReservation> reservations = reservationService.findAll();

		model.addAttribute("reservations", reservations);
		
        return "dashboard/reservations"; // 
    }
	
	@Secured("ROLE_ADMIN")
	@Transactional
	@PostMapping("/admin/reservations/book")
	public ModelAndView reserveBook(
			@RequestParam("username") String username,
			@RequestParam("code") String code,
			@RequestParam("counter") int counter,
			Model model,
			Principal principal,
	        RedirectAttributes redirectAttributes
	) {
		
		List<ModelBook> books = bookService.findByCode(code);
		ModelUser reader = userService.findByUsernameOrEmail(username, username);
		ModelBook book = null;
		if(books == null || reader == null) {
			redirectAttributes.addFlashAttribute("reservationerror", 
				"Echec de la reservation! Lecteur ou livre introuvable...");
			return new ModelAndView("redirect:/admin/reservations");
		}
		book = books.get(0);
		
		if(book.getCopies() < counter) {
			redirectAttributes.addFlashAttribute("reservationerror", 
					"Echec de la reservation! Nombre d'exemplaires insuffisants.");
				return new ModelAndView("redirect:/admin/reservations");
		}
		
		ModelReservation reservation = new ModelReservation();
		reservation.setBook(book);
		book.addReservation(reservation);
		reservation.setUser(reader);
		reader.addReservation(reservation);
		
		book.setCopies(book.getCopies() - counter);
		reservation.setCopies(counter);
		
		Date reservationDate = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
		reservation.setReservationDate(reservationDate);
		
		// Update
		createReservation(reservation);
		userService.save(reader);
		bookService.save(book);
		
		String reservationStateMessage = "Reservation effectuée avec succès!";
		
		try {
   			globalMailSubject = "AsLibrary | Reservation";
			emailService.sendSimpleMessage(reader.getEmail(), globalMailSubject, reservationStateMessage);
		} catch (Exception e) {
			
		}
		
		redirectAttributes.addFlashAttribute("reservationstate", reservationStateMessage);
		return new ModelAndView("redirect:/admin/reservations");
	}
	

	@Transactional
	@PostMapping("/books/reservation")
	public ModelAndView reserveBook(
			@RequestParam("id") Long id, 
			@RequestParam("username") String username,
			@RequestParam("code") String code,
			@RequestParam("counter") int counter,
			Model model,
			Principal principal,
	        RedirectAttributes redirectAttributes
	) {
		
		ModelBook book = bookService.findById(id);
		ModelUser reader = userService.findByUsernameOrEmail(username, username);
		if(book == null || reader == null) {
			redirectAttributes.addFlashAttribute("reservationerror", 
				"Echec de la reservation! Lecteur ou livre introuvable...");
			return new ModelAndView("redirect:/books/book_details?id=" + id);
		}
		
		if(book.getCopies() < counter) {
			redirectAttributes.addFlashAttribute("reservationerror", 
					"Echec de la reservation! Nombre d'exemplaires insuffisants.");
				return new ModelAndView("redirect:/books/book_details?id=" + id);
		}
		
		ModelReservation reservation = new ModelReservation();
		reservation.setBook(book);
		book.addReservation(reservation);
		reservation.setUser(reader);
		reader.addReservation(reservation);
		
		/*
		Date reservationDate = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
		reservation.setReservationDate(reservationDate);
		*/
		
		reservation.setCopies(counter);
	
		// Update
		createReservation(reservation);
		userService.save(reader);
		bookService.save(book);
		
		redirectAttributes.addFlashAttribute("reservationstate", "Demande de reservation envoyée avec succès!");
		return new ModelAndView("redirect:/books/book_details?id=" + id); // Redirigez vers la page des livres après la sauvegarde
	}
    
    @Secured("ROLE_ADMIN")
	@Transactional
	@PostMapping("/admin/books/reservation/validate")
	public ModelAndView validateReservationBook(
			@RequestParam("id") Long id,
			@RequestParam("bookId") Long bookId,
			Model model,
			Principal principal,
	        RedirectAttributes redirectAttributes
	) {
		
		ModelReservation reservation = reservationService.findById(id);
		
		if(reservation == null) {
			redirectAttributes.addFlashAttribute("reservationvalidationerror", 
				"Echec de la reservation! Lecteur ou livre introuvable...");
			return new ModelAndView("redirect:/books/book_details?id=" + bookId);
		}
		
		Date reservationDate = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
		reservation.setReservationDate(reservationDate);
		
		updateReservation(id, reservation);
		
		String reservationStateMessage = "Reservation effectuée avec succès!";
		
		try {
   			globalMailSubject = "AsLibrary | Reservation";
			emailService.sendSimpleMessage(reservation.getUser().getEmail(), globalMailSubject, reservationStateMessage);
		} catch (Exception e) {
			
		}
		
		redirectAttributes.addFlashAttribute("reservationvalidationstate", reservationStateMessage);
		return new ModelAndView("redirect:/books/book_details?id=" + bookId); // Redirigez vers la page des livres après la sauvegarde
	}
    
    @Secured("ROLE_ADMIN")
	@Transactional
	@PostMapping("/admin/reservations/validate")
	public ModelAndView validateReservationBook(
			@RequestParam("id") Long id,
			@RequestParam("reservationState") int reservationState,
			Model model,
			Principal principal,
	        RedirectAttributes redirectAttributes
	) {
		
		ModelReservation reservation = reservationService.findById(id);
		
		if(reservation == null) {
			redirectAttributes.addFlashAttribute("reservationvalidationerror", 
				"Echec de la reservation! Lecteur ou livre introuvable...");
			return new ModelAndView("redirect:/admin/reservations");
		}
		
		String reservationStateMessage = "Reservation effectuée avec succès!";
		
		if(reservationState == 1) {
			Date reservationDate = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
			reservation.setReservationDate(reservationDate);
		} else {
			reservation.setReservationDate(null);
			reservationStateMessage = "Reservation supprimée ou annuler!";
		}
		
		updateReservation(id, reservation);
		
		try {
   			globalMailSubject = "AsLibrary | Reservation";
			emailService.sendSimpleMessage(reservation.getUser().getEmail(), globalMailSubject, reservationStateMessage);
		} catch (Exception e) {
			
		}
		
		return new ModelAndView("redirect:/admin/reservations");
	}
    
    @Secured("ROLE_ADMIN")
	@Transactional
	@PostMapping("/admin/reservations/delete")
	public ModelAndView deleteReservationBook(
			@RequestParam("id") Long id,
			Model model,
			Principal principal,
	        RedirectAttributes redirectAttributes
	) {
		
		ModelReservation reservation = reservationService.findById(id);
		
		if(reservation == null) {
			redirectAttributes.addFlashAttribute("reservationvalidationerror", 
				"Echec de suppression!");
			return new ModelAndView("redirect:/admin/reservations");
		}
		
		deleteReservation(reservation.getId());
		
		return new ModelAndView("redirect:/admin/reservations");
	}


    @GetMapping("/admin/reservations/all")
    public List<ModelReservation> getAllReservations() {
        return reservationService.findAll();
    }

    @PostMapping("/admin/reservation")
    public ModelReservation createReservation(@RequestBody ModelReservation reservation) {
        return reservationService.save(reservation);
    }

    @GetMapping("/admin/reservations/{id}")
    public ModelReservation getReservationById(@PathVariable Long id) {
        return reservationService.findById(id);
    }

    @PutMapping("/admin/reservations/{id}")
    public ModelReservation updateReservation(@PathVariable Long id, @RequestBody ModelReservation reservationDetails) {
        // Implémentez la logique de mise à jour  ici
        return reservationService.save(reservationDetails);
    }

    @DeleteMapping("/admin/reservations/{id}")
    public void deleteReservation(@PathVariable Long id) {
        reservationService.deleteById(id);
    }
}
