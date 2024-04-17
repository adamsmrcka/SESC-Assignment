package uk.ac.leedsbeckett.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.leedsbeckett.student.Request.EnrolmentRequest;
import uk.ac.leedsbeckett.student.model.*;
import uk.ac.leedsbeckett.student.service.CourseService;
import uk.ac.leedsbeckett.student.service.EnrolmentService;
import uk.ac.leedsbeckett.student.service.StudentService;

@Controller
public class EnrolmentController {

    private final EnrolmentService enrolmentService;
    private final StudentService studentService;
    private final CourseService courseService;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public EnrolmentController(EnrolmentService enrolmentService, StudentService studentService, CourseService courseService, CourseRepository courseRepository, StudentRepository studentRepository) {
        this.enrolmentService = enrolmentService;
        this.studentService = studentService;
        this.courseService = courseService;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }
@PostMapping("/api/enrol")
public ResponseEntity<Invoice> enrolStudentJson(@RequestBody EnrolmentRequest enrolmentRequest) {
    Invoice invoice = enrolmentService.enrolStudentInCourse(enrolmentRequest);

    if (invoice == null) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    else {
        return new ResponseEntity<>(invoice, HttpStatus.OK);
    }
}

}

