package uk.ac.leedsbeckett.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.leedsbeckett.student.model.Course;
import uk.ac.leedsbeckett.student.model.Student;
import uk.ac.leedsbeckett.student.service.CourseService;
import uk.ac.leedsbeckett.student.service.EnrolmentService;
import uk.ac.leedsbeckett.student.service.StudentService;

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
            if (course == null) {
                throw new IllegalArgumentException("Course with id " + courseId + " not found");
            }

            // Get the currently logged-in student
            Student student = studentService.getCurrentUser();
            if (student == null) {
                throw new IllegalStateException("No logged-in student found");
            }

            // Enroll student in the course
            enrolmentService.enrolStudentInCourse(student, course);
            attributes.addFlashAttribute("enrollmentSuccessMessage", "Enrollment successful!");
            return "redirect:/enrollment-success";
        } catch (RuntimeException e) {
            String errorMessage;
            if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                errorMessage = e.getMessage();
            } else {
                errorMessage = "Failed to enroll in the course. Please try again later.";
            }
            attributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/course-details/" + courseId;
        } catch (Exception e) {
            attributes.addFlashAttribute("errorMessage", "Failed to enroll in the course. Please try again later.");
            return "redirect:/course-details/" + courseId;
        }
    }
}
