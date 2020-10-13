package uk.gov.companieshouse.kafka_email;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;
import uk.gov.companieshouse.kafka.serialization.AvroSerializer;
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;

import uk.gov.companieshouse.kafka_email.model.EmailSend;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutionException;


public class EmailKafkaProducerTest {

    private EmailKafkaProducer emailKafkaProducer;

    @Mock
    private SerializerFactory serializerFactory;
    
    @Mock
    private CHKafkaProducer chKafkaProducer;

    @Mock
    private AvroSerializer<EmailSend> mockSerializer;

    private EmailSend testEmail = generateEmailSend();

    @Before
	public void test() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void sendEmail__ok()
            throws SerializationException, ExecutionException, InterruptedException {
        
        byte[] testBytes = new byte[]{ 0x1, 0x2, 0x3 };
        when(mockSerializer.toBinary(testEmail)).thenReturn(testBytes);
        when(serializerFactory.getGenericRecordSerializer(EmailSend.class))
            .thenReturn(mockSerializer);
        final ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        
        emailKafkaProducer = new EmailKafkaProducer(serializerFactory, chKafkaProducer);
        emailKafkaProducer.sendEmail(testEmail);

        verify(serializerFactory).getGenericRecordSerializer(EmailSend.class);
        verify(mockSerializer).toBinary(testEmail);
        verify(chKafkaProducer).send(captor.capture());
        final Message sentEmail = captor.getValue();
        assertEquals(sentEmail.getValue(), testBytes);
        assertEquals(sentEmail.getTopic(), "email-send");
    }

    @Test(expected=SerializationException.class)
    public void sendEmail__serializationError()
            throws SerializationException, ExecutionException, InterruptedException {
        
        when(mockSerializer.toBinary(testEmail)).thenThrow(new SerializationException("TEST ERROR"));
        when(serializerFactory.getGenericRecordSerializer(EmailSend.class))
            .thenReturn(mockSerializer);
        
        emailKafkaProducer = new EmailKafkaProducer(serializerFactory, chKafkaProducer);
        emailKafkaProducer.sendEmail(testEmail);

        verify(serializerFactory).getGenericRecordSerializer(EmailSend.class);
        verify(mockSerializer).toBinary(testEmail);
    }

    private static EmailSend generateEmailSend() {
        EmailSend emailSend = new EmailSend();
        emailSend.setData("TEST-DATA");
        emailSend.setEmailAddress("test@ch.gov.uk");
        emailSend.setAppId("test-app");
        emailSend.setMessageId("12345678");
        emailSend.setMessageType("test-message");
        emailSend.setCreatedAt("2020-10-13");
        return emailSend;
    }
    


    
}
