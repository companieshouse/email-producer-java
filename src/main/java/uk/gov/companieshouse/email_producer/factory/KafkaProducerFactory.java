package uk.gov.companieshouse.email_producer.factory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.kafka.producer.Acks;
import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;
import uk.gov.companieshouse.kafka.producer.ProducerConfig;

/**
 * Configuration class for the kafka queue used to send emails
 */
@Configuration
public class KafkaProducerFactory {

    @Value("${kafka.broker.addr}")
    private String brokerAddr;

    @Value("${kafka.config.acks}")
    private String acks;

    @Value("${kafka.config.retries}")
    private int retries;

    @Value("${kafka.config.is.round.robin}")
    private boolean isRoundRobin;

    @Bean
    CHKafkaProducer kafkaProducer() {
        ProducerConfig config = new ProducerConfig();
        config.setBrokerAddresses(brokerAddr.split(","));
        config.setAcks(Acks.valueOf(acks));
        config.setRoundRobinPartitioner(isRoundRobin);
        config.setRetries(retries);

        return new CHKafkaProducer(config);
    }
}
