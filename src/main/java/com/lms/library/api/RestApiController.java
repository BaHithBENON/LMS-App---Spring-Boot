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
    
    /**
     * Retrieves a user with the given id, and their corresponding profile.
     *
     * @param id The id of the user to retrieve.
     * @return A ResponseEntity containing a ApiResponse with a status code, a ModelUser object, and a success message.
     */
    @GetMapping("/readers/uuid/{id}")
    public ResponseEntity<ApiResponse> getReaderById(@PathVariable("id") Long id) {
    	// Retrieve the user with the given id.
    	ModelUser reader = null;
    	reader = userService.findById(id);
    	
    	// Create a new ApiResponse with a status code, the user, and a success message.
    	ApiResponse response = new ApiResponse(
			reader != null ? 200 : 404,
			reader,
			reader != null ? "Lecteur @[" + id + "] récupérée avec succès!" : "Lecteur introuvable!"
    	);
    	
    	// Return a ResponseEntity with the ApiResponse and the appropriate status code.
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    /**
     * Retrieves a user with the given username or email.
     *
     * @param value The username or email of the user to retrieve.
     * @return A ResponseEntity containing a ApiResponse with a status code, a ModelUser object, and a success message.
     */
    @GetMapping("/readers/usem/{value}")
    public ResponseEntity<ApiResponse> getReaderByUsernameOrEmail(@PathVariable("value") String value) {
    	
    	// Retrieve the user with the given username or email.
    	ModelUser reader = null;
    	reader = userService.findByUsernameOrEmail(value, value);
    	
    	// Create a new ApiResponse with a status code, the user, and a success message.
    	ApiResponse response = new ApiResponse(
			reader != null ? 200 : 400,
			reader,
			reader != null ? "Lecteur @[" + value + "] récupérée avec succès!" : "Lecteur introuvable!"
    	);
    	
    	// Return a ResponseEntity with the ApiResponse and the appropriate status code.
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    /**
     * Creates a new user and profile with the given information.
     * Sends a welcome email to the user.
     *
     * @param  user	The user information to create a new user and profile.
     * @return        	A ResponseEntity containing a ApiResponse with a status code, a ModelUser object, and a success message.
     */
    @Transactional
    @PostMapping("/readers")
    public ResponseEntity<ApiResponse> createReader(@RequestBody ReaderRequest user) {
    	
    	// Check if user information is valid
		if(user == null) {
			ApiResponse response = new ApiResponse(
				400,
				user,
				"Failed to create reader! \n." +
				"Informations incorrect. Please verify your information !"
	    	);
			return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
		}
    			
    	// Create a new user and profile
    	ModelUser reader = new ModelUser();
    	ModelProfile profile = new ModelProfile();
    	
    	reader.setEmail(user.getEmail());
    	reader.setUsername(user.getUsername());
    	reader.setPassword(passwordEncoder.encode(user.getPassword()));
    	reader.setPenalty(false);
    	reader.setRole(UserRole.USER);
    	
    	profile.setAddress("");
    	profile.setPhoneNumber(user.getTelephone());
    	profile.setGender(user.getGender());
    	profile.setFirstname(user.getFirstname());
    	profile.setLastname(user.getLastname());
    	
    	profile.setUser(reader);
    	reader.setProfile(profile);
    	
    	// Save the user and profile
    	userService.save(reader);
    	profileService.save(profile);
    	
    	// Send a welcome email to the user
    	String readerStateMessage = "Hello! \n" +
    			"Your AsLibrary account has been successfully created! \n" +
    			"Please note the following credentials [ \n" +
    			"\n\t Username : " + user.getEmail() +
    			"\n\t Password : " + user.getPassword() + 
    			"\n]\n\n" + 
    			"You can now connect to our online library site to make your reservations and check your loan status. \n\n" +
    			"AsLibrary | Your favorite library !!!";
		
    	try {
    		globalMailSubject = "AsLibrary | Account";
			emailService.sendSimpleMessage(reader.getEmail(), globalMailSubject, readerStateMessage);
		} catch (Exception e) {
			
		}
    	
    	// Create a new ApiResponse with a status code, the user, and a success message
    	ApiResponse response = new ApiResponse(
			reader != null ? 200 : 400,
			reader,
			reader != null ? "Reader created successfully!" : "Failed to create reader!"
    	);
    	
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    /**
     * Update a reader's information.
     * @param id The id of the reader to update.
     * @param reader The new information for the reader.
     * @return A ResponseEntity containing an ApiResponse with the result of the update.
     */
    @Transactional
    @PutMapping("/readers/{id}")
    public ResponseEntity<ApiResponse> updateReader(@PathVariable("id") Long id, @RequestBody ReaderRequest reader) {
    	
    	// Find the reader to update
    	ModelProfile newReader = profileService.findById(id);
    	
    	// If the reader is not found, return an error response
    	if(newReader == null) {
    		ApiResponse response = new ApiResponse(
				404,
				reader,
				"Failed to update reader! Reader @[" + id + "] not found."
	    	);
			return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    	}
    	
    	// Check if the values provided are valid
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
				"Failed to update reader! \n." +
				"Incorrect information provided. Please verify your information!"
	    	);
			return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
		}
    	
    	// Update the reader's information
    	newReader.getUser().setEmail(reader.getEmail());
        newReader.getUser().setUsername(reader.getUsername());
        
        // If a new password is provided, encode it and update the user's password
        if(reader.getPassword() != null && !reader.getPassword().isBlank()) {
        	newReader.getUser().setPassword(passwordEncoder.encode(reader.getPassword()));        	
        }
   		
   		newReader.setAddress(reader.getAddress());
   		newReader.setPhoneNumber(reader.getTelephone());
   		newReader.setGender(reader.getGender());
   		newReader.setFirstname(reader.getFirstname());
   		newReader.setLastname(reader.getLastname());
   		
    	// Save the updated reader
    	ModelProfile updatedReader = profileService.save(newReader);
    	
    	// Create a response with the result of the update
    	ApiResponse response = new ApiResponse(
			updatedReader != null ? 200 : 500,
			updatedReader,
			updatedReader != null ? "Reader updated successfully!" : "Failed to update reader!"
    	);
    	
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    /**
     * Partially updates a reader's information.
     *
     * @param id The id of the reader to update.
     * @param reader The updated information of the reader.
     * @return A ResponseEntity containing a ApiResponse with a status code, a ModelProfile object, and a success message.
     */
    @Transactional
    @PatchMapping("/readers/{id}")
    public ResponseEntity<ApiResponse> partialUpdateReader(@PathVariable("id") Long id, @RequestBody ReaderRequest reader) {

    	// Find the reader by id
    	ModelProfile newReader = profileService.findById(id);
    	
    	// If the reader is not found, return a 404 error response
    	if(newReader == null) {
    		ApiResponse response = new ApiResponse(
				404,
				reader,
				"Failed to update reader! Reader @[" + id + "] not found."
	    	);
			return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    	}
    	
    	// Check if the provided information is not blank
    	// If any of the provided information is blank, return a 400 error response
		if(reader == null) {
			ApiResponse response = new ApiResponse(
				400,
				reader,
				"Failed to update reader! Please verify the provided information."
	    	);
			return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
		}
    	
    	// Update the relevant fields of the reader's information
    	if(reader.getUsername() != null && !reader.getUsername().isBlank()) {
			newReader.getUser().setUsername(reader.getUsername());
		}
		
		if(reader.getEmail() != null && !reader.getEmail().isBlank()) {
			newReader.getUser().setEmail(reader.getEmail());
		}
		
		if(reader.getGender() != null && !reader.getGender().isBlank()){
			newReader.setGender(reader.getGender());
		}
		
		if(reader.getAddress() != null && !reader.getAddress().isBlank()) {
			newReader.setAddress(reader.getAddress());
		}
		
		if(reader.getTelephone() != null && !reader.getTelephone().isBlank()) {
			newReader.setPhoneNumber(reader.getTelephone());
		}
			
        if(reader.getPassword() != null && !reader.getPassword().isBlank()) {
        	newReader.getUser().setPassword(passwordEncoder.encode(reader.getPassword()));        	
        }
   		
        if(reader.getFirstname() != null && !reader.getFirstname().isBlank()) {
        	newReader.setFirstname(reader.getFirstname());
		}
        
        if(reader.getLastname() != null && !reader.getLastname().isBlank()) {
        	newReader.setLastname(reader.getLastname());
		}
   		
    	// Save the updated reader's information
    	ModelProfile updatedReader = profileService.save(newReader);
    	
    	// Create a response with the result of the update
    	ApiResponse response = new ApiResponse(
			updatedReader != null ? 200 : 500,
			updatedReader,
			updatedReader != null ? "Reader updated successfully!" : "Failed to update reader!"
    	);
    	
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
 
    /**
     * Deletes a reader with the given id.
     *
     * @param  id  The id of the reader to delete.
     * @return     A ResponseEntity containing a ApiResponse with a status code, a message, and a success message.
     */
    @Transactional
    @DeleteMapping("/readers/{id}")
    public ResponseEntity<ApiResponse> deleteReader(@PathVariable("id") Long id) {
    	// Find the reader by id
    	ModelUser reader = null;
    	reader = userService.findById(id);
    	
    	// If the reader is not found, create a failure response
    	if(reader == null) {
    		ApiResponse response = new ApiResponse(
				404,
				"Id -> [" + id + "]",
				"Echec de suppression du lecteur! Lecteur @[" + id + "] introuvable."
	    	);
    		return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    	}
    	
    	// Delete the reader by id
    	userService.deleteById(id);
    	// Reset reader variable
    	reader = null;
    	// Find the reader by id again
    	reader = userService.findById(id);
    	
    	// Create a response with the result of the delete
    	ApiResponse response = new ApiResponse(
			reader == null ? 200 : 500,
			"Id -> [" + id + "]",
			reader == null ? "Lecteur supprimé avec succès!" : "Echec de suppression du lecteur!"
    	);
    	
    	// Return a ResponseEntity with the ApiResponse and the appropriate status code
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    
    /*
     *  Gestion des notifications
     */
    
    /**
     * Sends a notification to a user.
     * 
     * @param  id		The id of the user to send the notification to
     * @param  request	The notification request containing the content of the notification
     * @return         	A ResponseEntity containing an ApiResponse with the result of the notification sending
     */
    @Transactional
    @PostMapping("/notifications/{id}")
    public ResponseEntity<ApiResponse> sendNotification(
    		@PathVariable("id") Long id, 
    		@RequestBody NotificationRequest request
	) {
    	// Find the user by id
    	ModelUser reader = userService.findById(id);
		
    	// If the user is not found, create a failure response
    	if(reader == null) {
    		ApiResponse response = new ApiResponse(
				404,
				reader,
				"Echec de notification! Lecteur @[" + id + "] introuvable!."
	    	);
			return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    	}
    	
    	// If the content of the notification is empty, create a failure response
    	if(request.getContent() == null || request.getContent().isBlank()) {
    		ApiResponse response = new ApiResponse(
				400,
				reader,
				"Echec de notification! Vérifiez vos informations."
	    	);
			return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    	}
    	
    	// Create a new notification and associate it with the user
    	ModelNotification notification = new ModelNotification();
        reader.getNotifications().add(notification);
        notification.setUser(reader);
        notification.setMessage(request.getContent());
        notification.setSentDate(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
        
        // Save the user and the notification
        userService.save(reader);
        ModelNotification notificationSent = notificationService.save(notification);
        
        try {
   			// Set the subject and send the notification via email
   			globalMailSubject = "AsLibrary | Notification";
			emailService.sendSimpleMessage(reader.getEmail(), globalMailSubject, notification.getMessage());
		} catch (Exception e) {
			// If there is an error sending the notification, ignore it
		}
        
    	// Create a response with the result of the notification sending
    	ApiResponse response = new ApiResponse(
    		notificationSent != null ? 200 : 400,
			notificationSent,
			notificationSent != null ? "Notification envoyée avec succès!" : "Echec de notification!"
    	);
    	
    	// Return a ResponseEntity with the ApiResponse and the appropriate status code
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    /**
     * Retrieves all notifications of a user.
     *
     * @param  id  The id of the user.
     * @return     A ResponseEntity containing a ApiResponse with a status code, a list of notifications,
     *             and a success message.
     */
    @GetMapping("/notifications/{userId}")
    public ResponseEntity<ApiResponse> getNotificationByUser(@PathVariable("userId") Long id) {
    	// Find the user by id
    	ModelUser reader = userService.findById(id);
    	
    	// If the user is not found, create a failure response
    	if(reader == null) {
    		ApiResponse response = new ApiResponse(
				404,
				reader,
				"Echec de récupération des notifications. Lecteur @[" + id + "] introuvable!."
	    	);
			return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    	}
    	
    	// Create a response with the list of notifications
    	ApiResponse response = new ApiResponse(
			200,
			reader.getNotifications(),
			"Notifications récupérées avec succès!"
    	);

    	// Return a ResponseEntity with the ApiResponse and the appropriate status code
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    
    /*
	 * Gestion des livres
	 */
    
    /**
     * Retrieves all books from the database.
     *
     * @return A ResponseEntity containing a ApiResponse with a status code, a list of books,
     *         and a success message.
     */
    @GetMapping("/books/all")
    public ResponseEntity<ApiResponse> getAllbooks() {
    	
    	// Fetch all books from the service
    	List<ModelBook> books = bookService.findAll();
    	
    	// Create a response with the list of books
    	ApiResponse response = new ApiResponse(
			200, // HTTP Status code 200 OK
			books, // List of books
			"Liste des livres récupérée avec succès!" // Success message
    	);

    	// Return a ResponseEntity with the ApiResponse and the appropriate status code
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode()))
    			.body(response);
    }
    
    /**
     * Retrieves the top N books from the database.
     *
     * @param n The number of books to retrieve.
     * @return A ResponseEntity containing a ApiResponse with a status code, a list of books,
     *         and a success message.
     */
    @GetMapping("/books/top/{n}")
    public ResponseEntity<ApiResponse> getTopNBooks(@PathVariable int n) {
        // Fetch the top N books from the service
    	List<ModelBook> books = bookService.findTopNBooksWithLimit(n);
    	
    	// Create a response with the list of books
    	ApiResponse response = new ApiResponse(
			200, // HTTP Status code 200 OK
			books, // List of books
			books.size() + " livre(s) récupéré(s) avec succès!" // Success message
    	);

    	// Return a ResponseEntity with the ApiResponse and the appropriate status code
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode()))
    			.body(response);
    }
    
    /**
     * Retrieves a book by its ID from the database.
     *
     * @param  id The ID of the book to retrieve.
     * @return    A ResponseEntity containing a ApiResponse with a status code, a book, and a success message.
     */
    @GetMapping("/books/{id}")
    public ResponseEntity<ApiResponse> getBookById(@PathVariable("id") Long id) {
    	
    	/* Fetch the book with the given ID from the service */
    	ModelBook book = null;
    	book = bookService.findById(id);
    	
    	/* Create a response with the book and a success message */
    	ApiResponse response = new ApiResponse(
			book != null ? 200 : 404, // HTTP Status code 200 OK if book is found, 404 if not
			book, // The retrieved book
			book != null ? "Livre @[" + id + "] récupéré avec succès!" : "Livre introuvable!" // Success message
    	);
    	
    	/* Return a ResponseEntity with the ApiResponse and the appropriate status code */
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
	@Transactional
	@PostMapping("/books")
	/**
	 * Create a new book.
	 *
	 * @param  request The request containing the data for the new book.
	 * @return         A ResponseEntity containing a ApiResponse with a status code, a book, and a success message.
	 */
	public ResponseEntity<ApiResponse> createBook(@RequestBody BookRequest request) {

		// Check if the request is valid
		if(request == null || (
			request.getAuthors() == null || request.getAuthors().isBlank() ||
			request.getCategory() == null || request.getCategory().isBlank() ||
			request.getCode() == null || request.getCode().isBlank() ||
			request.getDescription() == null || request.getDescription().isBlank() ||
			request.getTitle() == null || request.getTitle().isBlank() ||
			request.getCopies() == null ||
			request.getYear() == null
		)) {
			// If the request is not valid, return a response with a 400 status code and an error message
			ApiResponse response = new ApiResponse(
				400,
				null,
				"Echec de création du livre!"
			);
			return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
		}

		// Create a new book object with the data from the request
		ModelBook book = new ModelBook();
		book.setTitle(request.getTitle());
		book.setDescription(request.getDescription());
		book.setCategory(request.getCategory());
		book.setYear(request.getYear());
		book.setAuthors(request.getAuthors());
		book.setCode(request.getCode());
		book.setCopies(request.getCopies());

		// Save the book
		ModelBook bookCreated = bookService.save(book);

		// Create a response with the created book and a success message
		ApiResponse response = new ApiResponse(
			bookCreated != null ? 200 : 400,
			bookCreated,
			bookCreated != null ? "Livre créér avec succès!" : "Echec de création du livre!"
		);

		// Return the response with the appropriate status code
		return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
	}
    
	/**
     * Updates a book with the given id using the data in the request.
     *
     * @param  id            the id of the book to update
     * @param  request       the data to update the book with
     * @return                a ResponseEntity containing an ApiResponse with the result of the update
     */
    @Transactional
    @PutMapping("/books/{id}")
    public ResponseEntity<ApiResponse> updateBook(@PathVariable("id") Long id, @RequestBody BookRequest request) {
    	
    	// Find the book to update
    	ModelBook newbook = bookService.findById(id);
    	
    	// If the book is not found, return an error response
    	if(newbook == null) {
    		ApiResponse response = new ApiResponse(
				404,
				request,
				"Echec de mise à jour du livre! Livre @[" + id + "] introuvable!."
	    	);
			return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    	}
    	
    	// Check if the request is not null
    	if(request == null) {
    		// Return an error response if the request is null or empty
			ApiResponse response = new ApiResponse(
				400,
				request,
				"Echec de mise à jour du livre! \n." +
				"Informations incorrect. Veuillez vérifier vos informations s'il vous plaît !"
	    	);
			return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    	}
    	
    	// Update the book's attributes if the request contains valid values
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
   		
    	// Save the updated book and create a response
    	ModelBook bookUpdated = bookService.save(newbook);
    	ApiResponse response = new ApiResponse(
			bookUpdated != null ? 200 : 500,
			bookUpdated,
			bookUpdated != null ? "Livre mis à jour avec succès!" : "Echec de mise à jour du livre!"
    	);
    	
    	// Return the response with the appropriate status code
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    /**
     * Deletes a book with the given id.
     *
     * @param  id            the id of the book to delete
     * @return                a ResponseEntity containing an ApiResponse with the result of the delete operation
     */
    @Transactional
    @DeleteMapping("/books/{id}")
    public ResponseEntity<ApiResponse> deleteBook(@PathVariable("id") Long id) {
    	
    	// Find the book to delete
    	ModelBook book = bookService.findById(id);
    	
    	// If the book is not found, return an error response
    	if(book == null) {
    		// Create the error response
    		ApiResponse response = new ApiResponse(
				404,
				"Id -> [" + id + "]",
				"Echec de suppression du livre! Livre @[" + id + "] introuvable."
	    	);
    		
    		// Return the error response with the appropriate status code
    		return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    	}
    	
    	// Delete the book
    	bookService.deleteById(id);
    	
    	// Find the book again to check if it has been deleted
    	book = bookService.findById(id);
    	
    	// Create the response
    	ApiResponse response = new ApiResponse(
			book == null ? 200 : 500,
			"Id -> [" + id + "]",
			book == null ? "Livre supprimé avec succès!" : "Echec de suppression du livre!"
    	);
    	
    	// Return the response with the appropriate status code
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    
    /*
     * Gestion des emprunts 
     */
    
    /**
     * Retrieves the loans of a user.
     *
     * @param  id  The id of the user.
     * @return     A ResponseEntity containing a ApiResponse with a status code, a list of loans,
     *             and a success message.
     */
    @GetMapping("/loans/{userId}")
    public ResponseEntity<ApiResponse> getLoansByUser(@PathVariable("userId") Long id) {
    	
    	// Find the user by id
    	ModelUser reader = userService.findById(id);
    	
    	// If the user is not found, create a failure response
    	ApiResponse response = new ApiResponse(
			reader == null ? 404 : 200, // Status code
			reader == null ? null : reader.getLoans(), // List of loans
			reader == null ? "Utilisateur introuvable!" : "Emprunts récupérés avec succès!" // Success message
    	);

    	// Return a ResponseEntity with the ApiResponse and the appropriate status code
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    /**
     * Retrieves a loan by its id.
     *
     * @param  id  The id of the loan.
     * @return     A ResponseEntity containing a ApiResponse with a status code, the loan,
     *             and a success message.
     */
    @GetMapping("/loans/read/{id}")
    public ResponseEntity<ApiResponse> getLoanById(@PathVariable("id") Long id) {
    	
    	/* Find the loan by id */
    	ModelLoan loan = loanService.findById(id);
    	
    	/* Create the ApiResponse object with the appropriate status code, loan, and message */
    	ApiResponse response = new ApiResponse(
			loan != null ? 200 : 404, // Status code
			loan, // Loan
			loan != null ? "Emprunt @[" + id + "] récupéré avec succès!" : 
					"Echec de récupération de l'emprunt! Emprunt @[" + id + "] introuvable." // Success message
    	);
    	
    	/* Return the response with the appropriate status code */
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
    
    /**
     * Updates a loan by returning it.
     * 
     * @param  id        The id of the loan to be returned.
     * @param  request   The request containing the return date.
     * @return           The HTTP response containing the updated loan.
     */
    @Transactional
    @PutMapping("/loans/{id}/return")
    public ResponseEntity<ApiResponse> returnLoan(
    		@PathVariable("id") Long id,
    		@RequestBody LoanRequest request
	) {
    	
    	// Check if request is not null
    	if(request == null) {
			ApiResponse response = new ApiResponse(
				400,
				null,
				"Echec d'emprunt du livre! Vérifiez vos informations."
	    	);
    		return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
		}
    	
    	// Find the loan by id
    	ModelLoan loan = loanService.findById(id);
		
		// Check if the loan exists
		if(loan == null) {
			ApiResponse response = new ApiResponse(
				404,
				null,
				"Echec de mise à jour de l'emprunt! "+
				"Emprunt @[" + id + "] introuvable."
	    	);
    		return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
		}
		
		// Update the return date of the loan and update the number of copies
		if(request.getReturnDate() != null) {
			loan.setReturnDate(request.getReturnDate());
			// Increase the number of copies
			loan.getBook().setCopies(loan.getBook().getCopies() + loan.getCopies());
		} else {
			loan.setReturnDate(null);
			// Decrease the number of copies
			loan.getBook().setCopies(loan.getBook().getCopies() - loan.getCopies());
		}
		
		// Save the updated loan
		ModelLoan loanUpdated = loanService.save(loan);
    	
    	// Create the HTTP response
    	ApiResponse response = new ApiResponse(
			loanUpdated != null ? 200 : 400,
			loanUpdated,
			loanUpdated != null ? "Retour d'emprunt enrégistré avec succès!" : "Echec de retour d'emprunt du livre!"
    	);
    	
    	// Return the HTTP response
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    /**
     * Deletes a loan by its id. If the loan is not returned yet, it will increase the
     * number of copies of the related book.
     * 
     * @param  id The id of the loan to be deleted.
     * @return    The HTTP response containing the result of the deletion.
     */
    @Transactional
    @DeleteMapping("/loans/{id}")
    public ResponseEntity<ApiResponse> deleteLoan(@PathVariable("id") Long id) {
    	
    	// Find the loan by id
    	ModelLoan loan = loanService.findById(id);
    	
    	// If the loan does not exist, return error response
    	if(loan == null) {
    		ApiResponse response = new ApiResponse(
				404,
				"Id -> [" + id + "]",
				"Echec de suppression de l'emprunt! Emprunt @[" + id + "] introuvable."
	    	);
    		return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    	}
    	
    	// If the loan is not returned, increase the number of copies of the related book
		if(loan.getReturnDate() == null) {
			loan.getBook().setCopies(loan.getBook().getCopies() + loan.getCopies());	
		}
		
		// Save the updated book
		bookService.save(loan.getBook());
		
		// Delete the loan
		loanService.deleteById(id);
		
		// Find the deleted loan
		ModelLoan loanDeleted = loanService.findById(id);
    	
    	// Create the HTTP response
    	ApiResponse response = new ApiResponse(
			loanDeleted == null ? 200 : 500,
			"Id -> [" + id + "]",
			loanDeleted == null ? "Emprunt supprimé avec succès!" : "Echec de suppression de l'emprunt!"
    	);
    	
    	// Return the HTTP response
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    /*
     * Gestion des reservations
     */
    
    /**
     * Retrieves all reservations of a user.
     *
     * @param  id  The id of the user.
     * @return     A ResponseEntity containing a ApiResponse with a status code, a list of reservations,
     *             and a success message.
     */
    @GetMapping("/reservations/{userId}")
    public ResponseEntity<ApiResponse> getReservationsByUser(@PathVariable("userId") Long id) {
    	// Find the user by id
    	ModelUser reader = userService.findById(id);
    	
    	// Create a response with the list of reservations
    	ApiResponse response = new ApiResponse(
			reader != null ? 200 : 404, // Status code
			reader != null ? reader.getReservations() : null, // List of reservations
			reader != null ? "Reservations récupérées avec succès!" : "Informations non retouvées" // Success message
    	);

    	// Return a ResponseEntity with the ApiResponse and the appropriate status code
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    /**
     * Retrieves a reservation by its id.
     *
     * @param  id  The id of the reservation.
     * @return     A ResponseEntity containing a ApiResponse with a status code, the reservation,
     *             and a success message.
     */
    @GetMapping("/reservations/read/{id}")
    public ResponseEntity<ApiResponse> getReservationsById(@PathVariable("id") Long id) {
    	
    	// Find the reservation by id
    	ModelReservation reservation = reservationService.findById(id);
    	
    	// Create a response with the reservation, status code and success message
    	ApiResponse response = new ApiResponse(
    			reservation != null ? 200 : 404, // Status code
    			reservation, // Reservation
    			reservation != null ? 
    					"Reservation @[" + id + "] récupérée avec succès!" : // Success message
    					"Echec de récupération de la reservation! Reservation @[" + id + "] introuvable." // Error message
    	);
    	
    	// Return the HTTP response
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
    
    /**
     * Updates the status of a reservation and sends a confirmation email if the status is updated successfully.
     *
     * @param id The ID of the reservation to update.
     * @param request The request object containing the new status.
     * @return The response entity containing the updated reservation and a success message if the update is successful, otherwise an error message.
     */
    @Transactional
    @PutMapping("/reservations/{id}/status")
    public ResponseEntity<ApiResponse> reservationStatus(
            @PathVariable("id") Long id,
            @RequestBody ReservationRequest request
    ) {
        /* Check if the request is valid */
        if(request == null || request.getCounter() < 0) {
            ApiResponse response = new ApiResponse(
                    400,
                    null,
                    "Failed to update reservation status! Check your information."
            );
            return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
        }

        /* Find the reservation by ID */
        ModelReservation reservation = reservationService.findById(id);

        /* If reservation is not found, return an error response */
        if(reservation == null) {
            ApiResponse response = new ApiResponse(
                    404,
                    null,
                    "Failed to update reservation! Reservation @[" + id + "] not found."
            );
            return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
        }

        /* Update the reservation date if the status is true, otherwise set it to null */
        if(request.isStatus()) {
            Date reservationDate = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
            reservation.setReservationDate(reservationDate);
        } else {
            reservation.setReservationDate(null);
        }

        /* Save the updated reservation */
        ModelReservation reservationUpdated = reservationService.save(reservation);

        /* If the reservation is updated successfully, send a confirmation email */
        if(reservationUpdated != null) {
            try {
                String mailContent = "Bonjour ! \n" +
                        "Votre reservation de livrechez AsLibrary a été valider avec succès ! \n" +
                        "Prenez note des Références [ \n" +
                        "\n\t Titre : " + reservationUpdated.getBook().getTitle() +
                        "\n\t Nombre d'exemplaires : " + reservationUpdated.getCopies() + 
                        "]\n\n" + 
                        "Vous pouvez vous connectez sur le site en ligne de la bibliothèque " + 
                        "pour faire vos réservations et consulter vos status d'emprunts. \n\n" +
                        "AsLibrary | Votre bibliothèque préférée !!!";
                globalMailSubject = "AsLibrary | Reservation";
                emailService.sendSimpleMessage(reservationUpdated.getUser().getEmail(), globalMailSubject, mailContent);
            } catch (Exception e) {
                /* Do nothing on exception */
            }
        }

        /* Create the API response with the updated reservation and a success/error message */
        ApiResponse response = new ApiResponse(
                reservationUpdated != null ? 200 : 400,
                reservationUpdated,
                reservationUpdated != null ? "Reservation updated successfully!" : "Failed to update reservation!"
        );

        /* Return the response entity */
        return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
    
    /**
     * Deletes a reservation by its ID.
     *
     * @param  id  The ID of the reservation to be deleted.
     * @return     A ResponseEntity containing a ApiResponse with a status code,
     *             the ID of the deleted reservation, and a success/error message.
     */
    @Transactional
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<ApiResponse> deleteReservation(@PathVariable("id") Long id) {
    	
    	/* Find the reservation by ID */
    	ModelReservation reservation = reservationService.findById(id);
    	
    	/* If reservation is not found, return an error response */
    	if(reservation == null) {
    		ApiResponse response = new ApiResponse(
				404,
				"Id -> [" + id + "]",
				"Echec de suppression de la reservation! Reservation @[" + id + "] introuvable."
	    	);
    		return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    	}
    	
    	/* Delete the reservation */
    	reservationService.deleteById(id);
    	
    	/* Find the deleted reservation */
    	ModelReservation reservationDeleted = reservationService.findById(id);
    	
    	/* Create the API response with the ID of the deleted reservation and a success/error message */
    	ApiResponse response = new ApiResponse(
			reservationDeleted == null ? 200 : 500,
			"Id -> [" + id + "]",
			reservationDeleted == null ? "Reservation supprimée avec succès!" : "Echec de suppression de la reservation!"
    	);
    	
    	/* Return the response entity */
    	return ResponseEntity.status(HttpStatusCode.valueOf(response.getResponseCode())).body(response);
    }
}
