package uk.ac.leedsbeckett.student.integration;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.leedsbeckett.student.model.Course;
import uk.ac.leedsbeckett.student.model.Student;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Integration tests.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class IntegrationTest {

    @Autowired
    private MockMvc mvc;

    /**
     * Test retrieving all courses via JSON API.
     *
     * @throws Exception if there is an error performing the request or assertions fail
     */
    @Test
    public void testGetAllCoursesJson() throws Exception {
        // Perform a GET request to /api/courses
        mvc.perform(get("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON))
                // Expect HTTP status 200 OK
                .andExpect(status().isOk())
                // Expect content type to be JSON
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                // Expect JSON path for self link
                .andExpect(jsonPath("$._links.self.href").exists())
                // Verify JSON path for courseList array
                .andExpect(jsonPath("$._embedded.courseList").isArray())
                // Verify the presence and value of courseId in the first course
                .andExpect(jsonPath("$._embedded.courseList[0].id").exists())
                .andExpect(jsonPath("$._embedded.courseList[0].id").value(1)) // Assuming the first course has id=1
                .andExpect(jsonPath("$._embedded.courseList[0].title").value("Introduction to Programming"))
                .andExpect(jsonPath("$._embedded.courseList[0].abbreviation").value("CS101"))
                .andExpect(jsonPath("$._embedded.courseList[0].description").value("Learn basic programming concepts and logic."))
                .andExpect(jsonPath("$._embedded.courseList[0].fee").value(1000.0));
    }

    /**
     * Test retrieving a specific course via JSON API.
     *
     * @throws Exception if there is an error performing the request or assertions fail
     */
    @Test
    public void testGetCourseJson() throws Exception {
        Long courseId = 1L;
        // Mock a Course object with ID = 1
        Course mockCourse = new Course("Introduction to Programming", "CS101", "Learn basic programming concepts and logic.", 1000.0);
        // Perform a GET request to /api/courses/{id}
        mvc.perform(get("/api/courses/{id}", courseId)
                        .contentType(MediaType.APPLICATION_JSON)) // Set the request content type
                // Expect HTTP status 200 OK
                .andExpect(status().isOk())
                // Expect content type to be application/hal+json
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
            // Verify JSON path for self link
            .andExpect(jsonPath("$._links.self.href").exists())
                // Verify JSON path for course data
                .andExpect(jsonPath("$.id").value(courseId))
                .andExpect(jsonPath("$.title").value(mockCourse.getTitle()))
                .andExpect(jsonPath("$.abbreviation").value(mockCourse.getAbbreviation()))
                .andExpect(jsonPath("$.description").value(mockCourse.getDescription()))
                .andExpect(jsonPath("$.fee").value(mockCourse.getFee()));
    }

    /**
     * Test retrieving enrolled courses for a student via JSON API.
     *
     * @throws Exception if there is an error performing the request or assertions fail
     */
    @Test
    public void testGetEnrolledCoursesByStudentId() throws Exception {
        // Mock student ID to retrieve enrolled courses
        String studentId = "c3922382";
        long id = 1;
        // Mock student and enrolled courses
        Student mockStudent = new Student(1L, studentId, "Adam", "Smrcka");
        List <Course> enrolledCourses = Arrays.asList(
                new Course(1L, "Introduction to Programming", "CS101", "Learn basic programming concepts and logic.", 1000.0));

        // Perform a GET request to /api/v1/courses/student/{id}
        mvc.perform(get("/api/courses/student/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)) // Set the request content type
                // Expect HTTP status 200 OK
                .andExpect(status().isOk())
                // Expect content type to be application/hal+json
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
        // Verify JSON path for self link
            .andExpect(jsonPath("$._links.self.href").exists())
                // Verify JSON path for course list
                .andExpect(jsonPath("$._embedded.courseList").isArray())
                .andExpect(jsonPath("$._embedded.courseList.length()").value(enrolledCourses.size()))
                // Verify JSON path for first course in the list
                .andExpect(jsonPath("$._embedded.courseList[0].id").value(enrolledCourses.get(0).getId()))
                .andExpect(jsonPath("$._embedded.courseList[0].title").value(enrolledCourses.get(0).getTitle()))
                .andExpect(jsonPath("$._embedded.courseList[0].abbreviation").value(enrolledCourses.get(0).getAbbreviation()))
                .andExpect(jsonPath("$._embedded.courseList[0].description").value(enrolledCourses.get(0).getDescription()))
                .andExpect(jsonPath("$._embedded.courseList[0].fee").value(enrolledCourses.get(0).getFee()));
    }

    /**
     * Test creating a new course via JSON API.
     *
     * @throws Exception if there is an error performing the request or assertions fail
     */
    @Test
    public void testCreateCourseJson() throws Exception {
        String newCourseJson = "{\"title\": \"Test Course\", \"abbreviation\": \"TEST101\", \"description\": \"Test description\", \"fee\": 100}";

        mvc.perform(post("/api/courses/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCourseJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$.id").exists());
    }

    /**
     * Test deleting a course via JSON API.
     *
     * @throws Exception if there is an error performing the request or assertions fail
     */
    @Test
    public void testDeleteCourseJson() throws Exception {
        Long courseIdToDelete = 1L;

        mvc.perform(post("/api/courses/delete/{id}", courseIdToDelete))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string("Course deleted: Introduction to Programming"));

    }
}