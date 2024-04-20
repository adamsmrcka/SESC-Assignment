package uk.ac.leedsbeckett.student.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository class managing Login-related operations
 */
public interface LoginRepository extends JpaRepository<Login, Long> {
    /**
     * Finds a Login user by email
     * @param email Login email
     * @return Login
     */
    Optional<Login> findByEmail(String email);

    /**
     * Finds a Login user by Student ID
     * @param studentID Student ID
     * @return Login
     */
    Login findByStudentID(String studentID);

    /**
     * Finds if Login user exists by Student ID
     * @param studentID Student ID
     * @return Boolean
     */
    boolean existsByStudentID(String studentID);
}
