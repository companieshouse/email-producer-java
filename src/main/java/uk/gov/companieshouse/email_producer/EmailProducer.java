package uk.gov.companieshouse.email_producer;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.email_producer.factory.EmailFactory;
import uk.gov.companieshouse.email_producer.model.Email;
import uk.gov.companieshouse.email_producer.model.EmailData;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;
import uk.gov.companieshouse.kafka.serialization.AvroSerializer;

@Service
public class EmailProducer {

    @Value("${email.producer.appId}")
    private String appId;

    private final EmailFactory emailFactory;
    private final AvroSerializer<Email> serializer;
    private final CHKafkaProducer chKafkaProducer;
    private static final String EMAIL_SEND_TOPIC = "email-send";

    public EmailProducer(final EmailFactory emailFactory, final AvroSerializer<Email> serializer,
            final CHKafkaProducer chKafkaProducer) {
        this.emailFactory = emailFactory;
        this.serializer = serializer;
        this.chKafkaProducer = chKafkaProducer;
        System.out.println(String.format("App ID: %s", appId));
    }

    /**
     * Sends an email-send message to the Kafka producer.
     * @param email EmailData object
     * @param messageType desired notification api template
     * @throws JsonProcessingException should a failure to build the email occur
     * @throws SerializationException should there be a failure to serialize the EmailSend object
     * @throws ExecutionException should something unexpected happen
     * @throws InterruptedException should something unexpected happen
     */
    public void sendEmail(final EmailData emailData, String messageType)
            throws JsonProcessingException, SerializationException, ExecutionException, InterruptedException {
        
        Email email = emailFactory.buildEmail(emailData, appId, messageType);
        
        final Message message = new Message();
        message.setValue(serializer.toBinary(email));
        message.setTopic(EMAIL_SEND_TOPIC);
        message.setTimestamp(new Date().getTime());

        chKafkaProducer.send(message);
    }
}
