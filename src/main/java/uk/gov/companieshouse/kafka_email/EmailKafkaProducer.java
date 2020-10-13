package uk.gov.companieshouse.kafka_email;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.kafka.producer.ProducerConfig;
import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;
import uk.gov.companieshouse.kafka.serialization.AvroSerializer;
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;
import uk.gov.companieshouse.kafka_email.model.EmailSend;

@Service
public class EmailKafkaProducer {

    private final SerializerFactory serializerFactory;
    private final CHKafkaProducer chKafkaProducer;
    private static final String EMAIL_SEND_TOPIC = "email-send";

    public EmailKafkaProducer(
        final ProducerConfig producerConfig
    ) {
        this.serializerFactory = new SerializerFactory();
        this.chKafkaProducer = new CHKafkaProducer(producerConfig);
    }

    /**
     * Sends an email-send message to the Kafka producer.
     * @param email EmailSend object
     * @throws SerializationException should there be a failure to serialize the EmailSend object
     * @throws ExecutionException should something unexpected happen
     * @throws InterruptedException should something unexpected happen
     */
    public void sendEmail(final EmailSend email)
            throws SerializationException, ExecutionException, InterruptedException {
        final Message message = this.createMessage(email);
        chKafkaProducer.send(message);
    }

    private Message createMessage(final EmailSend emailSend) throws SerializationException {
		final AvroSerializer<EmailSend> serializer =
				serializerFactory.getGenericRecordSerializer(EmailSend.class);
		final Message message = new Message();
		message.setValue(serializer.toBinary(emailSend));
		message.setTopic(EMAIL_SEND_TOPIC);
		message.setTimestamp(new Date().getTime());
		return message;
    }

}
