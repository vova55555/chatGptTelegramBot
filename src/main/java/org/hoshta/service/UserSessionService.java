package org.hoshta.service;

import org.hoshta.controllers.UserSessionController;
import org.hoshta.model.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserSessionService {

    private final Map<Long, UserSession> userSessionMap = new HashMap<>();
    private final UserSessionController userSessionController;

    @Autowired
    public UserSessionService(UserSessionController userSessionController) {
        this.userSessionController = userSessionController;
    }

    public UserSession getSession(Message message) {
        return userSessionController.getUserSessionOrSaveDefault(message);
//        return userSessionMap.getOrDefault(chatId, UserSession
//                .builder()
//                .chatId(chatId)
//                .build());
    }

    public UserSession saveSession(Long chatId, UserSession session) {
        return userSessionController.saveUserSession(session);
//        return userSessionMap.put(chatId, session);
    }
}
