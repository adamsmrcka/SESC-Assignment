package uk.ac.leedsbeckett.student.model;


import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Data
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(unique = true)
    private String externalStudentId;
    private String surname;
    private String forename;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "STUDENT_COURSE_TABLE",
            joinColumns = {
                    @JoinColumn(name = "student_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "course_id")
            }
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Set<Course> coursesEnrolledIn;

    /**
     * Student constructor with no fields
     */
    public Student() {
    }

    /**
     * Student Constructor with all fields
     * @param id ID
     * @param externalStudentId Student ID
     * @param forename Forename
     * @param surname Surname
     */
    public Student(long id, String externalStudentId, String forename, String surname) {
        this.id = id;
        this.externalStudentId = externalStudentId;
        this.forename = forename;
        this.surname = surname;
    }

    /**
     * Student Constructor without an ID
     * @param externalStudentId Student ID
     * @param forename Forename
     * @param surname Surname
     */
    public Student(String externalStudentId, String forename, String surname) {
        this.externalStudentId = externalStudentId;
        this.forename = forename;
        this.surname = surname;
    }

    /**
     * Enrols student into a course
     * @param course Course
     */
    @Transactional
    public void enrolInCourse(Course course) {
        if (coursesEnrolledIn == null) {
            coursesEnrolledIn = new HashSet<>();
        }

        // Check if the course is already enrolled
        if (!coursesEnrolledIn.contains(course)) {
            coursesEnrolledIn.add(course);
            course.getStudentsEnrolledInCourse().add(this);
        }
    }
}