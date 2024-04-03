package us.redsols.todo.model;

public class Email {

    private String to;
    private String subject;
    private String html;

    public Email(String to, String subject, String html) {
        this.to = to;
        this.subject = subject;
        this.html = html;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
