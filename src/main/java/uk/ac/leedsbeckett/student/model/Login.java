package uk.ac.leedsbeckett.student.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import uk.ac.leedsbeckett.student.service.LoginService;

import java.util.HashSet;
import java.util.Random;

@Entity
@Data
public class Login {

    public enum UserType {
        ADMIN,
        USER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String studentID;
    private String password;
    private UserType type;

    public Login(String email, String studentId, String password, UserType type) {
        this.email = email;
        this.studentID = studentId;
        setPassword(password);
        this.type = type;
    }

    public Login() {

    }

    public void setDefStudentID(String studentID) {
        this.studentID = studentID;
    }


    // Hash the password before setting it
    public void setPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }
    public void setType(String type) {
        try {
            this.type = UserType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Handle invalid user types here (e.g., default to a specific type)
            this.type = UserType.USER; // Default to USER if type is not recognized
        }
    }

    // Add a method to check if the provided password matches the hashed password
    public boolean checkPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(password, this.password);
    }
}