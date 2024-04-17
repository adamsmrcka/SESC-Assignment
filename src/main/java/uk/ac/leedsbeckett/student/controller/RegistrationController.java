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

    @Autowired
    private StudentService studentService;


    @PostMapping("/register")
    public ResponseEntity<EntityModel<Student>> CreateNewStudentJson(@RequestBody RegistrationRequest request) {
        return loginService.CreateNewStudentJson(request);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> checkLogin(@RequestParam String email, @RequestParam String password) {
        if (loginService.authenticate(email, password)) {
            Login loggedInUser = loginService.getByEmail(email);
            Student loggedInStudent = studentService.getStudentByExternalStudentId(loggedInUser.getStudentID());
            studentService.setCurrentUser(loggedInStudent);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "/main");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Login Failed! Incorrect email or password.");
        }
    }

}
