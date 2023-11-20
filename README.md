# Email Producer (Java)

Email Producer is a library for handling the sending of emails via Kafka, the [CHS Email Sender](https://github.com/companieshouse/chs-email-sender) and the [CHS Notification API](https://github.com/companieshouse/chs-notification-api).

The aim of this package is to standardise the email-sending flow, abstracting away all of the serialisation and Kafka message building logic so that future engineers looking to add emails to their service are free to focus on application-specific things, such as the data being sent to the Notification API and the templates they’ll use to render the emails. The result is the ability to send an email with a single new Maven dependency, a few configuration variables and one method call.

Things To consider
--

+ Always make sure any code changes you make get copied across to `main-8` (compatible with Java 8) also to `main` (compatible with Java 21)

###### Changes Specific to Java 8

+ Please raise a PR to merge your changes only to [main-8](https://github.com/companieshouse/email-producer-java/tree/main-8) branch
+ Use Java 8 Major tags generated from pipeline in your references (example : tags 1.x.x for java 8)

###### Changes Specific to Java 21

+ Please merge your changes only to [main](https://github.com/companieshouse/email-producer-java) branch
+ Use Java 21 Major tags generated from pipeline in your references (example : tags 3.x.x for java 21)

###### Pipeline

+ Please use this [Pipeline](https://ci-platform.companieshouse.gov.uk/teams/team-development/pipelines/email-producer-java) and make sure respective `source-code-main-21` or `source-code-main-8` task gets started once the PR is created or after the PR is merged to `main` or `main-8` and once the pipeline tasks are complete then use the created tags respectively.

## Requirements

In order to run the service locally you will need the following:
- [Java](http://www.oracle.com/technetwork/java/javase/downloads)
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

### Prerequisites

Before your service will be able to send an email, you'll need to define a HTML and TXT template, and add it to the [`assets/email-templates`](https://github.com/companieshouse/chs-notification-api/tree/develop/assets/email-templates) directory of the [CHS Notification API](https://github.com/companieshouse/chs-notification-api). Once the templates are written, register them with the Notification API by adding an entry to [`assets/template_registry.yaml`](https://github.com/companieshouse/chs-notification-api/blob/develop/assets/template_registry.yaml). For example:

```yaml
'suppression_application_received':
  'html-template': "suppression_application_received"
  'sent-by-app-ids': [ 'suppression-api' ]
```

where:

* The **key** is the *type* of email you'd like to send. You'll use this key later when calling the Email Producer.
* `html-template` refers to the *filename* of the new email template, minus the file extension.
* `sent-by-app-ids` refers to the *list of service applications* that will use this email type. You'll refer to this `appId` value when configuring the Email Producer in the next step.



### Configuration

The following configuration variables are required to be set in your project's `application.yml` file:

 Key                       | Example         
 ------------------------- | --------------- 
 kafka.broker.addr         | kafka:9092      
 kafka.config.acks         | WAIT_FOR_ALL    
 kafka.config.retries      | false           
 kafka.config.isRoundRobin | 10              
 email.producer.appId      | suppression-api 



### Building An Email

The `EmailData` object defines the minimum required data to send an email - `to` (the intended recipient's email address) and `subject`. To add application data to an email, a new class should be defined which extends `EmailData` and includes attributes for storing the data. For example, from the [Suppression API](https://github.com/companieshouse/suppression-api):

```java
import uk.gov.companieshouse.email_producer_java.EmailData;

public class ApplicationReceivedEmailData extends EmailData {

    private Suppression suppression;
    ...

}
```



### Sending An Email

Once you've created your `EmailData` object, sending the email to Kafka is very simple:
```java
import uk.gov.companieshouse.email_producer_java.EmailProducer;

...

private final EmailProducer emailProducer;

...

emailProducer.sendEmail(emailData, messageType);
```
where:
* `emailData` *(`EmailData`)* is an instance of the data object defined in the previous step
* `messageType` *(`String`)* references the email type in the CH Notification API's template registry.



### Complete Usage Example

For a full usage example, take a look at the [`ApplicationReceivedEmailData`](https://github.com/companieshouse/suppression-api/tree/master/src/main/java/uk/gov/companieshouse/model/email/ApplicationReceivedEmailData.java) and [`EmailService`](https://github.com/companieshouse/suppression-api/blob/master/src/main/java/uk/gov/companieshouse/service/EmailService.java) classes in the [Suppression API](https://github.com/companieshouse/suppression-api) respository.