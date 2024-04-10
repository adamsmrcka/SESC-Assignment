package uk.ac.leedsbeckett.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import uk.ac.leedsbeckett.student.model.Course;
import uk.ac.leedsbeckett.student.model.Student;
import uk.ac.leedsbeckett.student.service.CourseService;
import uk.ac.leedsbeckett.student.service.StudentService;

import java.security.Principal;
import java.util.List;

@Controller
public class CourseController {
    private final CourseService courseService;
    private final StudentService studentService;


    @Autowired
    public CourseController(CourseService courseService, StudentService studentService) {
        this.courseService = courseService;
        this.studentService = studentService;
    }
    @GetMapping("/main")
    public ModelAndView homePage() {
        ModelAndView modelAndView = new ModelAndView();

        // Get current logged-in user
        Student currentUser = studentService.getCurrentUser();

        if (currentUser != null) {
            modelAndView.setViewName("main");
            modelAndView.addObject("currentUser", currentUser);
        } else {
            // Redirect to login page if user is not logged in
            RedirectView redirectView = new RedirectView("/login", true);
            modelAndView.setView(redirectView);
        }

        return modelAndView;
    }

    /*public String studentPortal(Model model, Principal principal) {
        // Get current logged-in user
        Student currentUser = studentService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);
        return "main";
    }*/

    @GetMapping("/all-courses")
    public ModelAndView showAllCourses(Model model) {
        ModelAndView modelAndView = new ModelAndView();

        // Get current logged-in user
        Student currentUser = studentService.getCurrentUser();

        if (currentUser != null) {
            List<Course> courses = courseService.getAllCourses();
            modelAndView.setViewName("all-courses");
            modelAndView.addObject("courses", courses);
            modelAndView.addObject("currentUser", currentUser);
        } else {
            // Redirect to login page if user is not logged in
            RedirectView redirectView = new RedirectView("/login", true);
            modelAndView.setView(redirectView);
        }

        return modelAndView;
    }

    @GetMapping("/course-details/{id}")
    public ModelAndView showCourseDetails(@PathVariable Long id, Model model) {
        ModelAndView modelAndView = new ModelAndView();

        // Get current logged-in user
        Student currentUser = studentService.getCurrentUser();

        if (currentUser != null) {
            Course course = courseService.getCourseById(id);
            if (course != null) {
                modelAndView.setViewName("course-details");
                modelAndView.addObject("course", course);
                modelAndView.addObject("currentUser", currentUser);
            } else {
                // Course not found, redirect to all-courses
                RedirectView redirectView = new RedirectView("/all-courses", true);
                modelAndView.setView(redirectView);
            }
        } else {
            // User not logged in, redirect to login page
            RedirectView redirectView = new RedirectView("/login", true);
            modelAndView.setView(redirectView);
        }

        return modelAndView;
    }
}
