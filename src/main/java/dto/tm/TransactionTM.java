package dto.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionTM {
    private String transactionId;
    private String bookId;
    private String custId;
    private String issueDate;
    private String returnDate;
    private double fine;
}
