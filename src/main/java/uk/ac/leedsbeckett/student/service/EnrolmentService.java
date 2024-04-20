package uk.ac.leedsbeckett.student.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.leedsbeckett.student.Request.EnrolmentRequest;
import uk.ac.leedsbeckett.student.model.*;

import java.time.LocalDate;
import java.util.Set;

/**
 * Service class for handling student enrolment operations
 */
@Service
public class EnrolmentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final IntegrationService integrationService;
    private final CourseService courseService;


    /**
     * Constructor for EnrolmentService
     * @param studentRepository  Repository for accessing student data
     * @param integrationService Service for integrating with external systems
     * @param courseRepository   Repository for accessing course data
     * @param courseService      Service for course-related operations
     */
    @Autowired
    public EnrolmentService(StudentRepository studentRepository, IntegrationService integrationService, CourseRepository courseRepository, CourseService courseService) {
        this.studentRepository = studentRepository;
        this.integrationService = integrationService;
        this.courseRepository = courseRepository;
        this.courseService = courseService;
    }

    /**
     * Enrolls a student in a course and returns the invoice in JSON format
     * @param enrolmentRequest Request containing student ID and course ID for enrolment
     * @return EntityModel representing the invoice for the enrolled course
     */
    public EntityModel<Invoice> enrollStudentInCourseJson(EnrolmentRequest enrolmentRequest) {
        Invoice invoice = enrolStudentInCourse(enrolmentRequest);
        return EntityModel.of(invoice);
    }

    /**
     * Enrolls a student in a course and returns the invoice
     * @param enrolmentRequest Request containing student ID and course ID for enrolment
     * @return Invoice representing the course fee invoice
     * @throws RuntimeException if student or course is not found, or if there is an enrolment error
     */
    @Transactional
    public Invoice enrolStudentInCourse(EnrolmentRequest enrolmentRequest) {

        Student student = studentRepository.findStudentById(enrolmentRequest.getStudentId());
        if (student == null) {
            throw new RuntimeException("Student not found with ID: " + enrolmentRequest.getStudentId());
        }
        // Find the course by ID
        Course courseEnroll = courseRepository.findCourseById(enrolmentRequest.getCourseId());
        if (courseEnroll == null) {
            throw new RuntimeException("Course not found with ID: " + enrolmentRequest.getCourseId());
        }
        if (isStudentEnrolledInCourseWithId(student, courseEnroll.getId())) {
            throw new RuntimeException("Error: Student is already enrolled in this course!");
        }

        student.enrolInCourse(courseEnroll);
        studentRepository.save(student);

        // Create invoice for course fee
        Invoice invoice = createCourseFeeInvoice(student, courseEnroll);
        ResponseEntity<Invoice> response = integrationService.createCourseFeeInvoice(invoice);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to create course fee invoice");
        }
    }

    /**
     * Checks if a student is enrolled in a course with a specific course ID
     * @param student  The student to check enrolment for
     * @param courseId The ID of the course to check enrolment in
     * @return true if the student is enrolled in the course, false otherwise
     */
    @Transactional
    public boolean isStudentEnrolledInCourseWithId(Student student, Long courseId) {
        // Check if the student is enrolled in any course with the specified course ID
        Course course = courseService.getCourseById(courseId);
        Set<Student> enrolledStudents = course.getStudentsEnrolledInCourse();
        if (enrolledStudents != null) {
            for (Student enrolledStudent : enrolledStudents) {
                if (enrolledStudent.getId().equals(student.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Creates an invoice for the course fee based on student and course information
     * @param student The student for whom the invoice is being created
     * @param course  The course for which the invoice is being created
     * @return Invoice representing the course fee invoice
     */
    private Invoice createCourseFeeInvoice(Student student, Course course) {
        String studentId = student.getExternalStudentId();
        Account account = integrationService.getStudentAccount(studentId);

        Invoice invoice = new Invoice();
        invoice.setAccount(account);
        invoice.setType(Invoice.Type.TUITION_FEES);
        invoice.setAmount(course.getFee());
        invoice.setDueDate(LocalDate.now().plusDays(30));
        return invoice;
    }


}
