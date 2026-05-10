package ds.agh.rabbitmq.rabbitmqsystem.agency;

import ds.agh.rabbitmq.rabbitmqsystem.message.AgencyOrder;
import ds.agh.rabbitmq.rabbitmqsystem.message.GoodType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Slf4j
@EnableScheduling
@Profile("agency")
public class AgencySender {
    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange ordersExchange;
    private final Random random = new Random();
    @Getter @Setter private boolean isSending = true;

    @Value("${agency.id}")
    private int agencyId;

    public AgencySender(RabbitTemplate rabbitTemplate, TopicExchange ordersExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.ordersExchange = ordersExchange;
    }

    @Scheduled(fixedDelay = 15000)
    public void sendOrder() {
        if (!isSending) return;
        GoodType type = GoodType.values()[random.nextInt(GoodType.values().length)];
        sendOrder(random.nextInt(1000), type, random.nextDouble(100000));
    }

    public void sendOrder(int id, GoodType type, double quantity) {
        AgencyOrder order = new AgencyOrder(id, "Agency" + agencyId, agencyId, type, quantity);
        rabbitTemplate.convertAndSend(ordersExchange.getName(), "order." + type.getName(), order);
        log.info("Sent order: {}", order);
    }

}
