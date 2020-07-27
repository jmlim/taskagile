package io.jmlim.taskagile.domain.common.mail;

public interface Message {
    /**
     * 메시지를 받는 사람 가져오기
     *
     * @return
     */
    String getTo();

    /**
     * 메세지 제목
     *
     * @return
     */
    String getSubject();

    /**
     * 메세지 내용
     *
     * @return
     */
    String getBody();

    /**
     * 보내는 사람
     *
     * @return
     */
    String getFrom();
}
