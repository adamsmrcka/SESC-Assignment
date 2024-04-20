package uk.ac.leedsbeckett.student.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.ac.leedsbeckett.student.model.Account;
import uk.ac.leedsbeckett.student.model.Invoice;

/**
 * Service component for integrating with external systems, such as accounts and invoices
 */
@Component
public class IntegrationService {
    private final RestTemplate restTemplate;

    /**
     * Constructor for IntegrationService
     * @param restTemplate RestTemplate used for making HTTP requests
     */
    public IntegrationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Retrieves the account details for a student from an external service
     * @param studentId The ID of the student whose account details are to be retrieved
     * @return The Account object representing the student's account
     */
    public Account getStudentAccount(String studentId) {
        return restTemplate.getForObject("http://localhost:8081/accounts/student/" + studentId, Account.class);
    }

    /**
     * Creates a finance account for the provided account details via an external service
     * @param account The Account object containing details for creating the finance account
     * @return ResponseEntity containing the created Account object
     */
    public ResponseEntity<Account> createFinanceAccount(Account account) {
        return restTemplate.postForEntity("http://localhost:8081/accounts", account, Account.class);
    }

    /**
     * Creates a books account for a student identified by the student ID via an external service
     * @param studentId The ID of the student for whom the books account is to be created
     * @return ResponseEntity indicating the status of the books account creation
     */
    public ResponseEntity<Void> createBooksAccount(String studentId) {
        // Prepare headers for JSON content
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        // Prepare JSON data with the student ID
        String jsonBody = "{\"studentId\": \"" + studentId + "\"}";

        // Create a request entity with JSON data and headers
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

        // Send POST request to register the student account
        ResponseEntity<Void> responseEntity = restTemplate.postForEntity(
                "http://localhost:80/api/register", requestEntity, Void.class);

        // Return response
        return responseEntity;
    }

    /**
     * Creates an invoice for a course fee via an external service
     * @param invoice The Invoice object containing details for creating the invoice
     * @return ResponseEntity containing the created Invoice object
     */
    public ResponseEntity<Invoice> createCourseFeeInvoice(Invoice invoice) {
        return restTemplate.postForEntity("http://localhost:8081/invoices", invoice, Invoice.class);
    }
}
