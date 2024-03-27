package com.lms.library.notification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import com.lms.library.requests.NotificationRequest;
import com.lms.library.services.NotificationService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = TestConfig.class)
@TestPropertySource(locations = "classpath:application-test.yaml")
@ActiveProfiles("test")
public class NotificationTests {

	@Autowired
	private TestRestTemplate restTemplate;
	@MockBean
	private NotificationService notificationService;
	
	@BeforeEach
	public void setUp() {
	    restTemplate.getRestTemplate().setUriTemplateHandler(new DefaultUriBuilderFactory(TestConfig.baseUrl));
	}
	
    // Notification sent successfully with valid input
    @Test
    public void test_notification_sent_successfully() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Long id = 9L;
        NotificationRequest request = new NotificationRequest();
        request.setContent("Test notification");
        request.setSubject("API Test");

        // Act
        HttpEntity<NotificationRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<ApiResponse> response = restTemplate.exchange("/api/notifications/{id}", HttpMethod.POST, entity, ApiResponse.class, id);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getBody().getResponseCode());
        assertNotNull(response.getBody());
        assertEquals("Notification envoyée avec succès!", response.getBody().getMessage());
    }
    
    // Notification not sent with invalid user id
    @Test
    public void test_notification_not_sent_with_invalid_user_id() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Long id = 999L;
        NotificationRequest request = new NotificationRequest();
        request.setContent("Test notification");
        request.setSubject("API Test");

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
            "/api/notifications/{id}",
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            ApiResponse.class,
            id
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getResponseCode());
        assertNotNull(response.getBody());
        assertEquals("Echec de notification! Lecteur @[" + 999 + "] introuvable!.", response.getBody().getMessage());
    }
    
    // Notification not sent with no content
    @Test
    public void test_notification_not_sent_with_no_content() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Long id = 9L;
        NotificationRequest request = new NotificationRequest();
        request.setSubject("API Test");

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
            "/api/notifications/{id}",
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            ApiResponse.class,
            id
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getResponseCode());
        assertNotNull(response.getBody());
        assertEquals("Echec de notification! Vérifiez vos informations.", response.getBody().getMessage());
    }
    
    // Returns a 200 status code and a list of notifications when given a valid user ID.
    @Test
    public void test_valid_user_id() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        String url = "http://localhost:8080/api/notifications/9";
    
        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, ApiResponse.class);
    
        // Assert
        assertEquals(HttpStatus.OK.value(), response.getBody().getResponseCode());
        assertNotNull(response.getBody());
        assertEquals("Notifications récupérées avec succès!", response.getBody().getMessage());
    }
    
    // Returns a 404 status code and an error message when given an invalid user ID.
    @Test
    public void test_invalid_user_id() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        Long id = 999L;
        String url = "http://localhost:8080/api/notifications/"+ id;
    
        // Act
        ResponseEntity<ApiResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, ApiResponse.class);
    
        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getResponseCode());
        assertNotNull(response.getBody());
        assertEquals("Echec de récupération des notifications. Lecteur @[" + id + "] introuvable!.", response.getBody().getMessage());
    }
}
