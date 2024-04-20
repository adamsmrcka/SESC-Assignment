package uk.ac.leedsbeckett.student.controller;

import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.leedsbeckett.student.model.Account;
import uk.ac.leedsbeckett.student.model.Student;
import uk.ac.leedsbeckett.student.service.IntegrationService;
import uk.ac.leedsbeckett.student.service.StudentService;

/**
 * Controller class for handling student-related operations via RESTful endpoints
 */
@RestController
@RequestMapping("/api")
public class StudentController {

    private final StudentService studentService;
    private final IntegrationService integrationService;

    /**
     * Constructor to initialize StudentController with IntegrationService and StudentService dependencies
     * @param integrationService The IntegrationService instance to be injected
     * @param studentService     The StudentService instance to be injected
     */
    public StudentController(IntegrationService integrationService, StudentService studentService) {
        this.integrationService = integrationService;
        this.studentService = studentService;
    }

    /**
     * Endpoint to retrieve a student by ID in JSON format
     * @param id The ID of the student to retrieve
     * @return EntityModel representing the student in JSON format
     */
    @GetMapping("/students/{id}")
    @ResponseBody
    public EntityModel<Student> getStudentJson(@PathVariable Long id) {
        return studentService.getStudentByIdJson(id);
    }

    /**
     * Endpoint to update a student's information in JSON format
     * @param updateStudent The updated Student object containing new information
     * @return ResponseEntity containing an EntityModel representing the updated student in JSON format
     */
    @PostMapping("/student/update")
    public ResponseEntity<EntityModel<Student>> updateStudentJson(@RequestBody Student updateStudent) {
        return studentService.updateStudentJson(updateStudent);
    }

    /**
     * Endpoint to check graduate eligibility based on a student's account
     * @param studentId The ID of the student to check for graduate eligibility
     * @return ResponseEntity with a message indicating graduate eligibility ("Eligible" or "Not-Eligible")
     */
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
