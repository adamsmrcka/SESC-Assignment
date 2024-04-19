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

    public RegistrationRequest(String password, String forename, String surname, String email, String type) {
        this.password = password;
        this.forename = forename;
        this.surname = surname;
        this.email = email;
        this.type = type;
    }

    public RegistrationRequest() {

    }
}
