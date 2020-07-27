package io.jmlim.taskagile.domain.common.mail;


import lombok.Builder;

public class SimpleMessage implements Message {
    private final String to;
    private final String subject;
    private final String body;
    private final String from;

    @Builder
    public SimpleMessage(String to, String subject, String body, String from) {
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.from = from;
    }

    @Override
    public String getTo() {
        return to;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public String getFrom() {
        return from;
    }
}
