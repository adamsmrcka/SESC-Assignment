package uk.ac.leedsbeckett.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.leedsbeckett.student.model.Login;
import uk.ac.leedsbeckett.student.model.Student;
import uk.ac.leedsbeckett.student.service.LoginService;
import uk.ac.leedsbeckett.student.service.StudentService;

@Controller
public class RegistrationController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private StudentService studentService;

    @GetMapping(value = "/register")
    public ModelAndView register() {
        ModelAndView modelAndView = new ModelAndView("register");
        modelAndView.addObject("login", new Login());
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView processRegistration(@RequestParam String password, @RequestParam String forename, @RequestParam String surname, @RequestParam String email) {
        // Perform registration logic here
        // For example, save the login details to the database
        String studentID = loginService.registerUser(password, forename, surname, email);
        // Create a new ModelAndView object and set the view name
        ModelAndView modelAndView = new ModelAndView("registrationSuccess");

        // Add the student ID to the model
        modelAndView.addObject("studentID", studentID);

        // Return the ModelAndView object
        return modelAndView;
    }

    /**
     * GET mapping of the login page, allowing students to log in.
     *
     * @return ModelAndView of the login page, with a student object to enter credentials into.
     */
    @GetMapping(value = "/login")
    public ModelAndView logIn() {
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("login", new Login());
        return modelAndView;
    }

    /**
     * POST mapping of the login page.
     *
     * @param login Created student object to be checked against existing accounts in the database.
     * @return The ModelAndView of a homepage if login is successful, otherwise, the login page with a failure message.
     */
    @PostMapping(value = "/login")
    public ModelAndView checkLogin(Login login, @RequestParam String password) {
        if (loginService.authenticate(login.getEmail(), password)) {
            Login loggedInUser = loginService.getByEmail(login.getEmail());
            Student loggedInStudent = studentService.getStudentByExternalStudentId(loggedInUser.getStudentID());
            studentService.setCurrentUser(loggedInStudent);
            return new ModelAndView("redirect:/main");
        } else {
            return new ModelAndView("login").addObject("error", "Login Failed! Incorrect email or password.");
        }
    }
}
