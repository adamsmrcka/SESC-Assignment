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
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
/**
 * Service class responsible for managing course-related operations
 */
@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    /**
     * Constructor for CourseService class
     * @param courseRepository Repository for accessing course data
     * @param studentRepository Repository for accessing student data
     */
    @Autowired
    public CourseService(CourseRepository courseRepository, StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * Retrieves a course by its ID
     * @param id The ID of the course to retrieve
     * @return The course if found, otherwise null
     */
    public Course getCourseById(Long id) {
        Optional<Course> courseOptional = courseRepository.findById(id);
        return courseOptional.orElse(null);
    }

    /**
     * Retrieves all courses in JSON format
     * @return CollectionModel containing EntityModels of all courses
     */
    public CollectionModel<EntityModel<Course>> getAllCoursesJson() {
        List<EntityModel<Course>> courseList = courseRepository.findAll()
                .stream()
                .map(EntityModel::of)
                .collect(Collectors.toList());
        return CollectionModel.of(courseList, linkTo(methodOn(CourseController.class)
                .getAllCoursesJson())
                .withSelfRel());
    }

    /**
     * Retrieves a course by its ID in JSON format
     * @param id The ID of the course to retrieve
     * @return EntityModel representing the course
     * @throws RuntimeException if the course with the specified ID is not found
     */
    public EntityModel<Course> getCourseByIdJson(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course with id " + id + " not found."));
        return EntityModel.of(course,
                linkTo(methodOn(CourseController.class).getCourseJson(course.getId())).withSelfRel(),
                linkTo(methodOn(CourseController.class).getAllCoursesJson()).withRel("courses"));
    }

    /**
     * Retrieves the enrolled courses of a student by their ID in JSON format
     * @param studentId The ID of the student whose enrolled courses are to be retrieved
     * @return CollectionModel containing EntityModels of the enrolled courses of the student
     * @throws RuntimeException if no student is found with the specified ID
     */
    public CollectionModel<EntityModel<Course>> getEnrolledCoursesByStudentIdJson(Long studentId) {
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

    /**
     * Creates a new course and returns a ResponseEntity containing the created course in JSON format
     * @param newCourse The new course to create
     * @return ResponseEntity containing the created course as an EntityModel
     */
    public ResponseEntity<EntityModel<Course>> createNewCourseJson(Course newCourse) {
        Course savedCourse = courseRepository.save(newCourse);
        EntityModel<Course> entityModel = EntityModel.of(savedCourse,
                linkTo(methodOn(CourseController.class).getCourseJson(savedCourse.getId())).withSelfRel(),
                linkTo(methodOn(CourseController.class).getAllCoursesJson()).withRel("courses"));
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    /**
     * Deletes a course by its ID and returns a ResponseEntity indicating the deletion status
     * @param courseId The ID of the course to delete
     * @return ResponseEntity containing a message indicating the deletion status
     */
    public ResponseEntity<String> deleteCourseByIdJson(Long courseId) {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Course courseToDelete = courseOptional.get();
        // Before deleting the course, make sure to remove this course from any students enrolled
        Set<Student> studentsEnrolled = courseToDelete.getStudentsEnrolledInCourse();
        for (Student student : studentsEnrolled) {
            student.getCoursesEnrolledIn().remove(courseToDelete);
        }

        courseRepository.delete(courseToDelete);
        return ResponseEntity.ok("Course deleted: " + courseToDelete.getTitle());
    }

}
