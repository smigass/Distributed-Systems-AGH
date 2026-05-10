package ds.agh.rabbitmq.rabbitmqsystem.config.binding;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BindingConfig {
    // ** E2E **
    @Bean Binding adminAllToAgencies(FanoutExchange adminAllExchange, FanoutExchange adminAgencyExchange) {
        return new Binding(adminAgencyExchange.getName(), Binding.DestinationType.EXCHANGE, adminAllExchange.getName(), "", null);
    }

    @Bean Binding ordersToMonitor(TopicExchange monitorExchange, TopicExchange ordersExchange) {
        return new Binding(monitorExchange.getName(), Binding.DestinationType.EXCHANGE, ordersExchange.getName(), "#", null);
    }

    @Bean Binding confirmToMonitor(TopicExchange monitorExchange, TopicExchange confirmExchange) {
        return new Binding(monitorExchange.getName(), Binding.DestinationType.EXCHANGE, confirmExchange.getName(), "#", null);
    }

    // ** Admin **
    @Bean Binding adminAllToCarriers(FanoutExchange adminAllExchange, FanoutExchange adminCarrierExchange) {
        return new Binding(adminCarrierExchange.getName(), Binding.DestinationType.EXCHANGE, adminAllExchange.getName(), "", null);
    }

    // * Agencies *
    @Bean Binding monitorExchangeToAdmin(Queue adminQueue, TopicExchange monitorExchange) {
        return BindingBuilder.bind(adminQueue).to(monitorExchange).with("#");
    }

    @Bean Binding agencyExchangeToAgency1(Queue agency1Queue, FanoutExchange adminAgencyExchange) {
        return BindingBuilder.bind(agency1Queue).to(adminAgencyExchange);
    }

    @Bean Binding agencyExchangeToAgency2(Queue agency2Queue, FanoutExchange adminAgencyExchange) {
        return BindingBuilder.bind(agency2Queue).to(adminAgencyExchange);
    }

    // * Carriers *

    @Bean Binding carrierExchangeToCarrier1(Queue carrier1Queue, FanoutExchange adminCarrierExchange) {
        return BindingBuilder.bind(carrier1Queue).to(adminCarrierExchange);
    }

    @Bean Binding carrierExchangeToCarrier2(Queue carrier2Queue, FanoutExchange adminCarrierExchange) {
        return BindingBuilder.bind(carrier2Queue).to(adminCarrierExchange);
    }

    // ** Orders **
    @Bean Binding orderExchangeToPeopleQueue(Queue peopleQueue, TopicExchange ordersExchange) {
        return BindingBuilder.bind(peopleQueue).to(ordersExchange).with("order.people");
    }

    @Bean Binding orderExchangeToCargoQueue(Queue cargoQueue, TopicExchange ordersExchange) {
        return BindingBuilder.bind(cargoQueue).to(ordersExchange).with("order.cargo");
    }

    @Bean Binding orderExchangeToSatelliteQueue(Queue satelliteQueue, TopicExchange ordersExchange) {
        return BindingBuilder.bind(satelliteQueue).to(ordersExchange).with("order.satellite");
    }

    // ** Confirmations **
    @Bean Binding confirmExchangeToAgency1(Queue agency1Queue, TopicExchange confirmExchange) {
        return BindingBuilder.bind(agency1Queue).to(confirmExchange).with("confirm.agency1");
    }

    @Bean Binding confirmExchangeToAgency2(Queue agency2Queue, TopicExchange confirmExchange) {
        return BindingBuilder.bind(agency2Queue).to(confirmExchange).with("confirm.agency2");
    }
}
