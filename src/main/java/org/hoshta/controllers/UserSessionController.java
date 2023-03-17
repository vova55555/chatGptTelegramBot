package org.hoshta.controllers;

import org.hoshta.enums.ConversationState;
import org.hoshta.model.UserSession;
import org.hoshta.repo.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Locale;
import java.util.Objects;

@Controller
public class UserSessionController {
    private final UserSessionRepository userSessionRepository;

    @Autowired
    public UserSessionController(UserSessionRepository userSessionRepository) {
        this.userSessionRepository = userSessionRepository;
    }

    public UserSession getUserSessionOrSaveDefault(Message message) {
        Long chatId = message.getChatId();
        UserSession userSession = userSessionRepository.findByChatId(chatId).orElse(null);
        if (Objects.isNull(userSession)) {
            userSession = UserSession.builder()
                .chatId(chatId)
                .state(ConversationState.STARTED)
                .locale(new Locale(message.getFrom().getLanguageCode()))
                .build();
            saveUserSession(userSession);
        }
        return userSession;
    }

    public UserSession saveUserSession(UserSession userSession) {
        userSessionRepository.save(userSession);
        return userSession;
    }
}
