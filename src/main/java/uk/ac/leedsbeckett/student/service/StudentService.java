package uk.ac.leedsbeckett.student.service;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import uk.ac.leedsbeckett.student.controller.StudentController;
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

    public Student getCurrentUser() {
        return this.student;
    }

    public void setCurrentUser(Student stud) {
        this.student = studentRepository.findStudentById(stud.getId());
    }

    public Student getStudentByExternalStudentId(String studentID) {
        return studentRepository.findStudentsByExternalStudentId(studentID);
    }
}
