package uk.ac.leedsbeckett.student.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.leedsbeckett.student.model.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
public class EnrolmentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final IntegrationService integrationService;


    @Autowired
    public EnrolmentService(StudentRepository studentRepository, IntegrationService integrationService, CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.integrationService = integrationService;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public Invoice enrolStudentInCourse(Student student, Course course) {

        Student student1 = studentRepository.findStudentById(student.getId());
        Course courseEnroll = courseRepository.findCourseById(course.getId());
        if (isStudentEnrolledInCourseWithId(student1, courseEnroll.getId())) {
            throw new RuntimeException("Error: Student is already enrolled in this course!");
        }
        student1.enrolInCourse(courseEnroll);
            System.out.println(courseEnroll.getId());
            studentRepository.save(student1);

            // Create invoice for course fee
            Invoice invoice = createCourseFeeInvoice(student, course);
            ResponseEntity<Invoice> response = integrationService.createCourseFeeInvoice(invoice);
            if (response.getStatusCode().is2xxSuccessful()) {
                Invoice createdInvoice = response.getBody();
                return createdInvoice;
            } else {
                throw new RuntimeException("Failed to create course fee invoice");
            }
    }

    private boolean isStudentEnrolledInCourseWithId(Student student, Long courseId) {
        // Check if the student is enrolled in any course with the specified course ID
        Set<Course> enrolledCourses = student.getCoursesEnrolledIn();
        if (enrolledCourses != null) {
            for (Course enrolledCourse : enrolledCourses) {
                if (enrolledCourse.getId().equals(courseId)) {
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
