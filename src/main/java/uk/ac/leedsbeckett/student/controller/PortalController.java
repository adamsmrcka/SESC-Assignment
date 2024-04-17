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
import uk.ac.leedsbeckett.student.service.IntegrationService;
import uk.ac.leedsbeckett.student.service.LoginService;
import uk.ac.leedsbeckett.student.service.StudentService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

@RestController
public class PortalController {

    private final CourseRepository courseRepository;
    private final StudentService studentService;
    private final EnrolmentService enrolmentService;
    private final CourseController courseController;
    private final EnrolmentController enrolmentController;
    private final RegistrationController registrationController;
    private final StudentController studentController;
    private final LoginService loginService;
    private final IntegrationService integrationService;

    @Autowired
    public PortalController(IntegrationService integrationService, StudentController studentController, LoginService loginService, CourseRepository courseRepository, StudentService studentService, CourseController courseController, EnrolmentController enrolmentController, EnrolmentService enrolmentService, RegistrationController registrationController) {
        this.integrationService = integrationService;
        this.studentController = studentController;
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
    public ModelAndView processRegistration(Model model, @RequestParam String email, @RequestParam String forename, @RequestParam String surname, @RequestParam String password) {
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
                    modelAndView.setViewName("redirect:/registrationSuccess");
                    modelAndView.addObject("studentID", student.getExternalStudentId());
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
        ModelAndView modelAndView = new ModelAndView("registrationsuccess");
        modelAndView.addObject("studentID", studentID);
        return modelAndView;
    }

    @GetMapping("/login")
    public ModelAndView logIn() {
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("registrationRequest", new RegistrationRequest());
        return modelAndView;
    }

    @PostMapping("/login")
    public ModelAndView processLogin(Model model, @RequestParam String email, @RequestParam String password) {
        ModelAndView modelAndView = new ModelAndView();
        if (!loginService.emailExists(email)) {
            modelAndView.addObject("error", "Email does not Exist. Please try again.");
            RedirectView redirectView = new RedirectView("/login", true);
            modelAndView.setView(redirectView);
        }
        else {
            try {
                RegistrationRequest request = new RegistrationRequest();
                request.setEmail(email);
                request.setPassword(password);
                ResponseEntity<EntityModel<Student>> responseEntity = registrationController.checkLoginJson(request);
                Student student = responseEntity.getBody().getContent();
                if (student != null && student.getId() != null) {
                    modelAndView.setViewName("redirect:/main");
                } else {
                    model.addAttribute("error", "Login failed! Please try again.");
                    RedirectView redirectView = new RedirectView("/login", true);
                    modelAndView.setView(redirectView);
                }
            } catch (Exception e) {
                modelAndView.addObject("error", "Login failed. Please try again.");
                RedirectView redirectView = new RedirectView("/login", true);
                modelAndView.setView(redirectView);
            }
        }
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
    public ModelAndView showEnrollmentSuccess(@RequestParam("reference") String reference, @RequestParam("dueDate") LocalDate date, @RequestParam("amount") Double amount) {
        ModelAndView modelAndView = new ModelAndView();
        Student currentUser = studentService.getCurrentUser();
        if (currentUser != null) {
            // Retrieve invoice details from the model attributes
            if (reference != null && date != null && amount != null) {
                Invoice invoice = new Invoice();
                invoice.setReference(reference);
                invoice.setDueDate(date);
                invoice.setAmount(amount);
                modelAndView.setViewName("enrollment-success");
                modelAndView.addObject("invoice", invoice);
                modelAndView.addObject("currentUser", currentUser);
            }
            else {
                // If no invoice details found, handle the error scenario gracefully
                modelAndView.addObject("errorMessage", "No invoice details found");
                RedirectView redirectView = new RedirectView("/enrollment-success", true);
                modelAndView.setView(redirectView);
            }
        }
        else {
            RedirectView redirectView = new RedirectView("/login", true);
            modelAndView.setView(redirectView);
        }
        return modelAndView;
    }

    @PostMapping("/enrol")
    public ModelAndView enrollStudent(@RequestParam("courseId") Long courseId) {
        ModelAndView modelAndView = new ModelAndView();
        Student currentUser = studentService.getCurrentUser();
            if (currentUser != null) {
                try {
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
                    modelAndView.addObject("invoice", invoice);
                    RedirectView redirectView = new RedirectView("/enrollment-success?reference=" + invoice.getReference()
                            + "&dueDate=" + invoice.getDueDate() + "&amount=" + invoice.getAmount(), true);                    modelAndView.setView(redirectView);
                } catch (RuntimeException e) {
                    String errorMessage;
                    if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                        errorMessage = e.getMessage();
                    } else {
                        errorMessage = "Failed to enroll in the course. Please try again later.";
                    }
                    modelAndView.addObject("errorMessage", errorMessage);
                    RedirectView redirectView = new RedirectView("/course-details/" + courseId, true);
                    modelAndView.setView(redirectView);
                } catch (Exception e) {
                    modelAndView.addObject("errorMessage", "Failed to enroll in the course. Please try again later.");
                    RedirectView redirectView = new RedirectView("/course-details/" + courseId, true);
                    modelAndView.setView(redirectView);
                }
            }
            else {
                // Redirect to login page if user is not logged in
                RedirectView redirectView = new RedirectView("/login", true);
                modelAndView.setView(redirectView);
            }
        return modelAndView;
    }
    @GetMapping("/my-courses")
    public ModelAndView showMyCourses(Model model) {
        ModelAndView modelAndView = new ModelAndView();

        // Get current logged-in user
        Student currentUser = studentService.getCurrentUser();

        if (currentUser != null) {
            CollectionModel<EntityModel<Course>> coursesModel = courseController.getEnrolledCoursesByStudentId(currentUser.getId());
            Collection<EntityModel<Course>> courses = coursesModel.getContent();
            modelAndView.setViewName("my-courses");
            modelAndView.addObject("courses", courses);
            modelAndView.addObject("currentUser", currentUser);
        } else {
            // Redirect to login page if user is not logged in
            RedirectView redirectView = new RedirectView("/login", true);
            modelAndView.setView(redirectView);
        }
        return modelAndView;
    }

    @GetMapping("/profile")
    public ModelAndView showMyProfile(@RequestParam(name = "error", required = false) String error, @RequestParam(name = "success", required = false) String success) {
        ModelAndView modelAndView = new ModelAndView();

        // Get current logged-in user
        Student currentUser = studentService.getCurrentUser();

        if (currentUser != null) {
            modelAndView.setViewName("profile");
            modelAndView.addObject("currentUser", currentUser);
            modelAndView.addObject("student", currentUser);
            if (error != null) {
                modelAndView.addObject("error", error);
            }
            if (success != null) {
                modelAndView.addObject("success", success);
            }

        } else {
            // Redirect to login page if user is not logged in
            RedirectView redirectView = new RedirectView("/login", true);
            modelAndView.setView(redirectView);
        }
        return modelAndView;
    }

    @PostMapping("/profile")
    public ModelAndView updateStudent(Model model, Student updateStudent) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            Student currentUser = studentService.getCurrentUser();
            if (currentUser != null) {
                updateStudent.setId(currentUser.getId());
                updateStudent.setExternalStudentId(currentUser.getExternalStudentId());
                ResponseEntity<EntityModel<Student>> responseEntity = studentController.updateStudentJson(updateStudent);
                Student student = responseEntity.getBody().getContent();

                if (student == null) {
                    throw new RuntimeException("Enrollment failed. Please try again later.");
                }
                studentService.setCurrentUser(student);
                model.addAttribute("success", "Profile updated!");
                RedirectView redirectView = new RedirectView("/profile", true);
                modelAndView.setView(redirectView);
            }
            else {
                // User not logged in, redirect to login page
                RedirectView redirectView = new RedirectView("/login", true);
                modelAndView.setView(redirectView);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Profile update failed! Please try again.");
            RedirectView redirectView = new RedirectView("/profile", true);
            modelAndView.setView(redirectView);
        }
        return modelAndView;
    }

    @GetMapping("/graduation")
    public ModelAndView showGraduation() {
        ModelAndView modelAndView = new ModelAndView();

        // Get current logged-in user
        Student currentUser = studentService.getCurrentUser();
        ResponseEntity<String> response = studentController.getGraduateEligibility(currentUser.getExternalStudentId());
        String eligibility = response.getBody();
        if (currentUser != null) {
            if (Objects.equals(eligibility.toLowerCase(), "eligible")) {
                    modelAndView.setViewName("graduation");
                    modelAndView.addObject("currentUser", currentUser);
                modelAndView.addObject("eligible", true);
            }
                else if(Objects.equals(eligibility.toLowerCase(), "non-eligible")) {
                modelAndView.addObject("eligible", false);
            }
        } else {
            // Redirect to login page if user is not logged in
            RedirectView redirectView = new RedirectView("/login", true);
            modelAndView.setView(redirectView);
        }
        return modelAndView;
    }
}
