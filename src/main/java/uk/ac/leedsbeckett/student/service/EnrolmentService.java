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

@Service
public class EnrolmentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final IntegrationService integrationService;
    private final CourseService courseService;


    @Autowired
    public EnrolmentService(StudentRepository studentRepository, IntegrationService integrationService, CourseRepository courseRepository, CourseService courseService) {
        this.studentRepository = studentRepository;
        this.integrationService = integrationService;
        this.courseRepository = courseRepository;
        this.courseService = courseService;
    }

    public EntityModel<Invoice> enrollStudentInCourseJson(EnrolmentRequest enrolmentRequest){

        Invoice invoice = enrolStudentInCourse(enrolmentRequest);
        return EntityModel.of(invoice);
    }

    @Transactional
    public Invoice enrolStudentInCourse(EnrolmentRequest enrolmentRequest) {

        Student student = studentRepository.findStudentById(enrolmentRequest.getStudentId());
        Course courseEnroll = courseRepository.findCourseById(enrolmentRequest.getCourseId());
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
