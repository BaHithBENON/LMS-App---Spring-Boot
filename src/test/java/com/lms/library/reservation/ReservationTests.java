package com.lms.library.reservation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
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
import com.lms.library.models.ModelReservation;
import com.lms.library.models.ModelUser;
import com.lms.library.requests.ReservationRequest;
import com.lms.library.services.BookService;
import com.lms.library.services.EmailService;
import com.lms.library.services.LoanService;
import com.lms.library.services.ReservationService;
import com.lms.library.services.UserService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = TestConfig.class)
@TestPropertySource(locations = "classpath:application-test.yaml")
@ActiveProfiles("test")
public class ReservationTests {

	@Autowired
	private TestRestTemplate restTemplate;
	@MockBean
	private UserService userService;
	@MockBean
	private EmailService emailService;
	@MockBean
	private ReservationService reservationService;
	LoanService loanServiceMock = Mockito.mock(LoanService.class);
	BookService bookServiceMock = Mockito.mock(BookService.class);
	
	@BeforeEach
	public void setUp() {
	    restTemplate.getRestTemplate().setUriTemplateHandler(new DefaultUriBuilderFactory(TestConfig.baseUrl));
	}
	
	// Should return a 200 status code and a list of reservations when given a valid user ID
    @Test
    public void test_valid_get_all_reservations() {
        // Arrange
        Long userId = 9L;
        
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
    
        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
            "/api/reservations/" + userId,
            HttpMethod.GET,
            entity,
            ApiResponse.class
        );

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getBody().getResponseCode());
        assertNotNull(response.getBody().getBody());
        assertEquals("Reservations récupérées avec succès!", response.getBody().getMessage());
    }
    
    // Should return a 404 status code and an error message when given an invalid user ID
    @Test
    public void test_invalid_get_all_reservations() {
    	// Arrange
        Long userId = 90000L;
        
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
    
        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
            "/api/reservations/" + userId,
            HttpMethod.GET,
            entity,
            ApiResponse.class
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getResponseCode());
        assertNull(response.getBody().getBody());
        assertEquals("Informations non retouvées", response.getBody().getMessage());
    }
    
    // Should return 200 status code and the reservation object when a valid id is provided
    @Test
    public void test_valid_get_reservation() {
        // Arrange
        Long id = 6L;
        ModelReservation loan = new ModelReservation();
        loan.setId(id);
        Mockito.when(reservationService.findById(id)).thenReturn(loan);

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity("/api/reservations/read/" + id, ApiResponse.class);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getBody().getResponseCode());
        assertEquals("Reservation @[" + id + "] récupérée avec succès!", response.getBody().getMessage());
    }
    
    // Should return 404 status code and the null reservation object when an invalid id is provided
    @Test
    public void test_invalid_get_reservation() {
        // Arrange
        Long id = 0L;
        ModelReservation loan = new ModelReservation();
        loan.setId(id);
        Mockito.when(reservationService.findById(id)).thenReturn(loan);

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity("/api/reservations/read/" + id, ApiResponse.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getResponseCode());
        assertEquals("Echec de récupération de la reservation! Reservation @[" + id + "] introuvable.", response.getBody().getMessage());
    }
    
    // Successfully reserve a book with valid request data
    @Test
    public void test_successful_reservation() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ReservationRequest request = new ReservationRequest();
        request.setCode("ALC1988");
        request.setCounter(2);
        request.setUsername("@asistem");
        HttpEntity<ReservationRequest> entity = new HttpEntity<>(request, headers);
    
        // Mock the book service
        List<ModelBook> books = new ArrayList<>();
        ModelBook book = new ModelBook();
        book.setCode("ALC1988");
        book.setCopies(5);
        books.add(book);
        Mockito.when(bookServiceMock.findByCode(Mockito.anyString())).thenReturn(books);
    
        // Mock the user service
        ModelUser reader = new ModelUser();
        reader.setUsername("@asistem");
        reader.setEmail("asistemcdls@gmail.com");
        Mockito.when(userService.findByUsernameOrEmail(Mockito.anyString(), Mockito.anyString())).thenReturn(reader);
    
        // Mock the reservation service
        ModelReservation reservation = new ModelReservation();
        reservation.setBook(book);
        reservation.setUser(reader);
        reservation.setCopies(2);
        Mockito.when(reservationService.save(Mockito.any(ModelReservation.class))).thenReturn(reservation);
    
        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange("/api/reservations", HttpMethod.POST, entity, ApiResponse.class);
    
        // Assert
        assertEquals(HttpStatus.OK.value(), response.getBody().getResponseCode());
        assertNotNull(response.getBody().getBody());
        assertEquals("Livre reserver avec succès!", response.getBody().getMessage());
    }
    
    // Failed reserve a book with valid request data and with book copies more than counter
    @Test
    public void test_failed_reservation_by_counter() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ReservationRequest request = new ReservationRequest();
        request.setCode("ALC1988");
        request.setCounter(20000);
        request.setUsername("@asistem");
        HttpEntity<ReservationRequest> entity = new HttpEntity<>(request, headers);
    
        // Mock the book service
        List<ModelBook> books = new ArrayList<>();
        ModelBook book = new ModelBook();
        book.setCode("ALC1988");
        book.setCopies(5);
        books.add(book);
        Mockito.when(bookServiceMock.findByCode(Mockito.anyString())).thenReturn(books);
    
        // Mock the user service
        ModelUser reader = new ModelUser();
        reader.setUsername("@asistem");
        reader.setEmail("asistemcdls@gmail.com");
        Mockito.when(userService.findByUsernameOrEmail(Mockito.anyString(), Mockito.anyString())).thenReturn(reader);
    
        // Mock the reservation service
        ModelReservation reservation = new ModelReservation();
        reservation.setBook(book);
        reservation.setUser(reader);
        reservation.setCopies(2);
        Mockito.when(reservationService.save(Mockito.any(ModelReservation.class))).thenReturn(reservation);
    
        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange("/api/reservations", HttpMethod.POST, entity, ApiResponse.class);
    
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getResponseCode());
        assertNull(response.getBody().getBody());
        assertEquals("Echec de reservation du livre! Nombre d'exemplaires insuffisants.", response.getBody().getMessage());
    }
    
    // Failed reserve a book with valid request data and with no existing book code or username
    @Test
    public void test_failed_reservation_by_no_existing_data() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ReservationRequest request = new ReservationRequest();
        request.setCode("ALC19jrjjhjvrhj88");
        request.setCounter(20000);
        request.setUsername("@cjejlejrfjr");
        HttpEntity<ReservationRequest> entity = new HttpEntity<>(request, headers);
    
        // Mock the book service
        List<ModelBook> books = new ArrayList<>();
        ModelBook book = new ModelBook();
        book.setCode("ALC1988");
        book.setCopies(5);
        books.add(book);
        Mockito.when(bookServiceMock.findByCode(Mockito.anyString())).thenReturn(books);
    
        // Mock the user service
        ModelUser reader = new ModelUser();
        reader.setUsername("@asistemjhrj");
        reader.setEmail("asistemcdjchels@gmail.com");
        Mockito.when(userService.findByUsernameOrEmail(Mockito.anyString(), Mockito.anyString())).thenReturn(reader);
    
        // Mock the reservation service
        ModelReservation reservation = new ModelReservation();
        reservation.setBook(book);
        reservation.setUser(reader);
        reservation.setCopies(2);
        Mockito.when(reservationService.save(Mockito.any(ModelReservation.class))).thenReturn(reservation);
    
        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange("/api/reservations", HttpMethod.POST, entity, ApiResponse.class);
    
        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getResponseCode());
        assertNull(response.getBody().getBody());
        assertEquals("Echec de reservation du livre! Livre @[" + request.getCode() + "]" +
				"ou Lecteur @[" + request.getUsername() + "]" +
				" introuvable.", response.getBody().getMessage());
    }
    
    // Failed reserve a book with valid request data and with null data
    @Test
    public void test_failed_reservation_by_empty_data() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ReservationRequest request = new ReservationRequest();
        request.setCode(null);
        request.setCounter(0);
        request.setUsername(null);
        HttpEntity<ReservationRequest> entity = new HttpEntity<>(request, headers);
    
        // Mock the book service
        List<ModelBook> books = new ArrayList<>();
        ModelBook book = new ModelBook();
        book.setCode("ALC1988");
        book.setCopies(5);
        books.add(book);
        Mockito.when(bookServiceMock.findByCode(Mockito.anyString())).thenReturn(books);
    
        // Mock the user service
        ModelUser reader = new ModelUser();
        reader.setUsername("@asistemjhrj");
        reader.setEmail("asistemcdjchels@gmail.com");
        Mockito.when(userService.findByUsernameOrEmail(Mockito.anyString(), Mockito.anyString())).thenReturn(reader);
    
        // Mock the reservation service
        ModelReservation reservation = new ModelReservation();
        reservation.setBook(book);
        reservation.setUser(reader);
        reservation.setCopies(2);
        Mockito.when(reservationService.save(Mockito.any(ModelReservation.class))).thenReturn(reservation);
    
        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange("/api/reservations", HttpMethod.POST, entity, ApiResponse.class);
    
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getResponseCode());
        assertNull(response.getBody().getBody());
        assertEquals("Echec de reservation du livre! Vérifiez vos informations.", response.getBody().getMessage());
    }
    
    // Update the status of a reservation to 'true' and verify that the reservation date is set to the current date.
    @Test
    public void test_update_reservation_status_to_true() {
        // Arrange
        Long id = 6L;
        ReservationRequest request = new ReservationRequest();
        request.setStatus(true);

        ModelReservation reservation = new ModelReservation();
        reservation.setId(id);

        // Act
        Mockito.when(reservationService.findById(id)).thenReturn(reservation);
        Mockito.when(reservationService.save(reservation)).thenReturn(reservation);
        
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
            "/api/reservations/{id}/status",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            ApiResponse.class,
            id
        );
        // Assert
        assertEquals(HttpStatus.OK.value(), response.getBody().getResponseCode());
    }
    
    // Attempt to update the status of a reservation with a null request and verify that an appropriate error message is returned.
    @Test
    public void test_update_reservation_status_with_null_request() {
        // Arrange
        Long id = 6L;
        ReservationRequest request = new ReservationRequest();
        request.setCounter(-1);

        ModelReservation reservation = new ModelReservation();
        reservation.setId(id);

        // Act
        Mockito.when(reservationService.findById(id)).thenReturn(reservation);

        ResponseEntity<ApiResponse> response = restTemplate.exchange(
            "/api/reservations/{id}/status",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            ApiResponse.class,
            id
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getResponseCode());
        assertEquals("Echec de la mise à jour du status de la reservation! Vérifiez vos informations.", response.getBody().getMessage());
    }
    
    // Attempt to update the status of a reservation with invalid id.
    @Test
    public void test_update_reservation_status_with_invalid_id() {
        // Arrange
        Long id = 0L;
        ReservationRequest request = new ReservationRequest();
        
        ModelReservation reservation = new ModelReservation();
        reservation.setId(id);

        // Act
        Mockito.when(reservationService.findById(id)).thenReturn(reservation);

        ResponseEntity<ApiResponse> response = restTemplate.exchange(
            "/api/reservations/{id}/status",
            HttpMethod.PUT,
            new HttpEntity<>(request),
            ApiResponse.class,
            id
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getResponseCode());
        assertEquals("Echec de mise à jour de la reservation! "+
				"Reservation @[" + id + "] introuvable.", response.getBody().getMessage());
    }
    
    // Attempt to delete a reservation with an invalid ID (null, negative, zero)
    @Test
    public void test_delete_invalid_reservation_id() {
        // Arrange
        Long id = 0L;
        ModelReservation reservation = new ModelReservation();
        Mockito.when(reservationService.findById(id)).thenReturn(reservation);
    
        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange("/api/reservations/" + id, HttpMethod.DELETE, null, ApiResponse.class);
    
        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getResponseCode());
        assertNotNull(reservationService.findById(id));
    }
    
    // Successfully delete an existing reservation
    @Test
    public void test_delete_existing_reservation() {
        // Arrange
        Long id = 7L;
    
        // Mock the reservationService
        ModelReservation reservation = new ModelReservation();
        Mockito.when(reservationService.findById(id)).thenReturn(reservation);
    
        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
            "/api/reservations/{id}",
            HttpMethod.DELETE,
            null,
            ApiResponse.class,
            id
        );
    
        // Assert
        assertEquals(HttpStatus.OK.value(), response.getBody().getResponseCode());
    }
}
