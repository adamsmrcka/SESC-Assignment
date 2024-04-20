package uk.ac.leedsbeckett.student.controler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.ac.leedsbeckett.student.controller.CourseController;
import uk.ac.leedsbeckett.student.model.Course;
import uk.ac.leedsbeckett.student.service.CourseService;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CourseControllerTest {

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllCoursesJson() {
        // Create mock courses
        Course course1 = new Course("Modul1", "Sesc", "TestCourse 1", 200);
        Course course2 = new Course("Modul2", "TT", "TestCourse 2", 12345);
        List<Course> courses = Arrays.asList(course1, course2);

        // Mock CourseService behavior
        // Create mock EntityModel instances for courses
        EntityModel<Course> entityModel1 = EntityModel.of(course1);
        EntityModel<Course> entityModel2 = EntityModel.of(course2);

        // Create mock CollectionModel response containing EntityModel instances
        CollectionModel<EntityModel<Course>> collectionModel = CollectionModel.of(Arrays.asList(entityModel1, entityModel2));

        // Mock CourseService behavior
        when(courseService.getAllCoursesJson()).thenReturn(collectionModel);

        // Call the controller method
        CollectionModel<EntityModel<Course>> response = courseController.getAllCoursesJson();

        // Verify the response
        assertEquals(2, response.getContent().size());

        // Verify that getAllCoursesJson was called
        verify(courseService, times(1)).getAllCoursesJson();
    }

    @Test
    public void testGetCourseJson() {
        // Create mock course
        Course course = new Course("Modul1", "Sesc", "TestCourse 1", 200);

        // Mock CourseService behavior
        when(courseService.getCourseByIdJson(1L)).thenReturn(EntityModel.of(course));

        // Call the controller method
        EntityModel<Course> response = courseController.getCourseJson(1L);

        // Verify the response
        assertEquals(course.getId(), response.getContent().getId());

        // Verify that getCourseByIdJson was called with the correct ID
        verify(courseService, times(1)).getCourseByIdJson(1L);
    }

    @Test
    public void testGetEnrolledCoursesByStudentId() {
        // Mock student ID
        Long studentId = 123L;

        // Create mock courses
        Course course1 = new Course("Modul1", "Sesc", "TestCourse 1", 200);
        Course course2 = new Course("Modul2", "TT", "TestCourse 2", 12345);
        List<Course> courses = Arrays.asList(course1, course2);

        // Mock CourseService behavior
        // Create mock EntityModel instances for courses
        EntityModel<Course> entityModel1 = EntityModel.of(course1);
        EntityModel<Course> entityModel2 = EntityModel.of(course2);

        // Create mock CollectionModel response containing EntityModel instances
        CollectionModel<EntityModel<Course>> collectionModel = CollectionModel.of(Arrays.asList(entityModel1, entityModel2));

        // Mock CourseService behavior
        when(courseController.getEnrolledCoursesByStudentId(studentId)).thenReturn(collectionModel);

        // Call the controller method
        CollectionModel<EntityModel<Course>> response = courseController.getEnrolledCoursesByStudentId(studentId);

        // Verify the response
        assertEquals(2, response.getContent().size());

        // Verify that getEnrolledCoursesByStudentIdJson was called with the correct student ID
        verify(courseService, times(1)).getEnrolledCoursesByStudentIdJson(studentId);
    }

    @Test
    public void testCreateCourseJson() {
        // Create a mock new course
        Course newCourse = new Course("Modul1", "Sesc", "TestCourse 1", 200);;
        // Mock CourseService behavior
        Course savedCourse = new Course("Modul2", "TT", "TestCourse 2", 12345);
        when(courseService.createNewCourseJson(newCourse)).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(savedCourse)));

        // Call the controller method
        ResponseEntity<EntityModel<Course>> response = courseController.createCourseJson(newCourse);

        // Verify the response status
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Verify that createNewCourseJson was called with the correct new course
        verify(courseService, times(1)).createNewCourseJson(newCourse);
    }
}
