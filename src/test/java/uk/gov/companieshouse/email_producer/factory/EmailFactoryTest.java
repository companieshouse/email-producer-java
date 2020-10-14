package uk.gov.companieshouse.email_producer.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uk.gov.companieshouse.email_producer.model.Email;
import uk.gov.companieshouse.email_producer.model.EmailData;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailFactoryTest {

    private EmailFactory emailFactory;

    @Mock
    private ObjectMapper objectMapper;

    @Before
	public void test() {
        MockitoAnnotations.initMocks(this);
        this.emailFactory = new EmailFactory(objectMapper);
	}

    @Test
    public void buildEmail__ok() throws JsonProcessingException {
        EmailData testEmailData = generateEmailData();
        when(objectMapper.writeValueAsString(testEmailData)).thenReturn("TEST DATA");

        Email result = emailFactory.buildEmail(testEmailData, "test-app", "test-message");

        verify(objectMapper, times(1)).writeValueAsString(testEmailData);
        assertEquals(result.getData(), "TEST DATA");
        assertEquals(result.getEmailAddress(), "test@ch.gov.uk");
        assertEquals(result.getAppId(), "test-app");
        assertEquals(result.getMessageType(), "test-message");
    }

    @Test(expected=JsonProcessingException.class)
    public void buildEmail__mappingError() throws JsonProcessingException {
        EmailData testEmailData = generateEmailData();
        when(objectMapper.writeValueAsString(testEmailData)).thenThrow(new JsonProcessingException("Test"){});

        emailFactory.buildEmail(testEmailData, "test-app", "test-message");
    }

    private static EmailData generateEmailData() {
        EmailData emailData = new EmailData();
        emailData.setTo("test@ch.gov.uk");
        emailData.setSubject("Regarding: Test");
        return emailData;
    }
}
