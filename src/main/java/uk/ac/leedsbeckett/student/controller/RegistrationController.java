package uk.ac.leedsbeckett.student.controller;

import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.leedsbeckett.student.Request.RegistrationRequest;
import uk.ac.leedsbeckett.student.model.Student;
import uk.ac.leedsbeckett.student.service.LoginService;
import uk.ac.leedsbeckett.student.service.StudentService;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    private final LoginService loginService;

    private final StudentService studentService;

    public RegistrationController(LoginService loginService, StudentService studentService) {
        this.loginService = loginService;
        this.studentService = studentService;
    }


    @PostMapping("/register")
    public ResponseEntity<EntityModel<Student>> CreateNewStudentJson(@RequestBody RegistrationRequest request) {
        return loginService.CreateNewStudentJson(request);
    }

    @PostMapping("/login")
    public ResponseEntity<EntityModel<Student>> checkLoginJson(@RequestBody RegistrationRequest request) {
        return loginService.loginUserJson(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        studentService.setCurrentUser(null);
        return ResponseEntity.ok().build();
    }

}
