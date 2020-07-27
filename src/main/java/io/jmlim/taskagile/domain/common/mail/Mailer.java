package io.jmlim.taskagile.domain.common.mail;

public interface Mailer {

    /**
     * 보내는 메세지
     *
     * @param message
     */
    void send(Message message);
}
