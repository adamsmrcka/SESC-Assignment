package uk.ac.leedsbeckett.student.Request;

import lombok.Getter;
import lombok.Setter;
import uk.ac.leedsbeckett.student.model.Course;
import uk.ac.leedsbeckett.student.model.Student;

@Getter
@Setter
public class EnrolmentRequest {
    private Long studentId;
    private Long courseId;

}
