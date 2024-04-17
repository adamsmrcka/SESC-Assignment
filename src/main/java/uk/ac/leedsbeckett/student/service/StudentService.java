package uk.ac.leedsbeckett.student.service;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.leedsbeckett.student.controller.CourseController;
import uk.ac.leedsbeckett.student.controller.StudentController;
import uk.ac.leedsbeckett.student.model.Course;
import uk.ac.leedsbeckett.student.model.Login;
import uk.ac.leedsbeckett.student.model.Student;
import uk.ac.leedsbeckett.student.model.StudentRepository;

import javax.swing.text.html.parser.Entity;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class StudentService {
    private Student student;
    private final StudentRepository studentRepository;
    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public EntityModel<Student> getStudentByIdJson(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course with id " + id + "not found."));
        return EntityModel.of(student,
                linkTo(methodOn(StudentController.class)
                        .getStudentJson(student.getId())).withSelfRel());
    }

    @Transactional
    public Student getCurrentUser() {
        return this.student;
    }

    @Transactional
    public void setCurrentUser(Student stud) {
        this.student = studentRepository.findStudentById(stud.getId());
    }

    @Transactional
    public Student getStudentByExternalStudentId(String studentID) {
        return studentRepository.findStudentsByExternalStudentId(studentID);
    }

    public ResponseEntity<EntityModel<Student>> updateStudentJson(Student updateStudent){
        updateStudent.setCoursesEnrolledIn(getCurrentUser().getCoursesEnrolledIn());
        Student updatedStudent = studentRepository.save(updateStudent);
        EntityModel<Student> entityModel = EntityModel.of(updatedStudent,
                linkTo(methodOn(StudentController.class).getStudentJson(updatedStudent.getId())).withSelfRel());
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }
}
