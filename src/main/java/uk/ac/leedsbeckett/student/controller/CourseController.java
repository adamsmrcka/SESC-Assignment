package uk.ac.leedsbeckett.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.leedsbeckett.student.model.Course;
import uk.ac.leedsbeckett.student.service.CourseService;

@RestController
@RequestMapping("/api")
public class CourseController {
    private final CourseService courseService;


    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/courses")
    public CollectionModel<EntityModel<Course>> getAllCoursesJson() {
        return courseService.getAllCoursesJson();
    }

    @GetMapping("/courses/{id}")
    public EntityModel<Course> getCourseJson(@PathVariable Long id) {
        return courseService.getCourseByIdJson(id);
    }

    @GetMapping("/courses/student/{id}")
    public CollectionModel<EntityModel<Course>> getEnrolledCoursesByStudentId(@PathVariable Long id) {
        return courseService.getEnrolledCoursesByStudentIdJson(id);
    }

    @PostMapping("/courses/new")
    public ResponseEntity<EntityModel<Course>> createCourseJson(@RequestBody Course newCourse) {
        return courseService.createNewCourseJson(newCourse);
    }

    @PostMapping("/courses/delete/{id}")
    public ResponseEntity<String> deleteCourseJson(@PathVariable Long id) {
        return courseService.deleteCourseByIdJson(id);
    }

}
