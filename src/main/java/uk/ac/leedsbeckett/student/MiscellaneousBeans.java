package uk.ac.leedsbeckett.student;

import java.util.List;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import uk.ac.leedsbeckett.student.model.Course;
import uk.ac.leedsbeckett.student.model.CourseRepository;
import uk.ac.leedsbeckett.student.model.Student;
import uk.ac.leedsbeckett.student.model.StudentRepository;

/**
 * Controller class for handling miscellaneous operations related to students and courses.
 */
@RestController
@RequestMapping("/student/course")
public class MiscellaneousBeans {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    /**
     * Constructor to initialize repositories for dependency injection
     * @param studentRepository Repository for student data access
     * @param courseRepository  Repository for course data access
     */
    public MiscellaneousBeans(StudentRepository studentRepository,
                              CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * Endpoint to retrieve all students
     * @return List of all students
     */
    @GetMapping("/student")
    public List<Student> findALlStudents() {
        return studentRepository.findAll();
    }

    /**
     * Endpoint to find a student by ID
     * @param id The ID of the student to find
     * @return The found student or null if not found
     */
    @GetMapping("/studentId")
    public Student findStudent(@RequestParam(value = "id") long id) {
        return studentRepository.findById(id).orElse(null);
    }

    /**
     * Endpoint to find students by surname containing a specified string
     * @param surname The partial or full surname to search for
     * @return List of students with surnames containing the specified string
     */
    @GetMapping("/find")
    public List<Student> findStudentsContainingByName(@RequestParam(value = "surname") String surname) {
        return studentRepository.findBySurnameContaining(surname);
    }

    /**
     * Endpoint to find courses with fees less than a specified price
     * @param price The maximum price of courses to search for
     * @return List of courses with fees less than the specified price
     */
    @GetMapping("/search/{price}")
    public List<Course> findCourseLessThanPrice(@PathVariable double price) {
        return courseRepository.findByFeeLessThan(price);
    }
    /**
     * Bean definition for creating a RestTemplate instance
     * @param builder RestTemplateBuilder instance for building the RestTemplate
     * @return RestTemplate instance to be used for RESTful HTTP requests
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

}