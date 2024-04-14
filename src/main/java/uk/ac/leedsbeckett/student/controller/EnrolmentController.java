package uk.ac.leedsbeckett.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import uk.ac.leedsbeckett.student.model.Course;
import uk.ac.leedsbeckett.student.model.Invoice;
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
            Student currentUser = studentService.getCurrentUser();
            if (currentUser != null) {
                Course course = courseService.getCourseById(courseId);
                if (course == null) {
                    throw new IllegalArgumentException("Course with id " + courseId + " not found");
                }


                // Enroll student in the course
                Invoice invoice = enrolmentService.enrolStudentInCourse(currentUser, course);
                if (invoice == null) {
                    throw new RuntimeException("Enrollment failed. Please try again later.");
                }
                attributes.addFlashAttribute("invoice", invoice);
                return "redirect:/enrollment-success";
            }
            else {
                // User not logged in, redirect to login page
                return "redirect:/login";
            }
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
    @GetMapping("/enrollment-success")
    public String showEnrollmentSuccess(Model model) {
        Student currentUser = studentService.getCurrentUser();
        if (currentUser != null) {
            // Retrieve invoice details from the model attributes
            if (model.containsAttribute("invoice")) {
                Invoice invoice = (Invoice) model.getAttribute("invoice");
                model.addAttribute("invoice", invoice);
                model.addAttribute("currentUser", currentUser);
                return "/enrollment-success"; // Return the name of the success view template
            } else {
                // If no invoice details found, handle the error scenario gracefully
                model.addAttribute("errorMessage", "No invoice details found");
                return "/course-details/{id}"; // Redirect to an error view
            }

        }
        else {
            // User not logged in, redirect to login page
            return "redirect:/login";
        }
    }
}
