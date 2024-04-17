package uk.ac.leedsbeckett.student.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface StudentRepository extends JpaRepository<Student,Long> {

    List<Student> findBySurnameContaining(String surname);
    Student findStudentById(Long ID);

    Student findStudentsByExternalStudentId(String studentID);

}
