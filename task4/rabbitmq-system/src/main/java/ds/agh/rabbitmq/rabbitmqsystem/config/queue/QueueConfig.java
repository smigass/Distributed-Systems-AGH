package ds.agh.rabbitmq.rabbitmqsystem.config.queue;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {

    // Queues
    @Bean
    Queue agency1Queue() {
        return new Queue(QueueNames.AGENCY_1);
    }

    @Bean
    Queue agency2Queue() {
        return new Queue(QueueNames.AGENCY_2);
    }


    @Bean Queue carrier1Queue() {
        return new Queue(QueueNames.CARRIER_1);
    }

    @Bean Queue carrier2Queue() {
        return new Queue(QueueNames.CARRIER_2);
    }


    @Bean
    Queue adminQueue() {
        return new Queue(QueueNames.ADMIN);
    }

    @Bean
    Queue peopleQueue() {
        return new Queue(QueueNames.PEOPLE);
    }

    @Bean
    Queue cargoQueue() {
        return new Queue(QueueNames.CARGO);
    }

    @Bean
    Queue satelliteQueue() {
        return new Queue(QueueNames.SATELLITE);
    }
}
