package uk.gov.companieshouse.email_producer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uk.gov.companieshouse.email_producer.factory.EmailFactory;
import uk.gov.companieshouse.email_producer.model.Email;
import uk.gov.companieshouse.email_producer.model.EmailData;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;
import uk.gov.companieshouse.kafka.serialization.AvroSerializer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.core.JsonProcessingException;


public class EmailProducerTest {

    private EmailProducer emailKafkaProducer;

    @Mock private EmailFactory emailFactory;
    @Mock private CHKafkaProducer chKafkaProducer;
    @Mock private AvroSerializer<Email> emailSerializer;
    @Mock private EmailData emailData;

    private Email mockEmail = generateEmail();

    @Before
	public void test() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void sendEmail__ok() throws SerializationException, ExecutionException,
            InterruptedException, JsonProcessingException {
        
        byte[] testBytes = new byte[]{ 0x1, 0x2, 0x3 };
        when(emailFactory.buildEmail(emailData, "test-app", "test-message")).thenReturn(mockEmail);
        when(emailSerializer.toBinary(mockEmail)).thenReturn(testBytes);
        final ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        
        emailKafkaProducer = new EmailProducer(emailFactory, emailSerializer, chKafkaProducer, "test-app");
        emailKafkaProducer.sendEmail(emailData, "test-message");

        verify(emailFactory).buildEmail(emailData, "test-app", "test-message");
        verify(emailSerializer).toBinary(mockEmail);
        verify(chKafkaProducer).send(captor.capture());
        final Message sentEmail = captor.getValue();
        assertEquals(sentEmail.getValue(), testBytes);
        assertEquals(sentEmail.getTopic(), "email-send");
    }

    @Test(expected=SerializationException.class)
    public void sendEmail__serializationError() throws SerializationException,
            ExecutionException, InterruptedException, JsonProcessingException {
        
        when(emailFactory.buildEmail(emailData, "test-app", "test-message")).thenReturn(mockEmail);
        when(emailSerializer.toBinary(mockEmail)).thenThrow(new SerializationException("TEST ERROR"));
        
        emailKafkaProducer = new EmailProducer(emailFactory, emailSerializer, chKafkaProducer, "test-app");
        emailKafkaProducer.sendEmail(emailData, "test-message");

        verify(emailSerializer).toBinary(mockEmail);
    }

    private static Email generateEmail() {
        Email emailSend = new Email();
        emailSend.setData("TEST-DATA");
        emailSend.setEmailAddress("test@ch.gov.uk");
        emailSend.setAppId("test-app");
        emailSend.setMessageId("12345678");
        emailSend.setMessageType("test-message");
        emailSend.setCreatedAt("2020-10-13");
        return emailSend;
    }
    


    
}
