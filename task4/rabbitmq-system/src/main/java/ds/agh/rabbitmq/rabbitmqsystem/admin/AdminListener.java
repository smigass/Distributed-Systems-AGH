package ds.agh.rabbitmq.rabbitmqsystem.admin;

import ds.agh.rabbitmq.rabbitmqsystem.message.AgencyOrder;
import ds.agh.rabbitmq.rabbitmqsystem.message.OrderConfirmation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("admin")
@RabbitListener(queues = "#{@adminQueue.name}")
public class AdminListener {
    private final MessagesStore store;

    public AdminListener(MessagesStore store) {
        this.store = store;
    }

    @RabbitHandler
    public void receiveConfirmation(OrderConfirmation confirmation) {
        store.saveMessage(confirmation.toString());
    }

    @RabbitHandler
    public void receiveOrder(AgencyOrder order) {
        store.saveMessage(order.toString());
    }

    @RabbitHandler(isDefault = true)
    public void receiveMessage(Object message) {
        store.saveMessage(message.toString());
    }
}
