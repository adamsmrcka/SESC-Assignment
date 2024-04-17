package uk.ac.leedsbeckett.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import uk.ac.leedsbeckett.student.model.Course;
import uk.ac.leedsbeckett.student.model.Student;
import uk.ac.leedsbeckett.student.service.CourseService;
import uk.ac.leedsbeckett.student.service.StudentService;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/api")
public class CourseController {
    private final CourseService courseService;
    private final StudentService studentService;


    @Autowired
    public CourseController(CourseService courseService, StudentService studentService) {
        this.courseService = courseService;
        this.studentService = studentService;
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

}
