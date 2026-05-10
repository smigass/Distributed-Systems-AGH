package ds.agh.rabbitmq.rabbitmqsystem.agency;

import ds.agh.rabbitmq.rabbitmqsystem.config.queue.QueueNames;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("agency")
@Configuration
public class AgencyConfig {
    @Value("${agency.id}")
    private int agencyId;


    @Bean
    Queue agencyQueue() {
        if (agencyId == 1) {
            return new Queue(QueueNames.AGENCY_1);
        } else {
            return new Queue(QueueNames.AGENCY_2);
        }
    }
}
