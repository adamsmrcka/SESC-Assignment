package uk.ac.leedsbeckett.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.leedsbeckett.student.model.Course;
import uk.ac.leedsbeckett.student.service.CourseService;
/**
 * Controller class for managing course-related operations via RESTful endpoints
 */
@RestController
@RequestMapping("/api")
public class CourseController {
    private final CourseService courseService;

    /**
     * Constructor to initialize CourseController with CourseService dependency injection
     * @param courseService The CourseService instance to be injected
     */
    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }
    /**
     * Endpoint to retrieve all courses as JSON
     * @return CollectionModel of EntityModel containing all courses
     */
    @GetMapping("/courses")
    public CollectionModel<EntityModel<Course>> getAllCoursesJson() {
        return courseService.getAllCoursesJson();
    }

    /**
     * Endpoint to retrieve a specific course by ID as JSON
     * @param id The ID of the course to retrieve
     * @return EntityModel representing the requested course
     */
    @GetMapping("/courses/{id}")
    public EntityModel<Course> getCourseJson(@PathVariable Long id) {
        return courseService.getCourseByIdJson(id);
    }

    /**
     * Endpoint to retrieve all courses enrolled by a specific student as JSON
     * @param id The ID of the student to retrieve enrolled courses for
     * @return CollectionModel of EntityModel containing enrolled courses of the student
     */
    @GetMapping("/courses/student/{id}")
    public CollectionModel<EntityModel<Course>> getEnrolledCoursesByStudentId(@PathVariable Long id) {
        return courseService.getEnrolledCoursesByStudentIdJson(id);
    }

    /**
     * Endpoint to create a new course
     * @param newCourse The Course object representing the new course to create
     * @return ResponseEntity containing the created course as EntityModel
     */
    @PostMapping("/courses/new")
    public ResponseEntity<EntityModel<Course>> createCourseJson(@RequestBody Course newCourse) {
        return courseService.createNewCourseJson(newCourse);
    }

    /**
     * Endpoint to delete a course by ID
     * @param id The ID of the course to delete
     * @return ResponseEntity containing a message indicating the status of the deletion
     */
    @PostMapping("/courses/delete/{id}")
    public ResponseEntity<String> deleteCourseJson(@PathVariable Long id) {
        return courseService.deleteCourseByIdJson(id);
    }

}
