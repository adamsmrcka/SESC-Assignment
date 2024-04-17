package uk.ac.leedsbeckett.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import uk.ac.leedsbeckett.student.Request.EnrolmentRequest;
import uk.ac.leedsbeckett.student.Request.RegistrationRequest;
import uk.ac.leedsbeckett.student.model.*;
import uk.ac.leedsbeckett.student.service.EnrolmentService;
import uk.ac.leedsbeckett.student.service.LoginService;
import uk.ac.leedsbeckett.student.service.StudentService;

import java.util.Collection;

@RestController
public class PortalController {

    private final CourseRepository courseRepository;
    private final StudentService studentService;
    private final EnrolmentService enrolmentService;
    private final CourseController courseController;
    private final EnrolmentController enrolmentController;
    private final RegistrationController registrationController;
    private final LoginService loginService;

    @Autowired
    public PortalController(LoginService loginService, CourseRepository courseRepository, StudentService studentService, CourseController courseController, EnrolmentController enrolmentController, EnrolmentService enrolmentService, RegistrationController registrationController) {
        this.loginService = loginService;
        this.courseRepository = courseRepository;
        this.studentService = studentService;
        this.courseController = courseController;
        this.enrolmentController = enrolmentController;
        this.enrolmentService = enrolmentService;
        this.registrationController = registrationController;
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

    @GetMapping("/register")
    public ModelAndView register(@RequestParam(name = "error", required = false) String error) {
        ModelAndView modelAndView = new ModelAndView("register");
        modelAndView.addObject("registrationRequest", new RegistrationRequest());
        if (error != null) {
            modelAndView.addObject("error", error);
        }
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView processRegistration(Model model, RedirectAttributes attributes, @RequestParam String email, @RequestParam String forename, @RequestParam String surname, @RequestParam String password) {
        ModelAndView modelAndView = new ModelAndView();
        if (loginService.emailExists(email)) {
            modelAndView.addObject("error", "Email already used. Please try again.");
            RedirectView redirectView = new RedirectView("/register", true);
            modelAndView.setView(redirectView);
        }
        else {
            try {
                RegistrationRequest request = new RegistrationRequest();
                request.setForename(forename);
                request.setEmail(email);
                request.setSurname(surname);
                request.setPassword(password);
                request.setType("user");
                ResponseEntity<EntityModel<Student>> responseEntity = registrationController.CreateNewStudentJson(request);
                Student student = responseEntity.getBody().getContent();
                if (student != null && student.getId() != null) {
                    modelAndView.addObject("studentId", student.getId());
                    RedirectView redirectView = new RedirectView("/registration_success", true);
                    modelAndView.setView(redirectView);
                } else {
                    model.addAttribute("error", "Registration failed! Please try again.");
                    RedirectView redirectView = new RedirectView("/register", true);
                    modelAndView.setView(redirectView);
                }
            } catch (Exception e) {
                modelAndView.addObject("error", "Registration failed. Please try again.");
                RedirectView redirectView = new RedirectView("/register", true);
                modelAndView.setView(redirectView);
            }
        }
        return modelAndView;
    }


    @GetMapping("/registrationSuccess")
    public ModelAndView registrationSuccess(@RequestParam String studentID) {
        ModelAndView modelAndView = new ModelAndView("registrationSuccess");
        modelAndView.addObject("studentID", studentID);
        return modelAndView;
    }

    @GetMapping("/login")
    public ModelAndView logIn() {
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("registrationRequest", new RegistrationRequest());
        return modelAndView;
    }

    @GetMapping("/all-courses")
    public ModelAndView showAllCourses(Model model) {
        ModelAndView modelAndView = new ModelAndView();

        // Get current logged-in user
        Student currentUser = studentService.getCurrentUser();

        if (currentUser != null) {
            CollectionModel<EntityModel<Course>> coursesModel = courseController.getAllCoursesJson();
            Collection<EntityModel<Course>> courses = coursesModel.getContent();
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
            EntityModel<Course> responseEntity = courseController.getCourseJson(id);
            Course course = responseEntity.getContent();
            if (course != null) {
                boolean isStudentEnrolled = enrolmentService.isStudentEnrolledInCourseWithId(currentUser, id);
                modelAndView.setViewName("course-details");
                modelAndView.addObject("course", course);
                modelAndView.addObject("currentUser", currentUser);
                modelAndView.addObject("isStudentEnrolled", isStudentEnrolled);
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

    @PostMapping("/enrol")
    public String enrollStudent(@RequestParam("courseId") Long courseId, RedirectAttributes attributes) {
        try {
            Student currentUser = studentService.getCurrentUser();
            if (currentUser != null) {
                Course course = courseRepository.findCourseById(courseId);
                EnrolmentRequest request = new EnrolmentRequest();
                request.setStudentId(currentUser.getId()); // Set the current user as the student
                request.setCourseId(course.getId());
                ResponseEntity<Invoice> responseEntity = enrolmentController.enrolStudentJson(request);
                Invoice invoice = responseEntity.getBody();

                //enrolmentService.enrolStudentInCourse(currentUser, course);
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
}
