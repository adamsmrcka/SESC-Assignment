package uk.ac.leedsbeckett.student.uniTests.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import uk.ac.leedsbeckett.student.controller.CourseController;
import uk.ac.leedsbeckett.student.model.Course;
import uk.ac.leedsbeckett.student.model.CourseRepository;
import uk.ac.leedsbeckett.student.model.StudentRepository;
import uk.ac.leedsbeckett.student.service.CourseService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the CourseService class.
 */
class CourseServiceTest {
    @Mock
    private CourseRepository courseRepository;

    private StudentRepository studentRepository;
    @InjectMocks
    private CourseService courseService;
    @Mock
    private CourseController courseController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * Test case for retrieving all courses in JSON format.
     */
    @Test
    void getAllCoursesJson() {
        // Mocking a Course object with ID 1
        Course mockCourse = new Course("Computer Science", "CS", "Advanced topics in CS", 1000.0);

        // Mock the behavior of courseRepository.findById(id)
        when(courseRepository.findById(1L)).thenReturn(Optional.of(mockCourse));

        // Call the method under test
        Course result = courseService.getCourseById(1L);

        // Verify that the course returned matches the mocked course
        assertNotNull(result);
        assertEquals(mockCourse.getId(), result.getId());
        assertEquals(mockCourse.getTitle(), result.getTitle());
        assertEquals(mockCourse.getAbbreviation(), result.getAbbreviation());
        assertEquals(mockCourse.getDescription(), result.getDescription());
        assertEquals(mockCourse.getFee(), result.getFee());
    }
}