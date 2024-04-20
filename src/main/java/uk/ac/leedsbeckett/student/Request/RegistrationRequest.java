package uk.ac.leedsbeckett.student.Request;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class RegistrationRequest {

    private String email;
    private String password;
    private String forename;
    private String surname;
    private String type;

    /**
     * RegistrationRequest Constructor with all fields
     * @param password Login Password
     * @param forename Student Forename
     * @param surname Student Surname
     * @param email Login Email
     * @param type Login User Type
     */
    public RegistrationRequest(String password, String forename, String surname, String email, String type) {
        this.password = password;
        this.forename = forename;
        this.surname = surname;
        this.email = email;
        this.type = type;
    }

    /**
     * RegistrationRequest Constructor with no fields
     */
    public RegistrationRequest() {

    }
}
