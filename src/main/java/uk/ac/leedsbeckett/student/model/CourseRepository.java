package uk.ac.leedsbeckett.student.model;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByFeeLessThan(double fee);

    Course findCourseById(Long ID);
}