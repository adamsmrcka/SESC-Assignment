package uk.ac.leedsbeckett.student.model;

import lombok.Data;

/**
 * Represents a student account with basic information.
 */
@Data
public class Account {
    private Long id;
    private String studentId;
    private boolean hasOutstandingBalance;
}
