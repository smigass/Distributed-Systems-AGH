package ds.agh.rabbitmq.rabbitmqsystem.agency;

import ds.agh.rabbitmq.rabbitmqsystem.message.AdminMessage;
import ds.agh.rabbitmq.rabbitmqsystem.message.OrderConfirmation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Profile("agency")
@Component
@RabbitListener(queues = "#{@agencyQueue.name}")
public class AgencyListener {
    private final AgencySender sender;
    private final Random random = new Random();

    public AgencyListener(AgencySender sender) {
        this.sender = sender;
    }

    @RabbitHandler
    public void receiveAdminMessage(AdminMessage adminMessage) {
        log.info("Received admin message: {}", adminMessage);

        if (adminMessage.getCommand() == null) {
            log.warn("Received AdminMessage without command (likely wrong payload on this queue): {}", adminMessage);
            return;
        }

        switch (adminMessage.getCommand()) {
            case STOP -> {
                log.info("Stopping agency order listener...");
                sender.setSending(false);
            }
            case RESUME -> {
                log.info("Starting agency order listener...");
                sender.setSending(true);
            }
            case ORDER -> {
                if (adminMessage.getType() != null && sender.isSending()) {
                    sender.sendOrder(random.nextInt(1000), adminMessage.getType(), adminMessage.getQuantity());
                } else {
                    sender.sendOrder();
                }
            }
            case INFO -> log.info("ADMIN BROADCAST: {}", adminMessage.getText());
        }
    }

    @RabbitHandler
    public void receiveConfirmation(OrderConfirmation confirmation) {
        log.info("Received order confirmation: {}", confirmation);
    }

    @RabbitHandler(isDefault = true)
    public void receiveUnknown(Object message) {
        log.warn("Received unknown message on agency queue: {}", message);
    }
}