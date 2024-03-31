package uk.ac.leedsbeckett.student.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.ac.leedsbeckett.student.model.Account;
import uk.ac.leedsbeckett.student.model.Invoice;

@Component
public class IntegrationService {
    private final RestTemplate restTemplate;

    public IntegrationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Account getStudentAccount(String studentId) {
        return restTemplate.getForObject("http://localhost:8081/accounts/student/" + studentId, Account.class);
    }

    public ResponseEntity<Account> createFinanceAccount(Account account) {
        return restTemplate.postForEntity("http://localhost:8081/accounts", account, Account.class);
    }

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

    public ResponseEntity<Invoice> createCourseFeeInvoice(Invoice invoice) {
        return restTemplate.postForEntity("http://localhost:8081/invoices", invoice, Invoice.class);
    }
}
