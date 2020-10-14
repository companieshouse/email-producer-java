# Email Producer (Java)

Library for handling the sending of emails via Kafka and chs-email-sender.


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
    <artifactId>email-producer-java</artifactId>
    <version>unversioned</version>
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
import uk.gov.companieshouse.email_producer_java.EmailData;

public class ApplicationReceivedEmailData extends EmailData {

    private Suppression suppression;
    ...

}
```

### Sending An Email

Once you've created your EmailData object, sending the email to Kafka is very simple:
```java
import uk.gov.companieshouse.email_producer_java.EmailProducer;

...

private final EmailProducer emailProducer;

...

emailProducer.sendEmail(emailData, EMAIL_APP_ID, messageType);
```
where:
* `emailData` (_EmailData_) is an instance of the data object defined in the previous step
* `EMAIL_APP_ID` (_String_) is the service application's name (e.g. `suppression-api`); and
* `messageType` (_String_) references an email template in the CH Notification API's template registry.
