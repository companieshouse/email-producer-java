package uk.gov.companieshouse.email_producer.factory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.gov.companieshouse.email_producer.model.Email;
import uk.gov.companieshouse.kafka.serialization.AvroSerializer;
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;

@Configuration
public class EmailSerializerFactory {
    
    @Bean
    AvroSerializer<Email> serializerFactory() {
        SerializerFactory serializerFactory = new SerializerFactory();
        return serializerFactory.getGenericRecordSerializer(Email.class);
    }
}
