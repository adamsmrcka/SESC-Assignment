package uk.ac.leedsbeckett.student.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import uk.ac.leedsbeckett.student.Request.RegistrationRequest;
import uk.ac.leedsbeckett.student.controller.*;
import uk.ac.leedsbeckett.student.model.CourseRepository;
import uk.ac.leedsbeckett.student.model.Student;
import uk.ac.leedsbeckett.student.service.EnrolmentService;
import uk.ac.leedsbeckett.student.service.LoginService;
import uk.ac.leedsbeckett.student.service.StudentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = PortalController.class)
@AutoConfigureMockMvc
public class PortalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    private StudentService studentService;

    @MockBean
    private EnrolmentService enrolmentService;

    @MockBean
    private StudentController studentController;

    @MockBean
    private LoginService loginService;

    @MockBean
    private CourseController courseController;

    @MockBean
    private EnrolmentController enrolmentController;

    @MockBean
    private RegistrationController registrationController;

    @InjectMocks
    private PortalController portalController;

    @Autowired
    private ObjectMapper objectMapper;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testHomePage() throws Exception {
        // Mock current user
        Student currentUser = new Student();
        currentUser.setId(1L);
        currentUser.setForename("Test");
        currentUser.setSurname("User");

        when(studentService.getCurrentUser()).thenReturn(currentUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/main"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("main"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentUser"));
    }

    @Test
    public void testRegister() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/register"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("register"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("registrationRequest"));
    }

    @Test
    public void testProcessRegistration() throws Exception {
        RegistrationRequest request = new RegistrationRequest();
        request.setForename("John");
        request.setSurname("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("password");

        Student student = new Student(1L, "c123", request.getForename(), request.getSurname());

        ResponseEntity<EntityModel<Student>> mockResponseEntity = ResponseEntity.ok(
                EntityModel.of(student)
        );
        when(registrationController.CreateNewStudentJson(any(RegistrationRequest.class)))
                .thenReturn(mockResponseEntity);

        mockMvc.perform(post("/register")
                .param("forename", request.getForename())
                .param("surname", request.getSurname())
                .param("email", request.getEmail())
                .param("password", request.getPassword()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/registrationSuccess?studentID=c123"));
    }
    @Test
    public void testRegistrationSuccess() throws Exception {
        String studentId = "123";

        mockMvc.perform(MockMvcRequestBuilders.get("/registrationSuccess")
                        .param("studentID", studentId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registrationsuccess"))
                .andExpect(MockMvcResultMatchers.model().attribute("studentID", studentId));
    }
    @Test
    public void testLogIn() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("login"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("registrationRequest"));
    }

    @Test
    public void testProcessLogin_ValidCredentials() throws Exception {
        String email = "test@example.com";
        String password = "password";

        // Mock the login service to return true for emailExists
        when(loginService.emailExists(email)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", email)
                        .param("password", password))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/main"));
    }

    @Test
    public void testProcessLogin_InvalidCredentials() throws Exception {
        String email = "invalid@example.com";
        String password = "wrongpassword";

        // Mock the login service to return false for emailExists
        when(loginService.emailExists(email)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", email)
                        .param("password", password))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("login"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("error"));
    }
}
