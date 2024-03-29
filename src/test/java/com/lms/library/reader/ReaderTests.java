package com.lms.library.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
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
import com.lms.library.enums.UserRole;
import com.lms.library.models.ModelUser;
import com.lms.library.requests.ReaderRequest;
import com.lms.library.services.UserService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = TestConfig.class)
@TestPropertySource(locations = "classpath:application-test.yaml")
@ActiveProfiles("test")
public class ReaderTests {
	
	@Autowired
	private TestRestTemplate restTemplate;
	@MockBean
	private UserService userService;
	
	@BeforeEach
	public void setUp() {
	    restTemplate.getRestTemplate().setUriTemplateHandler(new DefaultUriBuilderFactory(TestConfig.baseUrl));
	}
	
	@Test
    public void test_getAllReaders_Success() {
		// Arrange
        List<ModelUser> readers = new ArrayList<>();
        readers.add(new ModelUser());
        ApiResponse expectedResponse = new ApiResponse(200, readers, "Liste des lecteurs récupérée avec succès!");
        ResponseEntity<ApiResponse> expectedEntity = ResponseEntity.status(HttpStatus.OK).body(expectedResponse);

        Mockito.when(userService.findByRole(UserRole.USER)).thenReturn(readers);

        // Act
        ResponseEntity<ApiResponse> actualEntity = restTemplate.getForEntity("/api/readers/all", ApiResponse.class);

        // Assert
        assertEquals(HttpStatus.OK.value(), actualEntity.getBody().getResponseCode());
        assertEquals(expectedResponse.getMessage(), actualEntity.getBody().getMessage());
    }
	
	@Test
    public void test_getAllReaders_Exception() {
        // Arrange
        Mockito.when(userService.findByRole(UserRole.USER)).thenThrow(new RuntimeException("Database error"));
    
        // Act
        ResponseEntity<ApiResponse> actualEntity = restTemplate.getForEntity("/api/readers/all", ApiResponse.class);
    
        // Assert
        assertEquals(HttpStatus.OK.value(), actualEntity.getBody().getResponseCode());
    }
	
    // Should return a 200 status code and the reader object when a valid reader ID is provided
    @Test
    public void test_valid_reader_id() {
        // Arrange
        Long id = 4L;
        ModelUser expectedReader = new ModelUser();
        expectedReader.setId(id);
        Object obj = expectedReader;

        Mockito.when(userService.findById(id)).thenReturn(expectedReader);

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                "/api/readers/uuid/" + id, ApiResponse.class
        );

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getBody().getResponseCode());
        ModelUser actualReader;
		try {
			actualReader = TestConfig.convertJsonToModelUser(response.getBody().getBody().toString());
			assertEquals(expectedReader, actualReader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    // Should return a 404 status code and a 'Lecteur introuvable!' message when a null reader object is returned from the userService
    @Test
    public void test_null_reader_object() {
        // Arrange
        Long id = 0L;

        Mockito.when(userService.findById(id)).thenReturn(null);

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity("/api/readers/uuid/" + id, ApiResponse.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getResponseCode());
    }
    
    
    // Creating a new reader with valid input should return a 200 status code and a success message
    @Test
    public void test_createReader_validInput() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ReaderRequest user = new ReaderRequest();
        user.setEmail("bahithbn@gmail.com");
        user.setUsername("hitho");
        user.setPassword("password");
        user.setTelephone("1234567890");
        user.setGender("Male");
        user.setFirstname("Hitho");
        user.setLastname("BN");
        user.setAddress("Medina");

        HttpEntity<ReaderRequest> request = new HttpEntity<>(user, headers);

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity("/api/readers", request, ApiResponse.class);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getBody().getResponseCode());
        assertEquals("Reader created successfully!", response.getBody().getMessage());
    }
    
    
    // Successfully update a reader's profile with all fields filled
    @Test
    public void test_update_reader_success() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Long id = 8L;
        ReaderRequest reader = new ReaderRequest();
        reader.setEmail("bahithbn@gmail.com");
        reader.setUsername("@hithotho");
        reader.setPassword("password");
        reader.setTelephone("1234567890");
        reader.setGender("Male");
        reader.setFirstname("Hitho");
        reader.setLastname("BN");
        reader.setAddress("Medina");

        HttpEntity<ReaderRequest> request = new HttpEntity<>(reader, headers);

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange("/api/readers/" + id, HttpMethod.PUT, request, ApiResponse.class);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getBody().getResponseCode());
        //assertEquals("Reader updated successfully!", response.getBody().getMessage());
    }
    
    
    // Fail to update a reader's profile with a non-existent id
    @Test
    public void test_update_reader_nonexistent_id() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Long id = 0L;
        ReaderRequest reader = new ReaderRequest();
        reader.setEmail("bahithbn@gmail.com");
        reader.setUsername("@hithotho");
        reader.setPassword("password");
        reader.setTelephone("1234567890");
        reader.setGender("Male");
        reader.setFirstname("Hitho");
        reader.setLastname("BN");
        reader.setAddress("Medina");

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
            "/api/readers/" + id,
            HttpMethod.PUT,
            new HttpEntity<>(reader, headers),
            ApiResponse.class
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getResponseCode());
        assertEquals("Failed to update reader! Reader @[" + id + "] not found.", response.getBody().getMessage());
    }
    /*
    // The method successfully deletes an existing reader with a valid ID.
    @Test
    public void test_delete_existing_reader() {
        // Arrange
        Long id = 13L;

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange("/api/readers/{id}", HttpMethod.DELETE, null, ApiResponse.class, id);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getBody().getResponseCode());
        assertNull(userService.findById(id));
    }
    */
    
    // The method returns an error response with a 500 status code if the reader still exists in the database after deletion.
    @Test
    public void test_delete_non_existing_reader() {
        // Arrange
        Long id = 0L;

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange("/api/readers/{id}", HttpMethod.DELETE, null, ApiResponse.class, id);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getResponseCode());
        assertNull(userService.findById(id));
    }
	
}
