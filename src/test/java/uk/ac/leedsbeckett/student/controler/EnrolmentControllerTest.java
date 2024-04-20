package uk.ac.leedsbeckett.student.controler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.ac.leedsbeckett.student.Request.EnrolmentRequest;
import uk.ac.leedsbeckett.student.controller.EnrolmentController;
import uk.ac.leedsbeckett.student.model.Invoice;
import uk.ac.leedsbeckett.student.service.EnrolmentService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class EnrolmentControllerTest{

    @Mock
    private EnrolmentService enrolmentService;

    @InjectMocks
    private EnrolmentController enrolmentController;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testEnrolStudentJson_Success() {
        // Create a mock EnrolmentRequest
        EnrolmentRequest enrolmentRequest = new EnrolmentRequest(1L, 1L);

        // Create a mock Invoice response
        EnrolmentService enrolmentService = mock(EnrolmentService.class);
        Invoice mockInvoice = new Invoice();
        mockInvoice.setAmount(100.0); // Set invoice amount for verification

        // Mock EnrolmentService behavior
        when(enrolmentService.enrolStudentInCourse(enrolmentRequest)).thenReturn(mockInvoice);

        EnrolmentController enrolmentController = new EnrolmentController(enrolmentService);

        // Call the controller method
        EntityModel<Invoice> response = enrolmentController.enrolStudentJson(enrolmentRequest);

        // Verify the response status and content
        assertEquals(mockInvoice, response.getContent());

        // Verify that enrolStudentInCourse was called with the correct EnrolmentRequest
        verify(enrolmentService, times(1)).enrolStudentInCourse(enrolmentRequest);
    }
}
