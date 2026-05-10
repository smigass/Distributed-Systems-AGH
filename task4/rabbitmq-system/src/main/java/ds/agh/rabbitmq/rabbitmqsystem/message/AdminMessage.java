package ds.agh.rabbitmq.rabbitmqsystem.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdminMessage {
    private Command command;
    private GoodType type;
    private double quantity;
    private String text;

    public AdminMessage(Command command, GoodType type, double quantity) {
        this.command = command;
        this.type = type;
        this.quantity = quantity;
    }

    public AdminMessage(Command command, String text) {
        this.command = command;
        this.text = text;
    }
}
