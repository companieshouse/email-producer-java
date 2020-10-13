package uk.gov.companieshouse.kafka_email;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.UUID;
import java.time.ZoneOffset;

import uk.gov.companieshouse.kafka_email.model.EmailData;
import uk.gov.companieshouse.kafka_email.model.Email;

public class EmailFactory {

    private final ObjectMapper objectMapper;

    protected EmailFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    protected Email buildEmail(EmailData emailData, String appId, String messageType) throws JsonProcessingException {
        final Email email = new Email();

        email.setData(objectMapper.writeValueAsString(emailData));
        email.setEmailAddress(emailData.getTo());
        email.setAppId(appId);
        email.setMessageId(UUID.randomUUID().toString());
        email.setMessageType(messageType);
        email.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC).toString());

        return email;
    }
}
