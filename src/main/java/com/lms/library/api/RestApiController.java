package com.lms.library.api;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.library.enums.UserRole;
import com.lms.library.models.ModelBook;
import com.lms.library.models.ModelLoan;
import com.lms.library.models.ModelNotification;
import com.lms.library.models.ModelProfile;
import com.lms.library.models.ModelReservation;
import com.lms.library.models.ModelUser;
import com.lms.library.requests.BookRequest;
import com.lms.library.requests.LoanRequest;
import com.lms.library.requests.NotificationRequest;
import com.lms.library.requests.ReaderRequest;
import com.lms.library.requests.ReservationRequest;
import com.lms.library.services.BookService;
import com.lms.library.services.EmailService;
import com.lms.library.services.LoanService;
import com.lms.library.services.NotificationService;
import com.lms.library.services.ProfileService;
import com.lms.library.services.ReservationService;
import com.lms.library.services.UserService;

@RestController
@RequestMapping("/api")
public class RestApiController {
	
	/*
	 * Initialisations
	 */
	
	@Autowired
    private UserService userService;
	@Autowired
	private BookService bookService;
    @Autowired
    private LoanService loanService;
    @Autowired
    private ReservationService reservationService;
	@Autowired
    private NotificationService notificationService;
    @Autowired
    private ProfileService profileService;
	@Autowired
    private PasswordEncoder passwordEncoder;
	@Autowired
	private EmailService emailService;
	
	private String globalMailSubject;
	
	/*
	 * Gestion des utilisateurs (lecteurs)
	 */
    
	/**
     * Retrieves all users with the role of USER, and their corresponding profiles.
     *
     * @return A ResponseEntity containing a ApiResponse with a status code, a list of ModelProfile objects, and a success message.
     */
    @GetMapping("/readers/all")
    public ResponseEntity<ApiResponse> getAllReaders() {
    	
    	// Retrieve all users with the role of USER.
    	List<ModelUser> readers = userService.findByRole(UserRole.USER);
    	
    	// Retrieve all profiles of users with the role of USER.
    	List<ModelProfile> allProfiles = profileService.findAllByUserRoleUser();
    	
    	// Create a new ApiResponse with a status code of 200, the list of profiles, and a success message.
    	ApiResponse response = new ApiResponse(
			200,
			allProfiles,
			"Liste des lecteurs récupérée avec succès!"
    	);

    	// Return a ResponseEntity with the ApiResponse and the appropriate status code.
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    @GetMapping("/readers/uuid/{id}")
    public ResponseEntity<ApiResponse> getReaderById(@PathVariable("id") Long id) {
    	ModelUser reader = null;
    	reader = userService.findById(id);
    	
    	ApiResponse response = new ApiResponse(
			reader != null ? 200 : 404,
			reader,
			reader != null ? "Lecteur @[" + id + "] récupérée avec succès!" : "Lecteur introuvable!"
    	);
    	
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    @GetMapping("/readers/usem/{value}")
    public ResponseEntity<ApiResponse> getReaderByUsernameOrEmail(@PathVariable("value") String value) {
    	ModelUser reader = null;
    	reader = userService.findByUsernameOrEmail(value, value);
    	
    	ApiResponse response = new ApiResponse(
			reader != null ? 200 : 400,
			reader,
			reader != null ? "Lecteur @[" + value + "] récupérée avec succès!" : "Lecteur introuvable!"
    	);
    	
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    @Transactional
    @PostMapping("/readers")
    public ResponseEntity<ApiResponse> createReader(@RequestBody ReaderRequest user) {
    	
    	// Vérification des valeurs (vides ou pas )
		if(user == null) {
			ApiResponse response = new ApiResponse(
				400,
				user,
				"Echec de création du lecteur! \n." +
				"Informations incorrect. Veuillez vérifier vos informations s'il vous plaît !"
	    	);
			return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
		}
    			
    	ModelUser reader = null;
    	
    	reader = new ModelUser();
        reader.setEmail(user.getEmail());
        reader.setUsername(user.getUsername());
   		reader.setPassword(passwordEncoder.encode(user.getPassword()));
   		reader.setPenalty(false);
   		reader.setRole(UserRole.USER);
   		
   		ModelProfile profile = new ModelProfile();
   	    profile.setAddress("");
   	    profile.setPhoneNumber(user.getTelephone());
   	    profile.setGender(user.getGender());
   	    profile.setFirstname(user.getFirstname());
   	    profile.setLastname(user.getLastname());
   		
   	    profile.setUser(reader);
   		reader.setProfile(profile);
   		
   		// Sauvegarde
   		userService.save(reader);
   		profileService.save(profile);
   		
   		String readerStateMessage = "Bonjour ! \n" +
   				"Votre compte AsLibrary a été créer avec succès ! \n" +
   				"Prennez note de vos idntifiants [ \n" +
   				"\n\t Nom d'utilisateur : " + user.getEmail() +
   				"\n\t Mot de passe : " + user.getPassword() + 
   				"\n]\n\n" + 
   				"Vous pouvez vous connectez sur le site en ligne de la bibliothèque " + 
   				"pour faire vos réservations et consulter vos status d'emprunts. \n\n" +
   				"AsLibrary | Votre bibliothèque préférée !!!";
		
   		try {
   			globalMailSubject = "AsLibrary | Compte";
			emailService.sendSimpleMessage(reader.getEmail(), globalMailSubject, readerStateMessage);
		} catch (Exception e) {
			
		}
    	
    	ApiResponse response = new ApiResponse(
			reader != null ? 200 : 400,
			reader,
			reader != null ? "Lecteur créér avec succès!" : "Echec de création du lecteur!"
    	);
    	
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    @Transactional
    @PutMapping("/readers/{id}")
    public ResponseEntity<ApiResponse> updateReader(@PathVariable("id") Long id, @RequestBody ReaderRequest reader) {

    	ModelProfile newreader = profileService.findById(id);
    	
    	if(newreader == null) {
    		ApiResponse response = new ApiResponse(
				404,
				reader,
				"Echec de mise à jour du lecteur! Lecteur @[" + id + "] introuvable!."
	    	);
			return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    	}
    	
    	// Vérification des valeurs (vides ou pas )
		if(reader == null || (
				reader.getUsername().isBlank() ||
				reader.getEmail().isBlank() ||
				reader.getGender().isBlank() || 
				reader.getAddress().isBlank() ||
				reader.getTelephone().isBlank()
		)) {
			ApiResponse response = new ApiResponse(
				400,
				reader,
				"Echec de mise à jour du lecteur! \n." +
				"Informations incorrect. Veuillez vérifier vos informations s'il vous plaît !"
	    	);
			return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
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
   		
    	ModelProfile profileUpdated = profileService.save(newreader);
    	ApiResponse response = new ApiResponse(
			profileUpdated != null ? 200 : 500,
			profileUpdated,
			profileUpdated != null ? "Lecteur mis à jour avec succès!" : "Echec de mise à jour du lecteur!"
    	);
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    @Transactional
    @PatchMapping("/readers/{id}")
    public ResponseEntity<ApiResponse> partialUpdateReader(@PathVariable("id") Long id, @RequestBody ReaderRequest reader) {

    	ModelProfile newreader = profileService.findById(id);
    	
    	if(newreader == null) {
    		ApiResponse response = new ApiResponse(
				404,
				reader,
				"Echec de mise à jour du lecteur! Lecteur @[" + id + "] introuvable!."
	    	);
			return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    	}
    	
    	// Vérification des valeurs (vides ou pas )
		if(reader == null) {
			ApiResponse response = new ApiResponse(
				400,
				reader,
				"Echec de mise à jour du lecteur! \n." +
				"Informations incorrect. Veuillez vérifier vos informations s'il vous plaît !"
	    	);
			return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
		}
    	
		if(reader.getUsername() != null && !reader.getUsername().isBlank()) {
			newreader.getUser().setUsername(reader.getUsername());
		}
		
		if(reader.getEmail() != null && !reader.getEmail().isBlank()) {
			newreader.getUser().setEmail(reader.getEmail());
		}
		
		if(reader.getGender() != null && !reader.getGender().isBlank()){
			newreader.setGender(reader.getGender());
		}
		
		if(reader.getAddress() != null && !reader.getAddress().isBlank()) {
			newreader.setAddress(reader.getAddress());
		}
		
		if(reader.getTelephone() != null && !reader.getTelephone().isBlank()) {
			newreader.setPhoneNumber(reader.getTelephone());
		}
			
        if(reader.getPassword() != null && !reader.getPassword().isBlank()) {
        	newreader.getUser().setPassword(passwordEncoder.encode(reader.getPassword()));        	
        }
   		
        if(reader.getFirstname() != null && !reader.getFirstname().isBlank()) {
        	newreader.setFirstname(reader.getFirstname());
		}
        
        if(reader.getLastname() != null && !reader.getLastname().isBlank()) {
        	newreader.setLastname(reader.getLastname());
		}
   		
    	ModelProfile profileUpdated = profileService.save(newreader);
    	ApiResponse response = new ApiResponse(
			profileUpdated != null ? 200 : 500,
			profileUpdated,
			profileUpdated != null ? "Lecteur mis à jour avec succès!" : "Echec de mise à jour du lecteur!"
    	);
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
 
    @Transactional
    @DeleteMapping("/readers/{id}")
    public ResponseEntity<ApiResponse> deleteReader(@PathVariable("id") Long id) {
    	ModelUser reader = null;
    	reader = userService.findById(id);
    	if(reader == null) {
    		ApiResponse response = new ApiResponse(
				404,
				"Id -> [" + id + "]",
				"Echec de suppression du lecteur! Lecteur @[" + id + "] introuvable."
	    	);
    		return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    	}
    	
    	userService.deleteById(id);
    	reader = null;
    	reader = userService.findById(id);
    	
    	ApiResponse response = new ApiResponse(
			reader == null ? 200 : 500,
			"Id -> [" + id + "]",
			reader == null ? "Lecteur supprimé avec succès!" : "Echec de suppression du lecteur!"
    	);
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    
    /*
     *  Gestion des notifications
     */
    
    @Transactional
    @PostMapping("/notifications/{id}")
    public ResponseEntity<ApiResponse> sendNotification(
    		@PathVariable("id") Long id, 
    		@RequestBody NotificationRequest request
	) {
    	ModelUser reader = userService.findById(id);
		
    	if(reader == null) {
    		ApiResponse response = new ApiResponse(
				404,
				reader,
				"Echec de notification! Lecteur @[" + id + "] introuvable!."
	    	);
			return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    	}
    	
    	if(request.getContent() == null || request.getContent().isBlank()) {
    		ApiResponse response = new ApiResponse(
				400,
				reader,
				"Echec de notification! Vérifiez vos informations."
	    	);
			return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    	}
    	
    	ModelNotification notification = new ModelNotification();
        reader.getNotifications().add(notification);
        notification.setUser(reader);
        notification.setMessage(request.getContent());
        notification.setSentDate(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
        
        userService.save(reader);
        ModelNotification notificationSent = notificationService.save(notification);
        
        try {
   			globalMailSubject = "AsLibrary | Notification";
			emailService.sendSimpleMessage(reader.getEmail(), globalMailSubject, notification.getMessage());
		} catch (Exception e) {
			
		}
        
    	ApiResponse response = new ApiResponse(
    		notificationSent != null ? 200 : 400,
			notificationSent,
			notificationSent != null ? "Notification envoyée avec succès!" : "Echec de notification!"
    	);
    	
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    @GetMapping("/notifications/{userId}")
    public ResponseEntity<ApiResponse> getNotificationByUser(@PathVariable("userId") Long id) {
    	ModelUser reader = userService.findById(id);
    	
    	if(reader == null) {
    		ApiResponse response = new ApiResponse(
				404,
				reader,
				"Echec de récupération des notifications. Lecteur @[" + id + "] introuvable!."
	    	);
			return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    	}
    	
    	ApiResponse response = new ApiResponse(
			200,
			reader.getNotifications(),
			"Notifications récupérées avec succès!"
    	);

    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    
    /*
	 * Gestion des livres
	 */
    
    @GetMapping("/books/all")
    public ResponseEntity<ApiResponse> getAllbooks() {
    	List<ModelBook> books = bookService.findAll();
    	
    	ApiResponse response = new ApiResponse(
			200,
			books,
			"Liste des livres récupérée avec succès!"
    	);

    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    @GetMapping("/books/top/{n}")
    public ResponseEntity<ApiResponse> getTopNBooks(@PathVariable int n) {
        List<ModelBook> books = bookService.findTopNBooksWithLimit(n);
        ApiResponse response = new ApiResponse(
			200,
			books,
			books.size() + " livre(s) récupéré(s) avec succès!"
    	);
        return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    @GetMapping("/books/{id}")
    public ResponseEntity<ApiResponse> getBookById(@PathVariable("id") Long id) {
    	ModelBook book = null;
    	book = bookService.findById(id);
    	
    	ApiResponse response = new ApiResponse(
			book != null ? 200 : 404,
			book,
			book != null ? "Livre @[" + id + "] récupéré avec succès!" : "Livre introuvable!"
    	);
    	
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    @Transactional
    @PostMapping("/books")
    public ResponseEntity<ApiResponse> createBook(@RequestBody BookRequest request) {
    	
    	if(request == null || (
			request.getAuthors() == null || request.getAuthors().isBlank() ||
			request.getCategory() == null || request.getCategory().isBlank() ||
			request.getCode() == null || request.getCode().isBlank() ||
			request.getDescription() == null || request.getDescription().isBlank() ||
			request.getTitle() == null || request.getTitle().isBlank() ||
			request.getCopies() == null ||
			request.getYear() == null
		)) {
    		ApiResponse response = new ApiResponse(
				400,
				null,
				"Echec de création du livre!"
	    	);
    		return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    	}
    	
    	ModelBook book = new ModelBook();
		book.setTitle(request.getTitle());
		book.setDescription(request.getDescription());
		book.setCategory(request.getCategory());
		book.setYear(request.getYear());
		book.setAuthors(request.getAuthors());
		book.setCode(request.getCode());
		book.setCopies(request.getCopies());
		
   		// Sauvegarde
   		ModelBook bookCreated = bookService.save(book);
    	
    	ApiResponse response = new ApiResponse(
			bookCreated != null ? 200 : 400,
			bookCreated,
			bookCreated != null ? "Livre créér avec succès!" : "Echec de création du livre!"
    	);
    	
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    @Transactional
    @PutMapping("/books/{id}")
    public ResponseEntity<ApiResponse> updateBook(@PathVariable("id") Long id, @RequestBody BookRequest request) {

    	ModelBook newbook = bookService.findById(id);
    	
    	if(newbook == null) {
    		ApiResponse response = new ApiResponse(
				404,
				request,
				"Echec de mise à jour du livre! Livre @[" + id + "] introuvable!."
	    	);
			return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    	}
    	
    	// Vérification des valeurs (vides ou pas )
		if(request == null) {
			ApiResponse response = new ApiResponse(
				400,
				request,
				"Echec de mise à jour du livre! \n." +
				"Informations incorrect. Veuillez vérifier vos informations s'il vous plaît !"
	    	);
			return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
		}
		
		if(request.getAuthors() != null && !request.getAuthors().isBlank()) {
			newbook.setAuthors(request.getAuthors());
		}
		
		if(request.getCategory() != null && !request.getCategory().isBlank()) {
			newbook.setCategory(request.getCategory());
		}
		
		if(request.getCode() != null && !request.getCode().isBlank()) {
			newbook.setCode(request.getCode());
		}
		
		if(request.getCopies() != null) {
			newbook.setCopies(request.getCopies());
		}
		
		if(request.getDescription() != null && !request.getDescription().isBlank()) {
			newbook.setDescription(request.getDescription());
		}
		
		if(request.getTitle() != null && !request.getTitle().isBlank()) {
			newbook.setTitle(request.getTitle());
		}
		
		if(request.getYear() != null) {
			newbook.setYear(request.getYear());
		}
   		
    	ModelBook bookUpdated = bookService.save(newbook);
    	ApiResponse response = new ApiResponse(
			bookUpdated != null ? 200 : 500,
			bookUpdated,
			bookUpdated != null ? "Livre mis à jour avec succès!" : "Echec de mise à jour du livre!"
    	);
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    @Transactional
    @DeleteMapping("/books/{id}")
    public ResponseEntity<ApiResponse> deleteBook(@PathVariable("id") Long id) {
    	ModelBook book = bookService.findById(id);
    	if(book == null) {
    		ApiResponse response = new ApiResponse(
				404,
				"Id -> [" + id + "]",
				"Echec de suppression du livre! Livre @[" + id + "] introuvable."
	    	);
    		return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    	}
    	
    	bookService.deleteById(id);
    	book = bookService.findById(id);
    	
    	ApiResponse response = new ApiResponse(
			book == null ? 200 : 500,
			"Id -> [" + id + "]",
			book == null ? "Livre supprimé avec succès!" : "Echec de suppression du livre!"
    	);
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    
    /*
     * Gestion des emprunts 
     */
    
    @GetMapping("/loans/{userId}")
    public ResponseEntity<ApiResponse> getLoansByUser(@PathVariable("userId") Long id) {
    	ModelUser reader = userService.findById(id);
    	
    	ApiResponse response = new ApiResponse(
			reader == null ? 404 : 200,
			reader == null ? null : reader.getLoans(),
			reader == null ? "Utilisateur introuvable!" : "Emprunts récupérés avec succès!"
    	);

    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    @GetMapping("/loans/read/{id}")
    public ResponseEntity<ApiResponse> getLoanById(@PathVariable("id") Long id) {
    	ModelLoan loan = loanService.findById(id);
    	
    	ApiResponse response = new ApiResponse(
			loan != null ? 200 : 404,
			loan,
			loan != null ? "Emprunt @[" + id + "] récupéré avec succès!" : 
					"Echec de récupération de l'emprunt! Emprunt @[" + id + "] introuvable."
    	);
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<ApiResponse> createLoan(@RequestBody LoanRequest request) {
    	
    	if(request == null || (
			request.getCode() == null || request.getCode().isBlank() ||
			request.getDue() == null ||
			request.getUsername() == null || request.getUsername().isBlank()
		)) {
			ApiResponse response = new ApiResponse(
				400,
				null,
				"Echec d'emprunt du livre! Vérifiez vos informations."
	    	);
    		return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
		}
    	
    	List<ModelBook> books = bookService.findByCode(request.getCode());
		ModelUser reader = userService.findByUsernameOrEmail(request.getUsername(), request.getUsername());
		
		if(books == null || reader == null) {
			ApiResponse response = new ApiResponse(
				404,
				null,
				"Echec d'emprunt du livre! Livre @[" + request.getCode() + "]" +
				"ou Lecteur @[" + request.getUsername() + "]" +
				" introuvable."
	    	);
    		return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
		}
		
		ModelBook book = books.get(0);
		
		if(book.getCopies() < request.getCounter()) {
			ApiResponse response = new ApiResponse(
				400,
				null,
				"Echec d'emprunt du livre! Nombre d'exemplaires insuffisants."
	    	);
    		return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
		}
		
		ModelLoan loan = new ModelLoan();
		loan.setBook(book);
		book.addLoan(loan);
		loan.setUser(reader);
		reader.addLoan(loan);
		
		book.setCopies(book.getCopies() - request.getCounter());
		loan.setCopies(request.getCounter());
		
		Date loanDate = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
		loan.setLoanDate(loanDate);
		
		//Date dueDate = Date.from(LocalDateTime.parse(request.getDue()).toInstant(ZoneOffset.UTC));
		Date dueDate = request.getDue();
		loan.setDueDate(dueDate);
		
		// Update
		userService.save(reader);
		bookService.save(book);
		ModelLoan loanUpdated = loanService.save(loan);
		
		try {
			String mailContent = "Bonjour ! \n" +
				"Votre emprunt de livre chez AsLibrary a été enrégistrer avec succès ! \n" +
				"Vous êtes prier de le retourner au plus tard le " + dueDate.toString() + " " +
				"Sinon, vous pourriez subir des pénalités. \n\n" +
				"Prenez note des références [ \n" +
				"\n\t Titre : " + loanUpdated.getBook().getTitle() +
				"\n\t Nombre d'exemplaires : " + loanUpdated.getCopies() + 
				"\n]\n\n" + 
				"Vous pouvez vous connectez sur le site en ligne de la bibliothèque " + 
				"pour faire vos réservations et consulter vos status d'emprunts. \n\n" +
				"AsLibrary | Votre bibliothèque préférée !!!";
   			globalMailSubject = "AsLibrary | Emprunt";
			emailService.sendSimpleMessage(reader.getEmail(), globalMailSubject, mailContent);
		} catch (Exception e) {
			
		}
    	
    	ApiResponse response = new ApiResponse(
			loanUpdated != null ? 200 : 400,
			loanUpdated,
			loanUpdated != null ? "Livre emprunter avec succès!" : "Echec d'emprunt du livre!"
    	);
    	
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    @Transactional
    @PutMapping("/loans/{id}/return")
    public ResponseEntity<ApiResponse> returnLoan(
    		@PathVariable("id") Long id,
    		@RequestBody LoanRequest request
	) {
    	
    	if(request == null) {
			ApiResponse response = new ApiResponse(
				400,
				null,
				"Echec d'emprunt du livre! Vérifiez vos informations."
	    	);
    		return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
		}
    	
    	ModelLoan loan = loanService.findById(id);
		
		if(loan == null) {
			ApiResponse response = new ApiResponse(
				404,
				null,
				"Echec de mise à jour de l'emprunt! "+
				"Emprunt @[" + id + "] introuvable."
	    	);
    		return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
		}
		
		if(request.getReturnDate() != null) {
			loan.setReturnDate(request.getReturnDate());
			// Mise à jour du nombre d'exemplaires
			loan.getBook().setCopies(loan.getBook().getCopies() + loan.getCopies());
		} else {
			loan.setReturnDate(null);
			// Mise à jour du nombre d'exemplaires
			loan.getBook().setCopies(loan.getBook().getCopies() - loan.getCopies());
		}
		
		ModelLoan loanUpdated = loanService.save(loan);
    	
    	ApiResponse response = new ApiResponse(
			loanUpdated != null ? 200 : 400,
			loanUpdated,
			loanUpdated != null ? "Retour d'emprunt enrégistré avec succès!" : "Echec de retour d'emprunt du livre!"
    	);
    	
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    @Transactional
    @DeleteMapping("/loans/{id}")
    public ResponseEntity<ApiResponse> deleteLoan(@PathVariable("id") Long id) {
    	ModelLoan loan = loanService.findById(id);
    	
    	if(loan == null) {
    		ApiResponse response = new ApiResponse(
				404,
				"Id -> [" + id + "]",
				"Echec de suppression de l'emprunt! Emprunt @[" + id + "] introuvable."
	    	);
    		return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    	}
    	
    	ModelBook book = loan.getBook();
		if(loan.getReturnDate() == null) {
			book.setCopies(book.getCopies() + loan.getCopies());	
		}
		
		bookService.save(book);
		loanService.deleteById(id);
		ModelLoan loanDeleted = loanService.findById(id);
    	
    	ApiResponse response = new ApiResponse(
			loanDeleted == null ? 200 : 500,
			"Id -> [" + id + "]",
			loanDeleted == null ? "Emprunt supprimé avec succès!" : "Echec de suppression de l'emprunt!"
    	);
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    /*
     * Gestion des reservations
     */
    
    @GetMapping("/reservations/{userId}")
    public ResponseEntity<ApiResponse> getReservationsByUser(@PathVariable("userId") Long id) {
    	ModelUser reader = userService.findById(id);
    	
    	ApiResponse response = new ApiResponse(
			reader != null ? 200 : 404,
			reader != null ? reader.getReservations() : null,
			reader != null ? "Reservations récupérées avec succès!" : "Informations non retouvées"
    	);

    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    @GetMapping("/reservations/read/{id}")
    public ResponseEntity<ApiResponse> getReservationsById(@PathVariable("id") Long id) {
    	ModelReservation reservation = reservationService.findById(id);
    	
    	ApiResponse response = new ApiResponse(
			reservation != null ? 200 : 404,
			reservation,
			reservation != null ? "Reservation @[" + id + "] récupérée avec succès!" : 
					"Echec de récupération de la reservation! Reservation @[" + id + "] introuvable."
    	);
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    @Transactional
    @PostMapping("/reservations")
    public ResponseEntity<ApiResponse> createReservation(@RequestBody ReservationRequest request) {
    	
    	if(request == null || (
			request.getCode() == null || request.getCode().isBlank() ||
			request.getCounter()  < 1 ||
			request.getUsername() == null || request.getUsername().isBlank()
		)) {
			ApiResponse response = new ApiResponse(
				400,
				null,
				"Echec de reservation du livre! Vérifiez vos informations."
	    	);
    		return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
		}
    	
    	List<ModelBook> books = bookService.findByCode(request.getCode());
		ModelUser reader = userService.findByUsernameOrEmail(request.getUsername(), request.getUsername());
		
		if(books == null || reader == null) {
			ApiResponse response = new ApiResponse(
				404,
				null,
				"Echec de reservation du livre! Livre @[" + request.getCode() + "]" +
				"ou Lecteur @[" + request.getUsername() + "]" +
				" introuvable."
	    	);
    		return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
		}
		
		ModelBook book = books.get(0);
		
		if(book.getCopies() < request.getCounter()) {
			ApiResponse response = new ApiResponse(
				400,
				null,
				"Echec de reservation du livre! Nombre d'exemplaires insuffisants."
	    	);
    		return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
		}
		
		ModelReservation reservation = new ModelReservation();
		reservation.setBook(book);
		book.addReservation(reservation);
		reservation.setUser(reader);
		reader.addReservation(reservation);
		reservation.setCopies(request.getCounter());
		
		// Update
		userService.save(reader);
		bookService.save(book);
		
		ModelReservation reservationUpdated = reservationService.save(reservation);
		
		if(reservationUpdated != null) {
			try {
				String mailContent = "Bonjour ! \n" +
						"Votre reservation de livre chez AsLibrary a été enrégistrer avec succès ! \n" +
						"Vous serez averti de notre décision quant-au status de votre réservation sous peu. \n\n" +
						"Prenez note des références [ \n" +
						"\n\t Titre : " + reservationUpdated.getBook().getTitle() +
						"\n\t Nombre d'exemplaires : " + reservationUpdated.getCopies() + 
						"\n]\n\n" + 
						"Vous pouvez vous connectez sur le site en ligne de la bibliothèque " + 
						"pour faire vos réservations et consulter vos status d'emprunts. \n\n" +
						"AsLibrary | Votre bibliothèque préférée !!!";
				globalMailSubject = "AsLibrary | Reservation";
				emailService.sendSimpleMessage(reader.getEmail(), globalMailSubject, mailContent);
			} catch (Exception e) {
				
			}
		}
    	
    	ApiResponse response = new ApiResponse(
			reservationUpdated != null ? 200 : 500,
			reservationUpdated,
			reservationUpdated != null ? "Livre reserver avec succès!" : "Echec de reservation du livre!"
    	);
    	
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    @Transactional
    @PutMapping("/reservations/{id}/status")
    public ResponseEntity<ApiResponse> reservationStatus(
    		@PathVariable("id") Long id,
    		@RequestBody ReservationRequest request
	) {
    	
    	if(request == null || request.getCounter() < 0) {
			ApiResponse response = new ApiResponse(
				400,
				null,
				"Echec de la mise à jour du status de la reservation! Vérifiez vos informations."
	    	);
    		return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
		}
    	
    	ModelReservation reservation = reservationService.findById(id);
		
		if(reservation == null) {
			ApiResponse response = new ApiResponse(
				404,
				null,
				"Echec de mise à jour de la reservation! "+
				"Reservation @[" + id + "] introuvable."
	    	);
    		return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
		}
		
		if(request.isStatus()) {
			Date reservationDate = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
			reservation.setReservationDate(reservationDate);
		} else {
			reservation.setReservationDate(null);
		}
		
		ModelReservation reservationUpdated = reservationService.save(reservation);
		
		if(reservationUpdated != null) {
			try {
				String mailContent = "Bonjour ! \n" +
					"Votre reservation de livre chez AsLibrary a été valider avec succès ! \n" +
					"Prenez note des références [ \n" +
					"\n\t Titre : " + reservationUpdated.getBook().getTitle() +
					"\n\t Nombre d'exemplaires : " + reservationUpdated.getCopies() + 
					"]\n\n" + 
					"Vous pouvez vous connectez sur le site en ligne de la bibliothèque " + 
					"pour faire vos réservations et consulter vos status d'emprunts. \n\n" +
					"AsLibrary | Votre bibliothèque préférée !!!";
	   			globalMailSubject = "AsLibrary | Reservation";
				emailService.sendSimpleMessage(reservationUpdated.getUser().getEmail(), globalMailSubject, mailContent);
			} catch (Exception e) {
				
			}
		}
    	
    	ApiResponse response = new ApiResponse(
			reservationUpdated != null ? 200 : 400,
			reservationUpdated,
			reservationUpdated != null ? "Reservation mis à jour avec avec succès!" : "Echec de mise à jour de la reservation!"
    	);
    	
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    @Transactional
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<ApiResponse> deleteReservation(@PathVariable("id") Long id) {
    	ModelReservation reservation = reservationService.findById(id);
    	
    	if(reservation == null) {
    		ApiResponse response = new ApiResponse(
				404,
				"Id -> [" + id + "]",
				"Echec de suppression de la reservation! Reservation @[" + id + "] introuvable."
	    	);
    		return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    	}
    	
		reservationService.deleteById(id);
		ModelReservation reservationDeleted = reservationService.findById(id);
    	
    	ApiResponse response = new ApiResponse(
			reservationDeleted == null ? 200 : 500,
			"Id -> [" + id + "]",
			reservationDeleted == null ? "Reservation supprimée avec succès!" : "Echec de suppression de la reservation!"
    	);
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
}
