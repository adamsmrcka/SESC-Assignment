package uk.ac.leedsbeckett.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import uk.ac.leedsbeckett.student.Request.EnrolmentRequest;
import uk.ac.leedsbeckett.student.Request.RegistrationRequest;
import uk.ac.leedsbeckett.student.model.*;
import uk.ac.leedsbeckett.student.service.EnrolmentService;
import uk.ac.leedsbeckett.student.service.LoginService;
import uk.ac.leedsbeckett.student.service.StudentService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

/**
 * Controller class managing portal-related operations and endpoints
 */
@RestController
public class PortalController {

    private final CourseRepository courseRepository;
    private final LoginRepository loginRepository;
    private final StudentService studentService;
    private final EnrolmentService enrolmentService;
    private final CourseController courseController;
    private final EnrolmentController enrolmentController;
    private final RegistrationController registrationController;
    private final StudentController studentController;
    private final LoginService loginService;

    /**
     * Constructor to initialize PortalController with required dependencies
     * @param studentController     StudentController instance
     * @param loginService          LoginService instance
     * @param courseRepository      CourseRepository instance
     * @param loginRepository       LoginRepository instance
     * @param studentService        StudentService instance
     * @param courseController      CourseController instance
     * @param enrolmentController   EnrolmentController instance
     * @param enrolmentService      EnrolmentService instance
     * @param registrationController RegistrationController instance
     */
    @Autowired
    public PortalController(StudentController studentController, LoginService loginService, CourseRepository courseRepository, LoginRepository loginRepository, StudentService studentService, CourseController courseController, EnrolmentController enrolmentController, EnrolmentService enrolmentService, RegistrationController registrationController) {
        this.studentController = studentController;
        this.loginService = loginService;
        this.courseRepository = courseRepository;
        this.loginRepository = loginRepository;
        this.studentService = studentService;
        this.courseController = courseController;
        this.enrolmentController = enrolmentController;
        this.enrolmentService = enrolmentService;
        this.registrationController = registrationController;
    }

    /**
     * Endpoint to display the main portal page
     * @return ModelAndView representing the main page view
     */
    @GetMapping("/main")
    public ModelAndView homePage() {
        ModelAndView modelAndView = new ModelAndView();

        // Get current logged-in user
        Student currentUser = studentService.getCurrentUser();

        if (currentUser != null) {
            modelAndView.setViewName("main");
            modelAndView.addObject("currentUser", currentUser);

            boolean isAdmin = false;  // Default to regular user
            Login login = loginRepository.findByStudentID(currentUser.getExternalStudentId());
            if (login != null && login.getType() == Login.UserType.ADMIN) {
                isAdmin = true;
            }

            modelAndView.addObject("isAdmin", isAdmin);
        } else {
            // Redirect to login page if user is not logged in
            RedirectView redirectView = new RedirectView("/login", true);
            modelAndView.setView(redirectView);
        }
        return modelAndView;
    }

    /**
     * Endpoint to display the registration page
     * @param error Error message to display on registration page (optional)
     * @return ModelAndView representing the registration page
     */
    @GetMapping("/register")
    public ModelAndView register(@RequestParam(name = "error", required = false) String error) {
        ModelAndView modelAndView = new ModelAndView("register");
        modelAndView.addObject("registrationRequest", new RegistrationRequest());
        if (error != null) {
            modelAndView.addObject("error", error);
        }
        return modelAndView;
    }

    /**
     * Endpoint to process user registration
     * @param model    Model object to add attributes
     * @param email    Email address of the user
     * @param forename First name of the user
     * @param surname  Last name of the user
     * @param password Password chosen by the user
     * @return ModelAndView representing the result of registration
     */
    @PostMapping("/register")
    public ModelAndView processRegistration(Model model, @RequestParam String email, @RequestParam String forename, @RequestParam String surname, @RequestParam String password) {
        ModelAndView modelAndView = new ModelAndView();
        if (loginService.emailExists(email)) {
            modelAndView.addObject("error", "Email already used. Please try again.");
            RedirectView redirectView = new RedirectView("/register", true);
            modelAndView.setView(redirectView);
        } else {
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
                modelAndView.addObject("error", "Registration failed. Please try again." + e);
                RedirectView redirectView = new RedirectView("/register", true);
                modelAndView.setView(redirectView);
            }
        }
        return modelAndView;
    }

    /**
     * Endpoint to display the registration success page
     * @param studentID ID of the registered student
     * @return ModelAndView representing the registration success page
     */
    @GetMapping("/registrationSuccess")
    public ModelAndView registrationSuccess(@RequestParam String studentID) {
        ModelAndView modelAndView = new ModelAndView("registrationsuccess");
        modelAndView.addObject("studentID", studentID);
        return modelAndView;
    }

    /**
     * Endpoint to display the login page
     * @return ModelAndView representing the login page
     */
    @GetMapping("/login")
    public ModelAndView logIn() {
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("registrationRequest", new RegistrationRequest());
        return modelAndView;
    }

    /**
     * Endpoint to process user login
     * @param model   Model object to add attributes
     * @param email   Email address of the user
     * @param password Password entered by the user
     * @return ModelAndView representing the result of login
     */
    @PostMapping("/login")
    public ModelAndView processLogin(Model model, @RequestParam String email, @RequestParam String password) {
        ModelAndView modelAndView = new ModelAndView();
        if (!loginService.emailExists(email)) {
            modelAndView.addObject("error", "Email does not Exist. Please try again.");
            RedirectView redirectView = new RedirectView("/login", true);
            modelAndView.setView(redirectView);
        } else {
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

    /**
     * Endpoint to display all courses available
     * @return ModelAndView representing the page displaying all courses
     */
    @GetMapping("/all-courses")
    public ModelAndView showAllCourses() {
        ModelAndView modelAndView = new ModelAndView();

        // Get current logged-in user
        Student currentUser = studentService.getCurrentUser();

        if (currentUser != null) {
            boolean isAdmin = false;
            Login login = loginRepository.findByStudentID(currentUser.getExternalStudentId());
            if (login != null && login.getType() == Login.UserType.ADMIN) {
                isAdmin = true;
            }
            modelAndView.addObject("isAdmin", isAdmin);

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

    /**
     * Endpoint to display details of a specific course
     * @param id    ID of the course to display
     * @return ModelAndView representing the course details page
     */
    @GetMapping("/course-details/{id}")
    public ModelAndView showCourseDetails(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView();

        // Get current logged-in user
        Student currentUser = studentService.getCurrentUser();

        if (currentUser != null) {
            boolean isAdmin = false;
            Login login = loginRepository.findByStudentID(currentUser.getExternalStudentId());
            if (login != null && login.getType() == Login.UserType.ADMIN) {
                isAdmin = true;
            }
            modelAndView.addObject("isAdmin", isAdmin);

            EntityModel<Course> responseEntity = courseController.getCourseJson(id);
            Course course = responseEntity.getContent();
            if (course != null) {
                if (!isAdmin) {
                    boolean isStudentEnrolled = enrolmentService.isStudentEnrolledInCourseWithId(currentUser, id);
                    modelAndView.setViewName("course-details");
                    modelAndView.addObject("course", course);
                    modelAndView.addObject("currentUser", currentUser);
                    modelAndView.addObject("isStudentEnrolled", isStudentEnrolled);
                } else {
                    modelAndView.setViewName("course-details");
                    modelAndView.addObject("course", course);
                    modelAndView.addObject("currentUser", currentUser);
                }
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

    /**
     * Endpoint to display enrollment success with invoice details
     * @param reference Reference of the invoice
     * @param date      Due date of the invoice
     * @param amount    Amount of the invoice
     * @return ModelAndView representing the enrollment success page
     */
    @GetMapping("/enrollment-success")
    public ModelAndView showEnrollmentSuccess(@RequestParam("reference") String reference, @RequestParam("dueDate") LocalDate date, @RequestParam("amount") Double amount) {
        ModelAndView modelAndView = new ModelAndView();
        Student currentUser = studentService.getCurrentUser();
        if (currentUser != null) {
            boolean isAdmin = false;
            Login login = loginRepository.findByStudentID(currentUser.getExternalStudentId());
            if (login != null && login.getType() == Login.UserType.ADMIN) {
                RedirectView redirectView = new RedirectView("/main", true);
                modelAndView.setView(redirectView);
            } else {
                modelAndView.addObject("isAdmin", isAdmin);
                // Retrieve invoice details from the model attributes
                if (reference != null && date != null && amount != null) {
                    Invoice invoice = new Invoice();
                    invoice.setReference(reference);
                    invoice.setDueDate(date);
                    invoice.setAmount(amount);
                    modelAndView.setViewName("enrollment-success");
                    modelAndView.addObject("invoice", invoice);
                    modelAndView.addObject("currentUser", currentUser);
                } else {
                    // If no invoice details found, handle the error scenario gracefully
                    modelAndView.addObject("errorMessage", "No invoice details found");
                    RedirectView redirectView = new RedirectView("/enrollment-success", true);
                    modelAndView.setView(redirectView);
                }
            }
        } else {
            RedirectView redirectView = new RedirectView("/login", true);
            modelAndView.setView(redirectView);
        }
        return modelAndView;
    }

    /**
     * Endpoint to process student enrollment in a course
     * @param courseId ID of the course to enroll in
     * @return ModelAndView representing the result of the enrollment
     */
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
                EntityModel<Invoice> responseEntity = enrolmentController.enrolStudentJson(request);
                Invoice invoice = responseEntity.getContent();

                if (invoice == null) {
                    throw new RuntimeException("Enrollment failed. Please try again later.");
                }
                modelAndView.addObject("invoice", invoice);
                RedirectView redirectView = new RedirectView("/enrollment-success?reference=" + invoice.getReference()
                        + "&dueDate=" + invoice.getDueDate() + "&amount=" + invoice.getAmount(), true);
                modelAndView.setView(redirectView);
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
        } else {
            // Redirect to login page if user is not logged in
            RedirectView redirectView = new RedirectView("/login", true);
            modelAndView.setView(redirectView);
        }
        return modelAndView;
    }

    /**
     * Endpoint to display courses enrolled by the current user
     * @return ModelAndView representing the page displaying user's enrolled courses
     */
    @GetMapping("/my-courses")
    public ModelAndView showMyCourses() {
        ModelAndView modelAndView = new ModelAndView();

        // Get current logged-in user
        Student currentUser = studentService.getCurrentUser();

        if (currentUser != null) {
            boolean isAdmin = false;
            Login login = loginRepository.findByStudentID(currentUser.getExternalStudentId());
            if (login != null && login.getType() == Login.UserType.ADMIN) {
                RedirectView redirectView = new RedirectView("/main", true);
                modelAndView.setView(redirectView);
            } else {
                modelAndView.addObject("isAdmin", isAdmin);
                CollectionModel<EntityModel<Course>> coursesModel = courseController.getEnrolledCoursesByStudentId(currentUser.getId());
                Collection<EntityModel<Course>> courses = coursesModel.getContent();
                modelAndView.setViewName("my-courses");
                modelAndView.addObject("courses", courses);
                modelAndView.addObject("currentUser", currentUser);
            }
        } else {
            // Redirect to login page if user is not logged in
            RedirectView redirectView = new RedirectView("/login", true);
            modelAndView.setView(redirectView);
        }
        return modelAndView;
    }

    /**
     * Endpoint to display the user's profile
     * @param error   Optional error message
     * @param success Optional success message
     * @return ModelAndView representing the user's profile page
     */
    @GetMapping("/profile")
    public ModelAndView showMyProfile(@RequestParam(name = "error", required = false) String error, @RequestParam(name = "success", required = false) String success) {
        ModelAndView modelAndView = new ModelAndView();

        // Get current logged-in user
        Student currentUser = studentService.getCurrentUser();

        if (currentUser != null) {
            boolean isAdmin = false;
            Login login = loginRepository.findByStudentID(currentUser.getExternalStudentId());
            if (login != null && login.getType() == Login.UserType.ADMIN) {
                isAdmin = true;
            }
            modelAndView.addObject("isAdmin", isAdmin);
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

    /**
     * Endpoint to update the user's profile
     * @param updateStudent Updated Student object containing profile updates
     * @return ModelAndView representing the result of the profile update
     */
    @PostMapping("/profile")
    public ModelAndView updateStudent(Student updateStudent) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            Student currentUser = studentService.getCurrentUser();
            if (currentUser != null) {
                updateStudent.setId(currentUser.getId());
                updateStudent.setExternalStudentId(currentUser.getExternalStudentId());
                ResponseEntity<EntityModel<Student>> responseEntity = studentController.updateStudentJson(updateStudent);
                Student student = responseEntity.getBody().getContent();

                if (student == null) {
                    RedirectView redirectView = new RedirectView("/profile", true);
                    modelAndView.setView(redirectView);
                } else {
                    studentService.setCurrentUser(student);
                    modelAndView.addObject("success", "Profile updated!");
                    RedirectView redirectView = new RedirectView("/profile", true);
                    modelAndView.setView(redirectView);
                }
            } else {
                // User not logged in, redirect to login page
                RedirectView redirectView = new RedirectView("/login", true);
                modelAndView.setView(redirectView);
            }
        } catch (Exception e) {
            modelAndView.addObject("error", "Profile update failed! Please try again.");
            RedirectView redirectView = new RedirectView("/profile", true);
            modelAndView.setView(redirectView);
        }
        return modelAndView;
    }

    /**
     * Endpoint to check graduation eligibility and display graduation status
     * @return ModelAndView representing the graduation status page
     */
    @GetMapping("/graduation")
    public ModelAndView showGraduation() {
        ModelAndView modelAndView = new ModelAndView();

        // Get current logged-in user
        Student currentUser = studentService.getCurrentUser();
        ResponseEntity<String> response = studentController.getGraduateEligibility(currentUser.getExternalStudentId());
        String eligibility = response.getBody();
        if (currentUser != null) {
            boolean isAdmin = false;
            Login login = loginRepository.findByStudentID(currentUser.getExternalStudentId());
            if (login != null && login.getType() == Login.UserType.ADMIN) {
                RedirectView redirectView = new RedirectView("/main", true);
                modelAndView.setView(redirectView);
            } else {
                modelAndView.addObject("isAdmin", isAdmin);
                modelAndView.addObject("currentUser", currentUser);
                if (Objects.equals(eligibility.toLowerCase(), "eligible")) {
                    modelAndView.setViewName("graduation");
                    modelAndView.addObject("eligible", true);
                } else if (Objects.equals(eligibility.toLowerCase(), "non-eligible")) {
                    modelAndView.addObject("eligible", false);
                }
            }
        } else {
            // Redirect to login page if user is not logged in
            RedirectView redirectView = new RedirectView("/login", true);
            modelAndView.setView(redirectView);
        }
        return modelAndView;
    }

    /**
     * Endpoint to display the course creation page
     * @param error   Optional error message
     * @param success Optional success message
     * @return ModelAndView representing the course creation page
     */
    @GetMapping("/create-course")
    public ModelAndView showCreateCourse(@RequestParam(name = "error", required = false) String error, @RequestParam(name = "success", required = false) String success) {
        ModelAndView modelAndView = new ModelAndView();

        // Get current logged-in user
        Student currentUser = studentService.getCurrentUser();

        if (currentUser != null) {
            Login login = loginRepository.findByStudentID(currentUser.getExternalStudentId());
            if (login.getType() == Login.UserType.ADMIN) {
                Course course = new Course();
                modelAndView.setViewName("create-course");
                modelAndView.addObject("currentUser", currentUser);
                modelAndView.addObject("course", course);
                modelAndView.addObject("isAdmin", true);
                if (error != null) {
                    modelAndView.addObject("error", error);
                }
                if (success != null) {
                    modelAndView.addObject("success", success);
                }
            } else {
                RedirectView redirectView = new RedirectView("/main", true);
                modelAndView.setView(redirectView);
            }

        } else {
            // Redirect to login page if user is not logged in
            RedirectView redirectView = new RedirectView("/login", true);
            modelAndView.setView(redirectView);
        }
        return modelAndView;
    }

    /**
     * Endpoint to delete a course
     * @param courseId ID of the course to be deleted
     * @return ModelAndView representing the result of the deletion operation
     */
    @PostMapping("/delete-course")
    public ModelAndView deleteCourse(@RequestParam("courseId") Long courseId) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            Student currentUser = studentService.getCurrentUser();
            if (currentUser != null) {
                boolean isAdmin = false;
                Login login = loginRepository.findByStudentID(currentUser.getExternalStudentId());
                if (login != null && login.getType() == Login.UserType.ADMIN) {
                    isAdmin = true;
                }
                modelAndView.addObject("isAdmin", isAdmin);
                ResponseEntity<String> responseEntity = courseController.deleteCourseJson(courseId);
                String response = responseEntity.getBody();

                if (response == null) {
                    modelAndView.addObject("error", "Deleting a course failed! Please try again.");
                    RedirectView redirectView = new RedirectView("/course-details/" + courseId, true);
                    modelAndView.setView(redirectView);
                } else {
                    modelAndView.addObject("success", "Course created!");
                    RedirectView redirectView = new RedirectView("/all-courses", true);
                    modelAndView.setView(redirectView);
                }
            } else {
                // User not logged in, redirect to login page
                RedirectView redirectView = new RedirectView("/login", true);
                modelAndView.setView(redirectView);
            }
        } catch (Exception e) {
            modelAndView.addObject("error", "Deleting a course failed! Please try again.");
            RedirectView redirectView = new RedirectView("/course-details/" + courseId, true);
            modelAndView.setView(redirectView);
        }
        return modelAndView;
    }

    /**
     * Endpoint to create a new course
     * @param newCourse New Course object containing course details
     * @return ModelAndView representing the result of the course creation
     */
    @PostMapping("/create-course")
    public ModelAndView createCourse(Course newCourse) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            Student currentUser = studentService.getCurrentUser();
            if (currentUser != null) {
                boolean isAdmin = false;
                Login login = loginRepository.findByStudentID(currentUser.getExternalStudentId());
                if (login != null && login.getType() == Login.UserType.ADMIN) {
                    isAdmin = true;
                }
                modelAndView.addObject("isAdmin", isAdmin);
                ResponseEntity<EntityModel<Course>> responseEntity = courseController.createCourseJson(newCourse);
                Course course = responseEntity.getBody().getContent();

                if (course == null) {
                    modelAndView.addObject("error", "Creating a new course failed! Please try again.");
                    RedirectView redirectView = new RedirectView("/create-course", true);
                    modelAndView.setView(redirectView);
                } else {
                    modelAndView.addObject("success", "Course created!");
                    RedirectView redirectView = new RedirectView("/create-course", true);
                    modelAndView.setView(redirectView);
                }
            } else {
                // User not logged in, redirect to login page
                RedirectView redirectView = new RedirectView("/login", true);
                modelAndView.setView(redirectView);
            }
        } catch (Exception e) {
            modelAndView.addObject("error", "Creating a new course failed! Please try again.");
            RedirectView redirectView = new RedirectView("/create-course", true);
            modelAndView.setView(redirectView);
        }
        return modelAndView;
    }

    /**
     * Endpoint to sign out the current user
     * @return ModelAndView representing the sign-out operation
     */
    @GetMapping("/sign-out")
    public ModelAndView signOut() {
        ModelAndView modelAndView = new ModelAndView();

        registrationController.logout();
        RedirectView redirectView = new RedirectView("/login", true);
        modelAndView.setView(redirectView);
        return modelAndView;
    }
}
