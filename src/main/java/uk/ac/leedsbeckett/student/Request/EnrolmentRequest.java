package uk.ac.leedsbeckett.student.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnrolmentRequest {
    private Long studentId;
    private Long courseId;

    /**
     * EnrolmentRequest Constructor with all fields
     * @param studentId Student ID
     * @param courseId Course ID
     */
    public EnrolmentRequest(Long studentId, Long courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    /**
     * Enrolment Constructor with no fields
     */
    public EnrolmentRequest() {

    }
}
