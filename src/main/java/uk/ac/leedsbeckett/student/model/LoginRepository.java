package uk.ac.leedsbeckett.student.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<Login, Long> {

    Optional<Login> findByEmail(String email);

    Login findByStudentID(String studentID);

    boolean existsByStudentID(String studentID);
}
