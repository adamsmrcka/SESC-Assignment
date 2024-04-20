package uk.ac.leedsbeckett.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;
import uk.ac.leedsbeckett.student.Request.EnrolmentRequest;
import uk.ac.leedsbeckett.student.model.*;
import uk.ac.leedsbeckett.student.service.EnrolmentService;
/**
 * Controller class for handling student enrolment operations via RESTful endpoints
 */
@RestController
@RequestMapping("/api")
public class EnrolmentController {

    private final EnrolmentService enrolmentService;

    /**
     * Constructor to initialize EnrolmentController with EnrolmentService dependency injection
     * @param enrolmentService The EnrolmentService instance to be injected
     */
    @Autowired
    public EnrolmentController(EnrolmentService enrolmentService) {
        this.enrolmentService = enrolmentService;
    }

    /**
     * Endpoint to enrol a student in a course
     * @param enrolmentRequest The EnrolmentRequest object containing enrolment details
     * @return EntityModel representing the invoice generated for the enrolment
     */
    @PostMapping("/enrol")
    public EntityModel<Invoice> enrolStudentJson(@RequestBody EnrolmentRequest enrolmentRequest) {
        return enrolmentService.enrollStudentInCourseJson(enrolmentRequest);
    }

}

