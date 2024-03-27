package com.lms.library.book;

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
import com.lms.library.requests.BookRequest;
import com.lms.library.requests.ReaderRequest;
import com.lms.library.services.BookService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = TestConfig.class)
@TestPropertySource(locations = "classpath:application-test.yaml")
@ActiveProfiles("test")
public class BookTests {
	
	@Autowired
	private TestRestTemplate restTemplate;
	@MockBean
	private BookService bookService;
	BookService bookServiceMock = Mockito.mock(BookService.class);
	
	@BeforeEach
	public void setUp() {
	    restTemplate.getRestTemplate().setUriTemplateHandler(new DefaultUriBuilderFactory(TestConfig.baseUrl));
	}
	
    // Returns a list of all books with status code 200
    @Test
    public void test_returns_all_books_with_status_code_200() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
    
        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
            "/api/books/all",
            HttpMethod.GET,
            entity,
            ApiResponse.class
        );
    
        // Assert
        assertEquals(200, response.getBody().getResponseCode());
        assertNotNull(response.getBody());
        assertEquals("Liste des livres récupérée avec succès!", response.getBody().getMessage());
    }
    
    // Should return a list of n books when n is less than or equal to the total number of books
    @Test
    public void test_return_list_of_n_books() {
        String url = "/api/books/top/{n}";
        int n = 5;
    
        // Mock the bookService and its method findTopNBooksWithLimit
        List<ModelBook> mockBooks = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            ModelBook book = new ModelBook();
            // Set book properties
            mockBooks.add(book);
        }
        Mockito.when(bookServiceMock.findTopNBooksWithLimit(n)).thenReturn(mockBooks);
    
        // Make the request to the API endpoint
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(url, ApiResponse.class, n);
    
        // Assert the response status code
        assertEquals(200, response.getBody().getResponseCode());
    
        // Assert the number of books returned in the response
        List<ModelBook> books = (List<ModelBook>) response.getBody().getBody();
        assertEquals(n, books.size());
    }
    
    // Should return 200 status code and book object when book with given id exists in the database
    @Test
    public void test_book_exists() {
        Long id = 10L;
        ModelBook expectedBook = new ModelBook();
        expectedBook.setId(id);
        // Set up expected book object
    
        // Mock the bookService
        Mockito.when(bookServiceMock.findById(id)).thenReturn(expectedBook);
    
        // Use TestRestTemplate to make the request
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity("/api/books/" + id, ApiResponse.class);
    
        assertEquals(200, response.getBody().getResponseCode());
        //assertEquals(expectedBook, response.getBody().getData());
        assertEquals("Livre @[" + id + "] récupéré avec succès!", response.getBody().getMessage());
    }
    
    // Should handle null input id and return 404 status code
    @Test
    public void test_null_id() {
        Long id = 99999L;

        ResponseEntity<ApiResponse> response = restTemplate.getForEntity("/api/books/" + id, ApiResponse.class);

        assertEquals(404, response.getBody().getResponseCode());
        assertNull(response.getBody().getBody());
        assertEquals("Livre introuvable!", response.getBody().getMessage());
    }
    
    // Book is created successfully with valid input data
    @Test
    public void test_book_created_successfully() {
        // Arrange
    	HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        BookRequest req = new BookRequest();
        req.setTitle("Test Book");
        req.setDescription("This is a test book");
        req.setCategory("Test Category");
        req.setYear(2021);
        req.setAuthors("Author 1, Author 2");
        req.setCode("123456");
        req.setCopies(5);
        
        HttpEntity<BookRequest> request = new HttpEntity<>(req, headers);

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity("/api/books", request, ApiResponse.class);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getBody().getResponseCode());
        assertNotNull(response.getBody());
        assertEquals("Livre créér avec succès!", response.getBody().getMessage());
    }
    
    // Book is not created with null input data
    @Test
    public void test_book_not_created_with_null_data() {
        // Arrange
    	HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        BookRequest req = new BookRequest();

        HttpEntity<BookRequest> request = new HttpEntity<>(req, headers);
        // Act
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity("/api/books", request, ApiResponse.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getResponseCode());
        assertNull(response.getBody().getBody());
        assertEquals("Echec de création du livre!", response.getBody().getMessage());
    }
    
    // The book is found and all fields are updated successfully.
    @Test
    public void test_updateBook_success() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Long id = 4794L;
        BookRequest request = new BookRequest();
        request.setAuthors("Author");
        request.setCategory("Category");
        request.setCode("Code");
        request.setCopies(5);
        request.setDescription("Description");
        request.setTitle("Title");
        request.setYear(2021);

        ModelBook book = new ModelBook();
        book.setId(id);

        Mockito.when(bookService.findById(id)).thenReturn(book);

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
            "api/books/" + id,
            HttpMethod.PUT,
            new HttpEntity<>(request, headers),
            ApiResponse.class
        );

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getBody().getResponseCode());
        assertEquals("Livre mis à jour avec succès!", response.getBody().getMessage());
    }
    
    // The book ID is null.
    @Test
    public void test_updateBook_nullId() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Long id = 10000L;
        BookRequest request = new BookRequest();

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange("/api/books/" + id, HttpMethod.PUT, new HttpEntity<>(request), ApiResponse.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getResponseCode());
        assertEquals("Echec de mise à jour du livre! Livre @[" + id + "] introuvable!.", response.getBody().getMessage());
    }
    
    // Book exists, delete it, return success message
    @Test
    public void test_deleteBook_success() {
        // Arrange
        Long id = 4791L;

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
            "/api/books/{id}",
            HttpMethod.DELETE,
            null,
            ApiResponse.class,
            id
        );

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getBody().getResponseCode());
        assertEquals("Livre supprimé avec succès!", response.getBody().getMessage());
    }
    
    // Attempt to delete a book with an invalid id, return error message
    @Test
    public void test_deleteBook_invalidId() {
        // Arrange
        Long id = 0L;

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
            "/api/books/{id}",
            HttpMethod.DELETE,
            null,
            ApiResponse.class,
            id
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getResponseCode());
        assertEquals("Echec de suppression du livre! Livre @[" + id + "] introuvable.", response.getBody().getMessage());
    }

}
