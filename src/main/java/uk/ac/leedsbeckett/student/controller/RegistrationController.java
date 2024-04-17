package uk.ac.leedsbeckett.student.controller;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.leedsbeckett.student.Request.RegistrationRequest;
import uk.ac.leedsbeckett.student.model.Login;
import uk.ac.leedsbeckett.student.model.Student;
import uk.ac.leedsbeckett.student.service.LoginService;
import uk.ac.leedsbeckett.student.service.StudentService;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    @Autowired
    private LoginService loginService;


    @PostMapping("/register")
    public ResponseEntity<EntityModel<Student>> CreateNewStudentJson(@RequestBody RegistrationRequest request) {
        return loginService.CreateNewStudentJson(request);
    }

    @PostMapping("/login")
    public ResponseEntity<EntityModel<Student>> checkLoginJson(@RequestBody RegistrationRequest request) {
        return loginService.loginUserJson(request);
    }

}
