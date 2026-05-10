package ds.agh.rabbitmq.rabbitmqsystem.carrier;

import ds.agh.rabbitmq.rabbitmqsystem.config.queue.QueueNames;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Profile("carrier")
@Configuration
public class CarrierConfig {

    @Value("${carrier.id}")
    private int carrierId;

    @Value("${carrier.people:false}")
    private boolean people;

    @Value("${carrier.cargo:false}")
    private boolean cargo;

    @Value("${carrier.satellite:false}")
    private boolean satellite;


    @Bean
    Queue fromAdminQueue() {
        return carrierId == 1
                ? new Queue(QueueNames.CARRIER_1)
                : new Queue(QueueNames.CARRIER_2);
    }

    // zamiast dwóch osobnych beanów — lista obsługiwanych kolejek
    @Bean
    List<String> carrierQueueNames() {
        List<String> queues = new ArrayList<>();
        if (people)    queues.add(QueueNames.PEOPLE);
        if (cargo)     queues.add(QueueNames.CARGO);
        if (satellite) queues.add(QueueNames.SATELLITE);
        return queues;
    }
}