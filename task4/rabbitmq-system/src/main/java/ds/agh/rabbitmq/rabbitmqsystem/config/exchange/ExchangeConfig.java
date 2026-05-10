package ds.agh.rabbitmq.rabbitmqsystem.config.exchange;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExchangeConfig {
    // Exchanges

    // ** Admin **
    @Bean
    TopicExchange monitorExchange() {
        return new TopicExchange("admin.monitor");
    }

    @Bean
    FanoutExchange adminAllExchange() {
        return new FanoutExchange("admin.all");
    }

    @Bean
    FanoutExchange adminAgencyExchange() {
        return new FanoutExchange("admin.agency");
    }

    @Bean
    FanoutExchange adminCarrierExchange() {
        return new FanoutExchange("admin.carrier");
    }

    // ** Orders **

    @Bean
    TopicExchange ordersExchange() {
        return new TopicExchange("orders");
    }

    // ** Confirmation **

    @Bean
    TopicExchange confirmExchange() {
        return new TopicExchange("confirm");
    }

}
