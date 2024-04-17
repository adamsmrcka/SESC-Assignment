package uk.ac.leedsbeckett.student.Request;

import lombok.Getter;
import lombok.Setter;
import uk.ac.leedsbeckett.student.model.Login;

@Getter
@Setter
public class RegistrationRequest {

    private String email;
    private String password;
    private String forename;
    private String surname;
    private String type;
}
