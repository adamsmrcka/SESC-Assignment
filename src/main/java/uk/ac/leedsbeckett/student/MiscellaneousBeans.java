package uk.ac.leedsbeckett.student;

import java.util.List;
import java.util.Set;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.RestTemplate;
import uk.ac.leedsbeckett.student.model.Course;
import uk.ac.leedsbeckett.student.model.CourseRepository;
import uk.ac.leedsbeckett.student.model.Student;
import uk.ac.leedsbeckett.student.model.StudentRepository;

@RestController
@RequestMapping("/student/course")
public class MiscellaneousBeans {

    private StudentRepository studentRepository;

    private CourseRepository courseRepository;

    public MiscellaneousBeans(StudentRepository studentRepository,
                              CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @PostMapping
    public Student saveStudentWithCourse(@RequestBody Student student) {
        return studentRepository.save(student);
    }

    @GetMapping
    public List<Student> findALlStudents() {
        return studentRepository.findAll();
    }

    @GetMapping("/studentId")
    public Student findStudent(@RequestParam(value = "id") long id) {
        return studentRepository.findById(id).orElse(null);
    }

    @GetMapping("/find")

    public List<Student> findStudentsContainingByName(@RequestParam(value = "surname") String surname) {
        return studentRepository.findBySurnameContaining(surname);
    }

    @GetMapping("/search/{price}")
    public List<Course> findCourseLessThanPrice(@PathVariable double price) {
        return courseRepository.findByFeeLessThan(price);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

}