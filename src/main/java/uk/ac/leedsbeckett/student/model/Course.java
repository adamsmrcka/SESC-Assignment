package uk.ac.leedsbeckett.student.model;


import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;

/**
 * Represents a Course with basic information.
 */
@Entity
@Table(name = "COURSE_TBL")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String abbreviation;
    private String description;
    private double fee;

    @ManyToMany(mappedBy = "coursesEnrolledIn")
    @JsonIgnore
    @ToString.Exclude
    Set<Student> studentsEnrolledInCourse;


    /**
     * Course Constructor with all fields
     * @param title Title of the course
     * @param abbreviation Abbreviation of the course
     * @param description Course Description
     * @param fee Course Fee in £
     */
    public Course(String title, String abbreviation, String description, double fee) {
        this.title = title;
        this.abbreviation = abbreviation;
        this.description = description;
        this.fee = fee;
        this.studentsEnrolledInCourse = new HashSet<>();
    }
}
