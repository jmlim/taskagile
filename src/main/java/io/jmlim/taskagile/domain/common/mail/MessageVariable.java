package io.jmlim.taskagile.domain.common.mail;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class MessageVariable {
    private final String key;
    private final Object value;

    public static MessageVariable from(String key, Object value) {
        return new MessageVariable(key, value);
    }

    public int hashCode() {
        return Objects.hash(key, value);
    }
}
