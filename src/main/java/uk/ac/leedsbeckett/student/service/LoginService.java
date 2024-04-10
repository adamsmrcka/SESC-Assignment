package uk.ac.leedsbeckett.student.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.leedsbeckett.student.model.*;

import java.util.Optional;
import java.util.Random;

@Component
public class LoginService {

    private Student student;
    private final LoginRepository loginRepository;
    private final StudentRepository studentRepository;
    private final IntegrationService integrationService;

    public LoginService(LoginRepository loginRepository, StudentRepository studentRepository, IntegrationService integrationService) {
        this.loginRepository = loginRepository;
        this.studentRepository = studentRepository;
        this.integrationService = integrationService;
    }

    @Transactional
    public boolean existsByEmail(String email) {
        return loginRepository.existsByEmail(email);
    }

    @Transactional
    public boolean authenticate(String email, String password) {
        Login user = getByEmail(email);
        if (user != null) {
            return user.checkPassword(password);
        }
        return false;
    }

    @Transactional
    public Login getByEmail(String email) {
        Optional<Login> optionalLogin = loginRepository.findByEmail(email);
        return optionalLogin.orElse(null);
    }

    @Transactional
    public String generateUniqueStudentID() {
        Random random = new Random();
        String studentID;
        do {
            int candidateID = random.nextInt(1000000) + 3000000; // Generate candidate ID
            studentID = "c" + candidateID;
        } while (loginRepository.existsByStudentID(studentID)); // Check if the ID already exists
        return studentID;
    }

    @Transactional
    public String registerUser(String password, String forename, String surname, String email) {
        // Generate a unique studentID
        String studentID = generateUniqueStudentID();

        // Create a new Student entity and associate it with the login
        Student student1 = new Student();
        student1.setForename(forename);
        student1.setSurname(surname);
        student1.setExternalStudentId(studentID);

        // Create an account for the student
        Account account = new Account();
        account.setStudentId(studentID);
        account.setHasOutstandingBalance(false);

        // Create and save the Login entity
        Login login1 = new Login();
        login1.setEmail(email);
        login1.setStudentID(studentID);
        login1.setPassword(password);

        try {
            // Save student, login, and create the finance account
            integrationService.createFinanceAccount(account);
            integrationService.createBooksAccount(studentID);
            studentRepository.save(student1);
            loginRepository.save(login1);
            // If all registrations are successful, return the studentID
            return studentID;
        } catch (Exception e) {
            // Rollback transaction if any registration fails
            throw new RuntimeException("Failed to register user", e);
        }
    }


}
