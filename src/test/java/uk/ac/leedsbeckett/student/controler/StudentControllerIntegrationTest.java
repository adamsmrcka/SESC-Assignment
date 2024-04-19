package uk.ac.leedsbeckett.student.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import uk.ac.leedsbeckett.student.controller.StudentController;
import uk.ac.leedsbeckett.student.model.Account;
import uk.ac.leedsbeckett.student.model.Student;
import uk.ac.leedsbeckett.student.service.IntegrationService;
import uk.ac.leedsbeckett.student.service.StudentService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(StudentController.class)
@AutoConfigureMockMvc
public class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private IntegrationService integrationService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetStudentById() throws Exception {

        Student student = new Student(1L, "c123545", "John", "Doe");
        EntityModel<Student> studentModel = EntityModel.of(student);

        when(studentService.getStudentByIdJson(anyLong())).thenReturn(studentModel);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/students/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals("{\"id\":1,\"externalStudentId\":\"c123545\",\"surname\":\"Doe\",\"forename\":\"John\",\"coursesEnrolledIn\":null}", response);

    }

    @Test
    public void testUpdateStudent() throws Exception {
        Student updatedStudent = new Student(1L, "c123545", "John", "Doe");
        String jsonRequest = objectMapper.writeValueAsString(updatedStudent);

        when(studentService.updateStudentJson(any())).thenReturn(ResponseEntity.ok(EntityModel.of(updatedStudent)));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/student/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals("{\"id\":1,\"externalStudentId\":\"c123545\",\"surname\":\"Doe\",\"forename\":\"John\",\"coursesEnrolledIn\":null}", response);

    }

    @Test
    public void testGetGraduateEligibility() throws Exception {
        String studentId = "123";

        Account account = new Account();
        account.setHasOutstandingBalance(false); // Eligible

        when(integrationService.getStudentAccount(anyString())).thenReturn(account);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/student/graduate/" + studentId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals("Eligible", response);
    }
}
