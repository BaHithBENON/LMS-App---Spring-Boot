package com.lms.library.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.lms.library.models.ModelBook;
import com.lms.library.models.ModelFile;
import com.lms.library.services.BookService;
import com.lms.library.services.UserDetailsServiceImpl;

@Controller
public class BookController {

	@Autowired
    private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private BookService bookService;
	
	/*
	 * Routes vers les vues
	 */
	
	@GetMapping("/admin/books")
    public String booksPage(Model model, Principal principal) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
		model.addAttribute("user", userDetails);
		

		List<ModelBook> books = bookService.findAll();
		for (ModelBook book : books) {
            if (book.getCover() != null && book.getCover().getContent() != null) {
                String imageBase64 = book.getCover().getImageBase64();
                book.setImageBase64(imageBase64); // Supposons que vous avez une méthode pour définir cette propriété
            }
        }
		model.addAttribute("books", books);
		
        return "dashboard/books"; // 
    }
	
	@GetMapping("/admin/add_book")
    public String adminDashboardPage(Model model, Principal principal) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
		model.addAttribute("user", userDetails);
		
		//
        return "dashboard/add_book"; // 
    }
	
	
	
	/*
	 * Routes vers les données
	 */

	@PostMapping("/admin/save_book")
	public ModelAndView saveBook(@RequestParam("title") String title,
			@RequestParam("description") String description,
			@RequestParam("category") String category,
			@RequestParam("year") Integer year,
			@RequestParam("authors") String authors,
			@RequestParam("code") String code,
			@RequestParam("copies") Integer copies,
			@RequestParam("thumbnail") MultipartFile thumbnail) {
		ModelBook book = new ModelBook();
		book.setTitle(title);
		book.setDescription(description);
		book.setCategory(category);
		book.setYear(year);
		book.setAuthors(authors);
		book.setCode(code);
		book.setCopies(copies);
		
		if(thumbnail.getSize() > 1000 ) {
			new ModelAndView("redirect:/admin/books");
		}
		
		if (!thumbnail.isEmpty()) {
			ModelFile cover = new ModelFile();
			cover.setFilePath(thumbnail.getOriginalFilename());
			try {
				byte[] imageBytes = thumbnail.getBytes();
				//cover.setContent(imageBytes);
				// Encodez les bytes en Base64
				String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
				//Base64.getEncoder().encode(imageBytes);
				cover.setContent(Base64.getEncoder().encode(imageBytes));
				cover.setImageBase64(imageBase64);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			book.setCover(cover);
		}
		
		createBook(book); // Assurez-vous que votre service gère la sauvegarde du livre
		
		return new ModelAndView("redirect:/admin/books"); // Redirigez vers la page des livres après la sauvegarde
	}
	
	/*
	 * @GetMapping("/books/book_details") public ModelAndView
	 * bookdetails(@RequestParam Long id, Model model, Principal principal) {
	 * UserDetails userDetails =
	 * userDetailsService.loadUserByUsername(principal.getName());
	 * model.addAttribute("user", userDetails);
	 * 
	 * ModelBook book = bookService.findById(id); if(book == null) {
	 * model.addAttribute("detailserror", "Livre introuvable"); return new
	 * ModelAndView("redirect:/admin/books"); } return new
	 * ModelAndView("guest/book_details"); }
	 * 
	 * @DeleteMapping("/admin/delete/{id}") public ModelAndView
	 * deleteByIdBook(@PathVariable Long id, Model model, Principal principal) {
	 * UserDetails userDetails =
	 * userDetailsService.loadUserByUsername(principal.getName());
	 * model.addAttribute("user", userDetails);
	 * 
	 * if(bookService.findById(id) == null) {
	 * 
	 * model.addAttribute("deleteerror", "Livre introuvable");
	 * 
	 * return new ModelAndView("redirect:/admin/book_details/" + id); }
	 * 
	 * deleteBook(id); if(bookService.findById(id) != null) {
	 * 
	 * model.addAttribute("deleteerror", "Echec de suppression");
	 * 
	 * return new ModelAndView("redirect:/admin/book_details/" + id); }
	 * 
	 * return new ModelAndView("redirect:/admin/books"); }
	 */
	
    @GetMapping
    public List<ModelBook> getAllBooks() {
        return bookService.findAll();
    }

    @PostMapping
    public ModelBook createBook(@RequestBody ModelBook book) {
        return bookService.save(book);
    }

    @GetMapping("/{id}")
    public ModelBook getBookById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PutMapping("/{id}")
    public ModelBook updateBook(@PathVariable Long id, @RequestBody ModelBook bookDetails) {
        // Implémentez la logique de mise à jour  ici
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
    }
}
