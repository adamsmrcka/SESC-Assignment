package uk.ac.leedsbeckett.student.model;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository class managing Course-related operations
 */
public interface CourseRepository extends JpaRepository<Course, Long> {


    /**
     * Finds courses that are less than fee
     * @param fee Course fee
     * @return List of Courses
     */
    List<Course> findByFeeLessThan(double fee);

    /**
     * Finds courses by ID
     * @param id Course ID
     * @return Course
     */
    Course findCourseById(Long id);
}