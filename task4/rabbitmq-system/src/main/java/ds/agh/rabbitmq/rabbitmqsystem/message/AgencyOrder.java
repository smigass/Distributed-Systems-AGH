package ds.agh.rabbitmq.rabbitmqsystem.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AgencyOrder implements Serializable {
    private int orderId;
    private String agencyName;
    private int agencyId;
    private GoodType goodType;
    private double quantity;
}
