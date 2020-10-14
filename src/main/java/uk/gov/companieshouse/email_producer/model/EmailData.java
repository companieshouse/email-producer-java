package uk.gov.companieshouse.email_producer.model;

public class EmailData {

    private String to;
    private String subject;
    private String cdnHost;

    public String getTo() { return to; }

    public String getSubject() { return subject; }

    public String getCdnHost() { return cdnHost; }

    public void setTo(String to) {
        this.to = to;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setCdnHost(String cdnHost) {
        this.cdnHost = cdnHost;
    }
}
