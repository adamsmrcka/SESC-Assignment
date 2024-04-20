package uk.ac.leedsbeckett.student.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * Represents an Invoice with basic information.
 */
@Data
public class Invoice {
    private Long id;
    private String reference;
    private Double amount;
    private LocalDate dueDate;
    private Type type;
    private Status status;
    private Account account;

    /**
     * Invoice fee type
     */
    public enum Type {
        LIBRARY_FINE,
        TUITION_FEES
    }

    /**
     * Invoice Status
     */
    enum Status {
        OUTSTANDING,
        PAID,
        CANCELLED
    }
}
