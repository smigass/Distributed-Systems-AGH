package ds.agh.rabbitmq.rabbitmqsystem.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderConfirmation {
    private int orderId;
    private String agencyName;
    private int carrierId;
}
