package uk.ac.leedsbeckett.student.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnrolmentRequest {
    private Long studentId;
    private Long courseId;

    public EnrolmentRequest(Long studentId, Long courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public EnrolmentRequest() {

    }
}
