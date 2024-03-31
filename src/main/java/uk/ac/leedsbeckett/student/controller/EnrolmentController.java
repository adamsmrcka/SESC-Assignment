package uk.ac.leedsbeckett.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.leedsbeckett.student.model.Course;
import uk.ac.leedsbeckett.student.model.Student;
import uk.ac.leedsbeckett.student.service.CourseService;
import uk.ac.leedsbeckett.student.service.EnrolmentService;
import uk.ac.leedsbeckett.student.service.StudentService;

import java.util.Optional;

@Controller
public class EnrolmentController {
    private final EnrolmentService enrolmentService;
    private final StudentService studentService;
    private final CourseService courseService;

    @Autowired
    public EnrolmentController(EnrolmentService enrolmentService, StudentService studentService, CourseService courseService) {
        this.enrolmentService = enrolmentService;
        this.studentService = studentService;
        this.courseService = courseService;
    }

    @PostMapping("/course-details/{id}")
    public String enrollStudent(@PathVariable Long courseId) {

        Optional<Course> course = courseService.getCourseById(courseId);
        Student student = enrolmentService.getStudent();
        // Enroll the student in the course
        enrolmentService.enrolStudentInCourse(student, course);

        // Redirect to a success page or any other appropriate action
        return "enrollment-success";
    }
}