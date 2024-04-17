package uk.ac.leedsbeckett.student.service;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.ac.leedsbeckett.student.Request.RegistrationRequest;
import uk.ac.leedsbeckett.student.model.*;

import java.net.URI;
import java.util.Optional;
import java.util.Random;

@Component
public class LoginService {

    private Student student;
    private final LoginRepository loginRepository;
    private final StudentRepository studentRepository;
    private final IntegrationService integrationService;
    private final StudentService studentService;

    public LoginService(StudentService studentService, LoginRepository loginRepository, StudentRepository studentRepository, IntegrationService integrationService) {
        this.loginRepository = loginRepository;
        this.studentRepository = studentRepository;
        this.integrationService = integrationService;
        this.studentService = studentService;
    }

    @Transactional
    public String authenticate(String email, String password) {
        Login user = getByEmail(email);
        if (user != null) {
            if(user.checkPassword(password)){
                return user.getStudentID();
            }
        }
        return null;
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
    public ResponseEntity<EntityModel<Student>> CreateNewStudentJson(RegistrationRequest request){
        String studentId = registerUser(request.getPassword(), request.getForename(), request.getSurname(), request.getEmail(), request.getType());
        Student student = studentRepository.findStudentsByExternalStudentId(studentId);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(student.getId())
                .toUri();

        EntityModel<Student> entityModel = EntityModel.of(student);
        entityModel.add(Link.of(uri.toString(), IanaLinkRelations.SELF));

        return ResponseEntity
                .created(uri)
                .body(entityModel);
    }

    @Transactional
    public ResponseEntity<EntityModel<Student>> loginUserJson(RegistrationRequest request){
        String studentId = authenticate(request.getEmail(), request.getPassword());
        Student student = studentRepository.findStudentsByExternalStudentId(studentId);
        studentService.setCurrentUser(student);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(student.getId())
                .toUri();

        EntityModel<Student> entityModel = EntityModel.of(student);
        entityModel.add(Link.of(uri.toString(), IanaLinkRelations.SELF));

        return ResponseEntity
                .created(uri)
                .body(entityModel);
    }

    @Transactional
    public String registerUser(String password, String forename, String surname, String email, String type) {
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
        login1.setType(type);

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


    @Transactional
    public boolean emailExists(String email) {
        Login user = getByEmail(email);
        return user != null;
    }
}
