package ds.agh.rabbitmq.rabbitmqsystem.carrier;

import ds.agh.rabbitmq.rabbitmqsystem.message.AdminMessage;
import ds.agh.rabbitmq.rabbitmqsystem.message.AgencyOrder;
import ds.agh.rabbitmq.rabbitmqsystem.message.OrderConfirmation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Profile("carrier")
@Component
public class CarrierListener {
    @Value("${carrier.id}")
    private int carrierId;

    private final RabbitTemplate rabbitTemplate;
    private final RabbitListenerEndpointRegistry registry;

    public CarrierListener(RabbitTemplate rabbitTemplate, RabbitListenerEndpointRegistry registry) {
        this.rabbitTemplate = rabbitTemplate;
        this.registry = registry;
    }

    @RabbitListener(queues = {"#{@carrierQueueNames}"}, id = "carrierOrderListener")
    public void receiveAndConfirmOrder(AgencyOrder order) {
        log.info("Carrier {} received order: {}", carrierId, order);
        OrderConfirmation confirmation = new OrderConfirmation(order.getOrderId(), "Carrier" + carrierId, carrierId);
        rabbitTemplate.convertAndSend("confirm", "confirm." + order.getAgencyName().toLowerCase(), confirmation);
    }

    @RabbitListener(queues = "#{fromAdminQueue.name}")
    public void receiveAdminMessage(AdminMessage adminMessage) {
        log.info("Received admin message: {}", adminMessage);
        switch (adminMessage.getCommand()) {
            case STOP -> {
                log.info("Stopping carrier order listener...");
                registry.getListenerContainer("carrierOrderListener").stop();
            }
            case RESUME -> {
                log.info("Starting carrier order listener...");
                registry.getListenerContainer("carrierOrderListener").start();
            }
            case INFO -> log.info("ADMIN BROADCAST: {}", adminMessage.getText());
            case ORDER -> log.warn("Received ORDER command, but I am a carrier. Ignoring.");
        }
    }
}