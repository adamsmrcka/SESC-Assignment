package uk.ac.leedsbeckett.student.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.ac.leedsbeckett.student.controller.CourseController;
import uk.ac.leedsbeckett.student.model.Course;
import uk.ac.leedsbeckett.student.model.CourseRepository;
import uk.ac.leedsbeckett.student.model.Student;
import uk.ac.leedsbeckett.student.model.StudentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        Optional<Course> courseOptional = courseRepository.findById(id);
        return courseOptional.orElse(null);
    }

    public CollectionModel<EntityModel<Course>> getAllCoursesJson() {
        List<EntityModel<Course>> courseList = courseRepository.findAll()
                .stream()
                .map(EntityModel::of)
                .collect(Collectors.toList());
        return CollectionModel.of(courseList, linkTo(methodOn(CourseController.class)
                .getAllCoursesJson())
                .withSelfRel());
    }

    public EntityModel<Course> getCourseByIdJson (Long id){
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course with id " + id + " not found."));
        return EntityModel.of(course,
            linkTo(methodOn(CourseController.class).getCourseJson(course.getId())).withSelfRel(),
            linkTo(methodOn(CourseController.class).getAllCoursesJson()).withRel("courses"));
    }


    public CollectionModel<EntityModel<Course>> getEnrolledCoursesByStudentIdJson(Long studentId){
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        List<EntityModel<Course>> courseList = student.getCoursesEnrolledIn()
                .stream()
                .map(EntityModel::of)
                .collect(Collectors.toList());
        return CollectionModel.of(courseList, linkTo(methodOn(CourseController.class)
                .getAllCoursesJson())
                .withSelfRel());
    }
}
