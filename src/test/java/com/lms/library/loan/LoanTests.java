package com.lms.library.loan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.lms.library.api.ApiResponse;
import com.lms.library.configurations.TestConfig;
import com.lms.library.models.ModelBook;
import com.lms.library.models.ModelLoan;
import com.lms.library.models.ModelUser;
import com.lms.library.requests.LoanRequest;
import com.lms.library.services.BookService;
import com.lms.library.services.EmailService;
import com.lms.library.services.LoanService;
import com.lms.library.services.UserService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = TestConfig.class)
@TestPropertySource(locations = "classpath:application-test.yaml")
@ActiveProfiles("test")
public class LoanTests {

	@Autowired
	private TestRestTemplate restTemplate;
	@MockBean
	private UserService userService;
	@MockBean
	private EmailService emailService;
	LoanService loanServiceMock = Mockito.mock(LoanService.class);
	BookService bookServiceMock = Mockito.mock(BookService.class);
	
	@BeforeEach
	public void setUp() {
	    restTemplate.getRestTemplate().setUriTemplateHandler(new DefaultUriBuilderFactory(TestConfig.baseUrl));
	}
	
    // Should return a 200 status code and a list of loans when given a valid user ID
    @Test
    public void test_valid_user_id() {
        // Arrange
        Long userId = 9L;
        
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
    
        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
            "/api/loans/" + userId,
            HttpMethod.GET,
            entity,
            ApiResponse.class
        );

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getBody().getResponseCode());
        assertNotNull(response.getBody().getBody());
        assertEquals("Emprunts récupérés avec succès!", response.getBody().getMessage());
    }
    
    // Should return a 404 status code and an error message when given an invalid user ID
    @Test
    public void test_invalid_user_id() {
    	// Arrange
        Long userId = 90000L;
        
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
    
        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
            "/api/loans/" + userId,
            HttpMethod.GET,
            entity,
            ApiResponse.class
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getResponseCode());
        assertNull(response.getBody().getBody());
        assertEquals("Utilisateur introuvable!", response.getBody().getMessage());
    }
    
    // Should return 200 status code and the loan object when a valid loan id is provided
    @Test
    public void test_valid_loan_id() {
        // Arrange
        Long loanId = 11L;
        ModelLoan loan = new ModelLoan();
        loan.setId(loanId);
        Mockito.when(loanServiceMock.findById(loanId)).thenReturn(loan);

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity("/api/loans/read/" + loanId, ApiResponse.class);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getBody().getResponseCode());
        assertEquals("Emprunt @[" + loanId + "] récupéré avec succès!", response.getBody().getMessage());
    }
    
    // Should handle null loan object returned from loanService.findById() method
    @Test
    public void test_null_loan_object() {
        // Arrange
        Long loanId = 0L;
        Mockito.when(loanServiceMock.findById(loanId)).thenReturn(null);

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity("/api/loans/read/" + loanId, ApiResponse.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getResponseCode());
        assertNull(response.getBody().getBody());
        assertEquals("Echec de récupération de l'emprunt! Emprunt @[" + loanId + "] introuvable.", response.getBody().getMessage());
    }
    
    // Successfully create a loan when given valid loan request with existing book code and username
    @Test
    public void test_createLoan_success() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        LoanRequest request = new LoanRequest();
        request.setCode("PAJ1813");
        request.setDue(new Date());
        request.setUsername("@asistem");
        request.setCounter(1);
        HttpEntity<LoanRequest> entity = new HttpEntity<>(request, headers);
    
        // Mock the book service
        List<ModelBook> books = new ArrayList<>();
        ModelBook book = new ModelBook();
        book.setCode("PAJ1813");
        book.setCopies(5);
        books.add(book);
        Mockito.when(bookServiceMock.findByCode(Mockito.anyString())).thenReturn(books);
    
        // Mock the user service
        ModelUser reader = new ModelUser();
        reader.setUsername("@asistem");
        reader.setEmail("asistemcdls@gmail.com.com");
        Mockito.when(userService.findByUsernameOrEmail(Mockito.anyString(), Mockito.anyString())).thenReturn(reader);
    
        // Mock the loan service
        ModelLoan loan = new ModelLoan();
        loan.setBook(book);
        loan.setUser(reader);
        Mockito.when(loanServiceMock.save(Mockito.any(ModelLoan.class))).thenReturn(loan);
    
        // Mock the email service
        Mockito.doNothing().when(emailService).sendSimpleMessage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    
        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange("/api/loans", HttpMethod.POST, entity, ApiResponse.class);
    
        // Assert
        assertEquals(HttpStatus.OK.value(), response.getBody().getResponseCode());
        assertNotNull(response.getBody().getBody());
        assertEquals("Livre emprunter avec succès!", response.getBody().getMessage());
    }
    
    // Failed create a loan when given valid loan request with book copies more than counter...
    @Test
    public void test_createLoan_counter_less() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        LoanRequest request = new LoanRequest();
        request.setCode("PAJ1813");
        request.setDue(new Date());
        request.setUsername("@asistem");
        request.setCounter(10000);
        HttpEntity<LoanRequest> entity = new HttpEntity<>(request, headers);
    
        // Mock the book service
        List<ModelBook> books = new ArrayList<>();
        ModelBook book = new ModelBook();
        book.setCode("PAJ1813");
        book.setCopies(5);
        books.add(book);
        Mockito.when(bookServiceMock.findByCode(Mockito.anyString())).thenReturn(books);
    
        // Mock the user service
        ModelUser reader = new ModelUser();
        reader.setUsername("@asistem");
        reader.setEmail("asistemcdls@gmail.com.com");
        Mockito.when(userService.findByUsernameOrEmail(Mockito.anyString(), Mockito.anyString())).thenReturn(reader);
    
        // Mock the loan service
        ModelLoan loan = new ModelLoan();
        loan.setBook(book);
        loan.setUser(reader);
        Mockito.when(loanServiceMock.save(Mockito.any(ModelLoan.class))).thenReturn(loan);
    
        // Mock the email service
        Mockito.doNothing().when(emailService).sendSimpleMessage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    
        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange("/api/loans", HttpMethod.POST, entity, ApiResponse.class);
    
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getResponseCode());
        assertNull(response.getBody().getBody());
        assertEquals("Echec d'emprunt du livre! Nombre d'exemplaires insuffisants.", response.getBody().getMessage());
    }
    
    // Failed create a loan when given valid loan request with no existing book code or username
    @Test
    public void test_createLoan_no_existing_book() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        LoanRequest request = new LoanRequest();
        request.setCode("PAJ1hcherjkh813");
        request.setDue(new Date());
        request.setUsername("@asistemcdls");
        request.setCounter(10000);
        HttpEntity<LoanRequest> entity = new HttpEntity<>(request, headers);
    
        // Mock the book service
        List<ModelBook> books = new ArrayList<>();
        ModelBook book = new ModelBook();
        book.setCode("PAJ1813");
        book.setCopies(5);
        books.add(book);
        Mockito.when(bookServiceMock.findByCode(Mockito.anyString())).thenReturn(books);
    
        // Mock the user service
        ModelUser reader = new ModelUser();
        reader.setUsername("@asistem");
        reader.setEmail("asistemcdls@gmail.com.com");
        Mockito.when(userService.findByUsernameOrEmail(Mockito.anyString(), Mockito.anyString())).thenReturn(reader);
    
        // Mock the loan service
        ModelLoan loan = new ModelLoan();
        loan.setBook(book);
        loan.setUser(reader);
        Mockito.when(loanServiceMock.save(Mockito.any(ModelLoan.class))).thenReturn(loan);
    
        // Mock the email service
        Mockito.doNothing().when(emailService).sendSimpleMessage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    
        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange("/api/loans", HttpMethod.POST, entity, ApiResponse.class);
    
        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getResponseCode());
        assertNull(response.getBody().getBody());
        assertEquals("Echec d'emprunt du livre! Livre @[" + request.getCode() + "]" +
				"ou Lecteur @[" + request.getUsername() + "]" +
				" introuvable.", response.getBody().getMessage());
    }
    
    // Failed create a loan when given null loan request values
    @Test
    public void test_createLoan_null_data() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        LoanRequest request = new LoanRequest();
        request.setCode(null);
        request.setDue(null);
        request.setUsername(null);
        request.setCounter(0);
        HttpEntity<LoanRequest> entity = new HttpEntity<>(request, headers);
    
        // Mock the book service
        List<ModelBook> books = new ArrayList<>();
        ModelBook book = new ModelBook();
        book.setCode("PAJ1813");
        book.setCopies(5);
        books.add(book);
        Mockito.when(bookServiceMock.findByCode(Mockito.anyString())).thenReturn(books);
    
        // Mock the user service
        ModelUser reader = new ModelUser();
        reader.setUsername("@asistem");
        reader.setEmail("asistemcdls@gmail.com.com");
        Mockito.when(userService.findByUsernameOrEmail(Mockito.anyString(), Mockito.anyString())).thenReturn(reader);
    
        // Mock the loan service
        ModelLoan loan = new ModelLoan();
        loan.setBook(book);
        loan.setUser(reader);
        Mockito.when(loanServiceMock.save(Mockito.any(ModelLoan.class))).thenReturn(loan);
    
        // Mock the email service
        Mockito.doNothing().when(emailService).sendSimpleMessage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    
        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange("/api/loans", HttpMethod.POST, entity, ApiResponse.class);
    
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getResponseCode());
        assertNull(response.getBody().getBody());
        assertEquals("Echec d'emprunt du livre! Vérifiez vos informations.", response.getBody().getMessage());
    }
    
    // The loan exists and the request contains a valid return date. 
    //The loan is updated with the return date and the number of copies of the book is increased by the number of copies in the loan.
    @Test
    public void test_valid_return_date() {
        // Arrange
        Long id = 17L;
        LoanRequest request = new LoanRequest();
        Date returnDate = new Date();
        request.setReturnDate(returnDate);

        ModelLoan loan = new ModelLoan();
        loan.setCopies(2);
        loan.setBook(new ModelBook());

        Mockito.when(loanServiceMock.findById(id)).thenReturn(loan);

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
            "/api/loans/{id}/return",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            ApiResponse.class,
            id
        );

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getBody().getResponseCode());
    }
    
    // The loan exists and the request contains a null return date. 
    //The loan is updated with the null date and the number of copies of the book is decreased by the number of copies in the loan.
    @Test
    public void test_invalid_return_date() {
        // Arrange
        Long id = 17L;
        LoanRequest request = new LoanRequest();
        request.setReturnDate(null);

        ModelLoan loan = new ModelLoan();
        loan.setCopies(2);
        loan.setBook(new ModelBook());

        Mockito.when(loanServiceMock.findById(id)).thenReturn(loan);

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
            "/api/loans/{id}/return",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            ApiResponse.class,
            id
        );

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getBody().getResponseCode());
    }
    
    // The loan doesn't exists.
    @Test
    public void test_invalid_return_by_no_valid_loan() {
        // Arrange
        Long id = 0L;
        LoanRequest request = new LoanRequest();
        request.setReturnDate(null);

        ModelLoan loan = new ModelLoan();
        loan.setCopies(2);
        loan.setBook(new ModelBook());

        Mockito.when(loanServiceMock.findById(id)).thenReturn(loan);

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
            "/api/loans/{id}/return",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            ApiResponse.class,
            id
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getResponseCode());
    }
    
    // loan exists and is not returned, book copies are updated, loan is deleted, and success response is returned
    @Test
    public void test_loan_exists_and_not_returned() {
        // Arrange
        Long loanId = 21L;
        ModelLoan loan = new ModelLoan();
        loan.setId(loanId);
        loan.setReturnDate(null);

        Mockito.when(loanServiceMock.findById(loanId)).thenReturn(loan);

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange("/api/loans/{id}", HttpMethod.DELETE, null, ApiResponse.class, loanId);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getBody().getResponseCode());
    }
    
    // loan does not exist, error response is returned
    @Test
    public void test_loan_does_not_exist() {
        // Arrange
        Long loanId = 0L;

        Mockito.when(loanServiceMock.findById(loanId)).thenReturn(null);

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
            "/api/loans/{id}",
            HttpMethod.DELETE,
            null,
            ApiResponse.class,
            loanId
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getResponseCode());
    }
    
}
