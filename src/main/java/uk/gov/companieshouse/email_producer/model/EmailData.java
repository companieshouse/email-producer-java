package uk.gov.companieshouse.email_producer.model;

public class EmailData {

    private String to;
    private String subject;

    public String getTo() { return to; }

    public String getSubject() { return subject; }

    public void setTo(String to) {
        this.to = to;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
