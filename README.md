# CH Kafka Email

Library for handling the production and sending of emails via Apache Kafka and chs-email-sender.

This extends the CH Kafka library to allow the consistent creation and sending of emails via Kafka.

## Requirements

In order to run the service locally you will need the following:
- [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven](https://maven.apache.org/download.cgi)
- [Git](https://git-scm.com/downloads)

## Getting Started

The library is built using maven:
```
mvn clean install
```

The library can be imported as a maven dependency:
```
<dependency>
    <groupId>uk.gov.companieshouse</groupId>
    <artifactId>ch-kafka-email</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

### Configuration

The following configuration variables are required to be set, whether via Environment Variables or your project's `application.yml` file:

Environment Variable Key    | application.yml Key       | Example                                                     
--------------------------- | ------------------------- | -------------
KAFKA_BROKER_ADDR           | kafka.broker.addr         | kafka:9092
KAFKA_CONFIG_ACKS           | kafka.config.acks         | WAIT_FOR_ALL
KAFKA_CONFIG_RETRIES        | kafka.config.retries      | false
KAFKA_CONFIG_IS_ROUND_ROBIN | kafka.config.isRoundRobin | 10

### Building An Email

The `EmailData` object defines the minimum required data to send an email - `to` (the intended recipient's email address), `subject` and `cdnHost`. To add application data to an email, a new class should be defined which extends `EmailData` and includes attributes for storing the data. For example, from [`Suppression Api`](https://github.com/companieshouse/suppression-api):

```java
import uk.gov.companieshouse.kafka_email.EmailData;

public class ApplicationReceivedEmailData extends EmailData {

    private Suppression suppression;
    ...

}
```

Once this data object has been defined, it can be used to construct an `EmailSend` object like so:
```java
import uk.gov.companieshouse.kafka_email.EmailFactory;
import uk.gov.companieshouse.kafka_email.EmailSend;

...

private final EmailFactory emailFactory;

...

final EmailSend email = emailFactory.buildEmail(
                emailData, EMAIL_APP_ID, messageType);
```
where:
* `emailData` (_EmailData_) is an instance of the data object defined in the previous step
* `EMAIL_APP_ID` (_String_) is the service application's name (e.g. `suppression-api`); and
* `messageType` (_String_) references an email template in the CH Notification API's template registry.

> **Note:** The call to this method will need to handle `JsonProcessingException` and `SerializationException`.

### Sending An Email

Providing the previous steps have been followed, sending the email to Kafka is very simple:
```java
import uk.gov.companieshouse.kafka_email.EmailKafkaProducer;

...

private final EmailKafkaProducer emailKafkaProducer;

...

emailKafkaProducer.sendEmail(email);
```

> **Note:** The call to this method will need to handle `ExecutionException` and `InterruptedException`.

## Spring Applications

`EmailFactory` and `EmailKafkaProducer` have been specified as Spring Services. In Spring applications, these classes do not require explicit instantiation. See the `EmailService` class in [`Suppression Api`](https://github.com/companieshouse/suppression-api) for a full usage example.
