package uk.ac.leedsbeckett.student.controller;

import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.leedsbeckett.student.Request.RegistrationRequest;
import uk.ac.leedsbeckett.student.model.Student;
import uk.ac.leedsbeckett.student.service.LoginService;
import uk.ac.leedsbeckett.student.service.StudentService;

/**
 * Controller class for handling user registration, login, and logout operations via RESTful endpoints
 */
@RestController
@RequestMapping("/api")
public class RegistrationController {

    private final LoginService loginService;
    private final StudentService studentService;

    /**
     * Constructor to initialize RegistrationController with LoginService and StudentService dependencies
     * @param loginService   The LoginService instance to be injected
     * @param studentService The StudentService instance to be injected
     */
    public RegistrationController(LoginService loginService, StudentService studentService) {
        this.loginService = loginService;
        this.studentService = studentService;
    }

    /**
     * Endpoint to handle new student registration
     * @param request The RegistrationRequest object containing registration details
     * @return ResponseEntity containing an EntityModel representing the newly registered student
     */
    @PostMapping("/register")
    public ResponseEntity<EntityModel<Student>> CreateNewStudentJson(@RequestBody RegistrationRequest request) {
        return loginService.CreateNewStudentJson(request);
    }

    /**
     * Endpoint to handle user login
     * @param request The RegistrationRequest object containing login credentials
     * @return ResponseEntity containing an EntityModel representing the authenticated student
     */
    @PostMapping("/login")
    public ResponseEntity<EntityModel<Student>> checkLoginJson(@RequestBody RegistrationRequest request) {
        return loginService.loginUserJson(request);
    }

    /**
     * Endpoint to handle user logout
     * @return ResponseEntity indicating successful logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        studentService.setCurrentUser(null);
        return ResponseEntity.ok().build();
    }

}
