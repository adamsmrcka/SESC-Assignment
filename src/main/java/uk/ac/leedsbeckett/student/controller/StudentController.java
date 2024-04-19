package uk.ac.leedsbeckett.student.controller;

import org.hibernate.metamodel.mapping.EntityValuedModelPart;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.leedsbeckett.student.model.Account;
import uk.ac.leedsbeckett.student.model.Course;
import uk.ac.leedsbeckett.student.model.Student;
import uk.ac.leedsbeckett.student.service.IntegrationService;
import uk.ac.leedsbeckett.student.service.StudentService;

import javax.swing.text.html.parser.Entity;

@RestController
@RequestMapping("/api")
public class StudentController {

    private final StudentService studentService;
    private final IntegrationService integrationService;

    public StudentController(IntegrationService integrationService, StudentService studentService) {
        this.integrationService = integrationService;
        this.studentService = studentService;
    }

    @GetMapping("/students/{id}")
    @ResponseBody
    public EntityModel<Student> getStudentJson(@PathVariable Long id) {
        return studentService.getStudentByIdJson(id);
    }

    @PostMapping("/student/update")
    public ResponseEntity<EntityModel<Student>> updateStudentJson(@RequestBody Student updateStudent) {
        return studentService.updateStudentJson(updateStudent);
    }

    @GetMapping("/student/graduate/{studentId}")
    public ResponseEntity<String> getGraduateEligibility(@PathVariable String studentId) {
        Account account = integrationService.getStudentAccount(studentId);
        if (account != null) {
            if (account.isHasOutstandingBalance()) {
                return ResponseEntity.ok("Not-Eligible");
            } else {
                return ResponseEntity.ok("Eligible");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
