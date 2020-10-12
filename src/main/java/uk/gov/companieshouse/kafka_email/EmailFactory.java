package uk.gov.companieshouse.kafka_email;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.UUID;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;

@Service
public class EmailFactory {

    private final ObjectMapper objectMapper;

    public EmailFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public EmailSend buildEmail(EmailData emailData, String appId, String messageType) throws JsonProcessingException {
        final EmailSend email = new EmailSend();

        email.setData(objectMapper.writeValueAsString(emailData));
        email.setEmailAddress(emailData.getTo());
        email.setAppId(appId);
        email.setMessageId(UUID.randomUUID().toString());
        email.setMessageType(messageType);
        email.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC).toString());

        return email;
    }
}
