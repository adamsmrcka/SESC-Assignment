package uk.ac.leedsbeckett.student.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.leedsbeckett.student.controller.StudentController;
import uk.ac.leedsbeckett.student.model.Student;
import uk.ac.leedsbeckett.student.model.StudentRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Service class responsible for managing student-related operations
 */
@Component
public class StudentService {
    private Student student;
    private final StudentRepository studentRepository;

    /**
     * Constructs a new StudentService with the specified StudentRepository
     * @param studentRepository The repository for student data access
     */
    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Retrieves a student by ID and creates an EntityModel representing the student with self-link
     * @param id The ID of the student to retrieve
     * @return EntityModel representing the retrieved student
     * @throws RuntimeException if the student with the specified ID is not found
     */
    public EntityModel<Student> getStudentByIdJson(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course with id " + id + "not found."));
        return EntityModel.of(student,
                linkTo(methodOn(StudentController.class)
                        .getStudentJson(student.getId())).withSelfRel());
    }

    /**
     * Retrieves the current authenticated user (student)
     * @return The current authenticated user (student)
     */
    @Transactional
    public Student getCurrentUser() {
        return this.student;
    }

    /**
     * Sets the current authenticated user (student)
     * @param student The student to set as the current authenticated user
     * @throws IllegalArgumentException if the specified student is not found in the database
     */
    @Transactional
    public void setCurrentUser(Student student) {
        if (student == null) {
            this.student = null; // Reset the current user to null
        } else {
            // Retrieve the student from the database by ID
            this.student = studentRepository.findById(student.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + student.getId()));
        }
    }
    /**
     * Updates a student's information and returns the updated EntityModel with self-link
     * @param updateStudent The updated student information
     * @return ResponseEntity containing the updated EntityModel of the student
     */
    public ResponseEntity<EntityModel<Student>> updateStudentJson(Student updateStudent) {
        updateStudent.setCoursesEnrolledIn(getCurrentUser().getCoursesEnrolledIn());
        Student updatedStudent = studentRepository.save(updateStudent);
        EntityModel<Student> entityModel = EntityModel.of(updatedStudent,
                linkTo(methodOn(StudentController.class).getStudentJson(updatedStudent.getId())).withSelfRel());
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }
}
