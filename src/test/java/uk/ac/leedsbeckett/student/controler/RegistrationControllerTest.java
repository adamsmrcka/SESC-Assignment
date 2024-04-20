package uk.ac.leedsbeckett.student.controler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import uk.ac.leedsbeckett.student.Request.RegistrationRequest;
import uk.ac.leedsbeckett.student.controller.RegistrationController;
import uk.ac.leedsbeckett.student.model.Student;
import uk.ac.leedsbeckett.student.service.LoginService;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class RegistrationControllerTest {

    @Mock
    private LoginService loginService;

    @InjectMocks
    private RegistrationController registrationController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testCreateNewStudentJson() {
        // Create a mock RegistrationRequest
        RegistrationRequest request = new RegistrationRequest("password", "John", "Doe", "johndoe@example.com", "type");

        // Mock response from LoginService
        String studentId = "123";
        when(loginService.CreateNewStudentJson(any())).thenReturn(ResponseEntity.created(URI.create("/api/students/123")).body(null));

        // Call the controller method
        ResponseEntity<EntityModel<Student>> response = registrationController.CreateNewStudentJson(request);

        // Verify the response
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("/api/students/123", response.getHeaders().getLocation().getPath());

        // Verify that CreateNewStudentJson was called with the correct request
        verify(loginService, times(1)).CreateNewStudentJson(request);
    }

    @Test
    public void testCheckLoginJson() {
        // Create a mock RegistrationRequest
        RegistrationRequest request = new RegistrationRequest("password", "John", "Doe", "johndoe@example.com", "type");

        // Mock response from LoginService
        when(loginService.loginUserJson(any())).thenReturn(ResponseEntity.created(URI.create("/api/students/123")).body(null));

        // Call the controller method
        ResponseEntity<EntityModel<Student>> response = registrationController.checkLoginJson(request);

        // Verify the response
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("/api/students/123", response.getHeaders().getLocation().getPath());

        // Verify that loginUserJson was called with the correct request
        verify(loginService, times(1)).loginUserJson(request);
    }
}
