package ds.agh.rabbitmq.rabbitmqsystem.admin;

import ds.agh.rabbitmq.rabbitmqsystem.message.AdminMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("admin")
public class AdminSender {
    private final RabbitTemplate rabbitTemplate;

    public AdminSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendAdminMessage(AdminMessage message, String exchangeName) {
        rabbitTemplate.convertAndSend(exchangeName, "", message);
        log.info("Admin message sent: {} via exchange {}", message, exchangeName);
    }
}
