package com.lms.library.controllers;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.lms.library.models.ModelLoan;
import com.lms.library.models.ModelReservation;
import com.lms.library.models.ModelUser;
import com.lms.library.services.BookService;
import com.lms.library.services.LoanService;
import com.lms.library.services.UserDetailsServiceImpl;
import com.lms.library.services.UserService;

@Controller
public class LoanController {

    @Autowired
    private LoanService loanService;

	@Autowired
    private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private BookService bookService;

	@Autowired
	private UserService userService;
    
    @Secured("ROLE_ADMIN")
	@GetMapping("/admin/loans")
    public String loansPage(Model model, Principal principal) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
		model.addAttribute("user", userDetails);
		

		List<ModelLoan> loans = loanService.findAll();

		model.addAttribute("loans", loans);
		
        return "dashboard/loans"; // 
    }
    
    @Secured("ROLE_ADMIN")
	@Transactional
	@PostMapping("/admin/books/loan")
	public ModelAndView loanBook(
			@RequestParam("id") Long id, 
			@RequestParam("username") String username,
			@RequestParam("due") @DateTimeFormat(pattern = "yyyy-MM-dd") Date due,
			@RequestParam("code") String code,
			@RequestParam("counter") int counter,
			Model model,
			Principal principal,
	        RedirectAttributes redirectAttributes
	) {
		
		ModelBook book = bookService.findById(id);
		ModelUser reader = userService.findByUsernameOrEmail(username, username);
		if(book == null || reader == null) {
			redirectAttributes.addFlashAttribute("loanerror", 
				"Echec de l'emprunt! Lecteur ou livre introuvable...");
			return new ModelAndView("redirect:/books/book_details?id=" + id);
		}
		
		if(book.getCopies() < counter) {
			redirectAttributes.addFlashAttribute("loanerror", 
					"Echec de l'emprunt! Nombre d'exemplaires insuffisants.");
				return new ModelAndView("redirect:/books/book_details?id=" + id);
		}
		
		ModelLoan loan = new ModelLoan();
		loan.setBook(book);
		book.addLoan(loan);
		loan.setUser(reader);
		reader.addLoan(loan);
		

		book.setCopies(book.getCopies() - counter);
		loan.setCopies(counter);
		
		Date loanDate = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
		loan.setLoanDate(loanDate);
		
		Date dueDate = due;
		loan.setDueDate(dueDate);
		
		// Update
		createLoan(loan);
		userService.save(reader);
		bookService.save(book);
		
		redirectAttributes.addFlashAttribute("loanstate", "Emprunt effectué avec succès!");
		return new ModelAndView("redirect:/books/book_details?id=" + id); // Redirigez vers la page des livres après la sauvegarde
	}
    
    
    @Secured("ROLE_ADMIN")
	@Transactional
	@PostMapping("/admin/loans/book")
	public ModelAndView loanBook( 
			@RequestParam("username") String username,
			@RequestParam("due") @DateTimeFormat(pattern = "yyyy-MM-dd") Date due,
			@RequestParam("code") String code,
			@RequestParam("counter") int counter,
			Model model,
			Principal principal,
	        RedirectAttributes redirectAttributes
	) {
		
		ModelBook book = bookService.findByCode(code);
		ModelUser reader = userService.findByUsernameOrEmail(username, username);
		if(book == null || reader == null) {
			redirectAttributes.addFlashAttribute("loanerror", 
				"Echec de l'emprunt! Lecteur ou livre introuvable...");
			return new ModelAndView("redirect:/admin/loans");
		}
		
		if(book.getCopies() < counter) {
			redirectAttributes.addFlashAttribute("loanerror", 
					"Echec de l'emprunt! Nombre d'exemplaires insuffisants.");
				return new ModelAndView("redirect:/admin/loans");
		}
		
		ModelLoan loan = new ModelLoan();
		loan.setBook(book);
		book.addLoan(loan);
		loan.setUser(reader);
		reader.addLoan(loan);
		
		book.setCopies(book.getCopies() - counter);
		loan.setCopies(counter);
		
		Date loanDate = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
		loan.setLoanDate(loanDate);
		
		Date dueDate = due;
		loan.setDueDate(dueDate);
		
		// Update
		createLoan(loan);
		userService.save(reader);
		bookService.save(book);
		
		redirectAttributes.addFlashAttribute("loanstate", "Emprunt effectué avec succès!");
		return new ModelAndView("redirect:/admin/loans"); // Redirigez vers la page des livres après la sauvegarde
	}
    
    
    @Secured("ROLE_ADMIN")
	@Transactional
	@PostMapping("/admin/loans/validate")
	public ModelAndView validateLoanBook(
			@RequestParam("id") Long id,
			@RequestParam("loanState") int loanState,
			Model model,
			Principal principal,
	        RedirectAttributes redirectAttributes
	) {
		
		ModelLoan loan = loanService.findById(id);
		
		if(loan == null) {
			redirectAttributes.addFlashAttribute("loanvalidationerror", 
				"Echec de l'emprunt! Lecteur ou livre introuvable...");
			return new ModelAndView("redirect:/admin/loans");
		}
		
		if(loanState == 1) {
			Date returnDate = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
			loan.setReturnDate(returnDate);
			// Mise à jour du nombre d'exemplaires
			loan.getBook().setCopies(loan.getBook().getCopies() + loan.getCopies());
		} else {
			loan.setReturnDate(null);
			// Mise à jour du nombre d'exemplaires
			loan.getBook().setCopies(loan.getBook().getCopies() - loan.getCopies());
		}
		
		updateLoan(id, loan);
		
		return new ModelAndView("redirect:/admin/loans");
	}
    
    @Secured("ROLE_ADMIN")
	@Transactional
	@PostMapping("/admin/books/loan/validate")
	public ModelAndView validateLoanBook(
			@RequestParam("id") Long id,
			@RequestParam("bookId") Long bookId,
			Model model,
			Principal principal,
	        RedirectAttributes redirectAttributes
	) {
		
		ModelLoan loan = loanService.findById(id);
		
		if(loan == null) {
			redirectAttributes.addFlashAttribute("loanvalidationerror", 
				"Echec de l'emprunt! Lecteur ou livre introuvable...");
			return new ModelAndView("redirect:/books/book_details?id=" + bookId);
		}
		
		Date returnDate = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
		loan.setReturnDate(returnDate);
		

		// Mise à jour du nombre d'exemplaires
		loan.getBook().setCopies(loan.getBook().getCopies() + loan.getCopies());
		
		updateLoan(id, loan);
		
		redirectAttributes.addFlashAttribute("loanvalidationstate", "Retour de livre effectué avec succès!");
		return new ModelAndView("redirect:/books/book_details?id=" + bookId); // Redirigez vers la page des livres après la sauvegarde
	}
    
    @Secured("ROLE_ADMIN")
	@Transactional
	@PostMapping("/admin/loans/delete")
	public ModelAndView deleteLoanBook(
			@RequestParam("id") Long id,
			Model model,
			Principal principal,
	        RedirectAttributes redirectAttributes
	) {
		
		ModelLoan loan = loanService.findById(id);
		
		if(loan == null) {
			redirectAttributes.addFlashAttribute("loanvalidationerror", 
				"Echec de suppression! Lecteur ou livre introuvable...");
			return new ModelAndView("redirect:/admin/loans");
		}
		
		ModelBook book = loan.getBook();
		if(loan.getReturnDate() == null) {
			book.setCopies(book.getCopies() + loan.getCopies());	
		}
		
		bookService.save(book);
		
		deleteLoan(loan.getId());
		
		return new ModelAndView("redirect:/admin/loans");
	}

    @GetMapping("/admin/loans/all")
    public List<ModelLoan> getAllLoans() {
        return loanService.findAll();
    }

    @PostMapping("/admin/loan")
    public ModelLoan createLoan(@RequestBody ModelLoan loan) {
        return loanService.save(loan);
    }

    @GetMapping("/admin/loans/{id}")
    public ModelLoan getLoanById(@PathVariable Long id) {
        return loanService.findById(id);
    }

    @PutMapping("/admin/loans/{id}")
    public ModelLoan updateLoan(@PathVariable Long id, @RequestBody ModelLoan loanDetails) {
        // Implémentez la logique de mise à jour  ici
        return null;
    }

    @DeleteMapping("/admin/loans/{id}")
    public void deleteLoan(@PathVariable Long id) {
        loanService.deleteById(id);
    }
}
