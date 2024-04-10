package uk.ac.leedsbeckett.student.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.ac.leedsbeckett.student.model.*;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class EnrolmentService {
    private final StudentRepository studentRepository;
    private final StudentService studentService;
    private final IntegrationService integrationService;

    public EnrolmentService(StudentRepository studentRepository, CourseRepository courseRepository, StudentService studentService, IntegrationService integrationService) {
        this.studentRepository = studentRepository;
        this.studentService = studentService;
        this.integrationService = integrationService;
    }

    public Student getStudent() {
        return studentService.getCurrentUser();
    }

    public void enrolStudentInCourse(Student student, Course course) {
        if (student.getCoursesEnrolledIn().contains(course)) {
            throw new IllegalStateException("Student is already enrolled in this course");
        }
        student.enrolInCourse(course);
        studentRepository.save(student);

        String studentId = student.getExternalStudentId();
        Account account = integrationService.getStudentAccount(studentId);

        // Create an invoice for the course fee
        Invoice invoice = new Invoice();
        invoice.setAccount(account);
        invoice.setType(Invoice.Type.TUITION_FEES);
        invoice.setAmount(course.getFee());
        invoice.setDueDate(LocalDate.now().plusDays(30));

        // Create the course fee invoice
        ResponseEntity<Invoice> response = integrationService.createCourseFeeInvoice(invoice);
        if (response.getStatusCode().is2xxSuccessful()) {
            Invoice createdInvoice = response.getBody();
            String referenceNumber = createdInvoice.getReference();
            // Now you have the reference number
        } else {
            throw new RuntimeException("Failed to create course fee invoice");
        }
    }
}

