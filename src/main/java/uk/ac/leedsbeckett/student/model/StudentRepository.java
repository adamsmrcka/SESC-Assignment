package uk.ac.leedsbeckett.student.model;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

/**
 * Repository class managing Student-related operations
 */
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Finds students by surname
     * @param surname Student surname
     * @return List of Students
     */
    List<Student> findBySurnameContaining(String surname);

    /**
     * Finds a Student by ID
     * @param ID ID
     * @return Student
     */
    Student findStudentById(Long ID);

    /**
     * Finds a Student by External Student ID
     * @param studentID Student ID
     * @return Student
     */
    Student findStudentsByExternalStudentId(String studentID);

}
