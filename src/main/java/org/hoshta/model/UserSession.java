package org.hoshta.model;

import lombok.Builder;
import lombok.Data;
import org.hoshta.enums.ConversationState;

import java.util.Locale;

@Data
@Builder
public class UserSession {
    private Long chatId;
    private ConversationState state;
    private String text;
    private Locale locale;
}
