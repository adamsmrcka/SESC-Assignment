package uk.ac.leedsbeckett.student.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.ac.leedsbeckett.student.Request.RegistrationRequest;
import uk.ac.leedsbeckett.student.model.*;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class LoginServiceTest {
    @InjectMocks
    private LoginService loginService;
    private StudentService studentService;
    @Mock
    private LoginRepository loginRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private IntegrationService integrationService;

    @BeforeEach
    void setUp() {
        Login login = new Login("test22@gmail.com", "c3922382", "password", Login.UserType.USER);
        loginRepository.save(login);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void authenticate() {
        String email = "test@example.com";
        String password = "password123";
        Login mockLogin = new Login();
        mockLogin.setStudentID("123");
        mockLogin.setEmail(email);
        mockLogin.setPassword(password);

        when(loginRepository.findByEmail(email)).thenReturn(java.util.Optional.of(mockLogin));

        String result = loginService.authenticate(email, password);
        assertEquals("123", result);
    }

    @Test
    void generateUniqueStudentID() {

        // Call the method under test
        String uniqueStudentID = loginService.generateUniqueStudentID();

        // Verify that the repository method was called with the expected student ID
        verify(loginRepository).existsByStudentID(uniqueStudentID);
    }

    /*
    @Test
    void createNewStudentJson() {
        RegistrationRequest request = new RegistrationRequest();
        request.setPassword("password");
        request.setForename("John");
        request.setSurname("Doe");
        request.setEmail("john.doe@example.com");
        request.setType("USER");

        // Mock student and login entities
        Student student = new Student();
        student.setId(1L);
        student.setForename(request.getForename());
        student.setSurname(request.getSurname());

        Login login = new Login();
        login.setEmail(request.getEmail());
        login.setStudentID(student.getExternalStudentId());
        login.setPassword(request.getPassword());
        login.setType(request.getType());

        // Mock repository methods
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(loginRepository.save(any(Login.class))).thenReturn(login);

        // Mock integration service methods
        when(integrationService.createFinanceAccount(any())).thenReturn(ResponseEntity.ok().build());
        when(integrationService.createBooksAccount(any())).thenReturn(ResponseEntity.ok().build());

        // Call the service method
        ResponseEntity<EntityModel<Student>> response = loginService.CreateNewStudentJson(request);

        // Verify the response
        assertEquals(201, response.getStatusCodeValue());

        // Verify that the student and login were saved
        verify(studentRepository, times(1)).save(any(Student.class));
        verify(loginRepository, times(1)).save(any(Login.class));

        // Verify that integration service methods were called
        verify(integrationService, times(1)).createFinanceAccount(any(Account.class));
        verify(integrationService, times(1)).createBooksAccount(student.getExternalStudentId());

        // Verify the URI in the response
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(student.getId())
                .toUri();
        assertEquals(location, response.getHeaders().getLocation());
    }

    @Test
    void loginUserJson() {
        // Mock RegistrationRequest
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("john.doe@example.com");
        request.setPassword("password");

        // Mock student and URI
        Student student = new Student();
        student.setId(1L);
        student.setForename("John");
        student.setSurname("Doe");
        student.setExternalStudentId("ABC123");

        URI mockUri = URI.create("/students/" + student.getId());

        // Stub authenticate method to return studentId
        when(loginService.authenticate(request.getEmail(), request.getPassword())).thenReturn(student.getExternalStudentId());

        // Stub repository method to return student
        Optional<Login> optionalLogin = Optional.of(new Login(request.getEmail(), student.getExternalStudentId(), request.getPassword(), Login.UserType.USER));
        when(loginRepository.findByEmail(request.getEmail())).thenReturn(optionalLogin);

        // Stub setCurrentUser method to do nothing
        doNothing().when(studentService).setCurrentUser(student);

        // Mock ServletUriComponentsBuilder behavior
        ServletUriComponentsBuilder builder = mock(ServletUriComponentsBuilder.class);
        when(ServletUriComponentsBuilder.fromCurrentRequest()).thenReturn(builder);
        when(builder.path(anyString())).thenReturn(builder);

        // Call the service method
        ResponseEntity<EntityModel<Student>> response = loginService.loginUserJson(request);

        // Verify the response
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(mockUri, response.getHeaders().getLocation());

        // Verify interactions
        verify(loginService, times(1)).authenticate(request.getEmail(), request.getPassword());
        verify(studentRepository, times(1)).findStudentsByExternalStudentId(student.getExternalStudentId());
        verify(studentService, times(1)).setCurrentUser(student);
    }

 */

    @Test
    void registerUser() {
        RegistrationRequest request = new RegistrationRequest();
        request.setPassword("password123");
        request.setForename("John");
        request.setSurname("Doe");
        request.setEmail("test@example.com");
        request.setType("student");

        // Mock the integrationService methods appropriately
        when(integrationService.createFinanceAccount(any())).thenReturn(ResponseEntity.ok().build());
        String accountId = "account123"; // Simulate a returned account ID
        when(integrationService.createBooksAccount(any())).thenReturn(ResponseEntity.ok().build());

        String result = loginService.registerUser(
                request.getPassword(), request.getForename(),
                request.getSurname(), request.getEmail(), request.getType());

        // Test registerUser method
        assertFalse(result.isEmpty());
        verify(integrationService, times(1)).createFinanceAccount(any());
        verify(integrationService, times(1)).createBooksAccount(result);
        verify(studentRepository, times(1)).save(any());
        verify(loginRepository, times(2)).save(any());
    }


    @Test
    void emailExists() {
        String existingEmail = "test@example.com";
        Login testLogin = new Login(existingEmail, "12345", "password", Login.UserType.USER);

        when(loginRepository.findByEmail(existingEmail)).thenReturn(Optional.of(testLogin));

        // Act
        boolean result = loginService.emailExists(existingEmail);

        // Assert
        assertTrue(result);
        verify(loginRepository, times(1)).findByEmail(existingEmail);
    }
}