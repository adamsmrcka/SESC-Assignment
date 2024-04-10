package uk.ac.leedsbeckett.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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

    @PostMapping("/enrol")
    public String enrollStudent(@RequestParam("courseId") Long courseId, RedirectAttributes attributes) {
        try {
            Course course = courseService.getCourseById(courseId);
            System.out.println(courseId);
            Student student = studentService.getCurrentUser();

            enrolmentService.enrolStudentInCourse(student, course);

            attributes.addFlashAttribute("enrollmentSuccessMessage", "Enrollment successful!");
            return "redirect:/enrollment-success";
        } catch (IllegalStateException e) {
            attributes.addFlashAttribute("errorMessage", "Student is already enrolled in this course");
        } catch (Exception e) {
            attributes.addFlashAttribute("errorMessage", "Failed to enroll in the course. Please try again later.");
        }

        return "redirect:/course-details/" + courseId;
    }
}

