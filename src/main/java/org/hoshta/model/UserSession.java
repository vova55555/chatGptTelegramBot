package org.hoshta.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hoshta.enums.ConversationState;

import javax.persistence.*;
import java.util.Locale;
@Entity
@Builder
@AllArgsConstructor
@Data
@Table(name = "chatGptUserSession")
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long chatId;
    @Enumerated(EnumType.STRING)
    private ConversationState state;
    private Locale locale;
    private String text;

    public UserSession() {

    }
}
