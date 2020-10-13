package uk.gov.companieshouse.kafka_email;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.kafka.producer.ProducerConfig;
import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;
import uk.gov.companieshouse.kafka.serialization.AvroSerializer;
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;
import uk.gov.companieshouse.kafka_email.model.EmailData;
import uk.gov.companieshouse.kafka_email.model.Email;

@Service
public class EmailProducer {

    private final EmailFactory emailFactory;
    private final AvroSerializer<Email> serializer;
    private final CHKafkaProducer chKafkaProducer;
    private static final String EMAIL_SEND_TOPIC = "email-send";

    public EmailProducer(final ProducerConfig producerConfig) {
        this(
            new EmailFactory(new ObjectMapper()),
            new SerializerFactory(),
            new CHKafkaProducer(producerConfig));
    }

    protected EmailProducer(final EmailFactory emailFactory, final SerializerFactory serializerFactory,
            final CHKafkaProducer chKafkaProducer) {
        this.emailFactory = emailFactory;
        this.serializer = serializerFactory.getGenericRecordSerializer(Email.class);
        this.chKafkaProducer = chKafkaProducer;
    }

    /**
     * Sends an email-send message to the Kafka producer.
     * @param email EmailData object
     * @throws JsonProcessingException should a failure to 
     * @throws SerializationException should there be a failure to serialize the EmailSend object
     * @throws ExecutionException should something unexpected happen
     * @throws InterruptedException should something unexpected happen
     */
    public void sendEmail(final EmailData emailData, String appId, String messageType)
            throws JsonProcessingException, SerializationException, ExecutionException, InterruptedException {
        
        Email email = emailFactory.buildEmail(emailData, appId, messageType);
        
        final Message message = new Message();
        message.setValue(serializer.toBinary(email));
        message.setTopic(EMAIL_SEND_TOPIC);
        message.setTimestamp(new Date().getTime());

        chKafkaProducer.send(message);
    }
}
