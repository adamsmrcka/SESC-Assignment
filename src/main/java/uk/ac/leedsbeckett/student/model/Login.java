package uk.ac.leedsbeckett.student.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Entity
@Data
public class Login {

    /**
     * Account Type
     */
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

    /**
     * Login constructor without id
     * @param email User email
     * @param studentId User unique ID
     * @param password User Password
     * @param type User account type
     */
    public Login(String email, String studentId, String password, UserType type) {
        this.email = email;
        this.studentID = studentId;
        setPassword(password);
        this.type = type;
    }

    /**
     * Login constructor with no fields
     */
    public Login() {

    }

    /**
    * Hash the password before setting it
    */
    public void setPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }

    /**
     * Tries to set a User type from String
     * @param type Login user type
     */
    public void setType(String type) {
        try {
            this.type = UserType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.type = UserType.USER; // Default to USER if type is not recognized
        }
    }

    /**
     * Checks if the provided password matches the hashed password
     * @param password unhashed password
     * @return boolean matches
     */
    public boolean checkPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(password, this.password);
    }
}