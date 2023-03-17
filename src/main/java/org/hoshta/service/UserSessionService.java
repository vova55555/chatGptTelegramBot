package org.hoshta.service;

import org.hoshta.controllers.UserSessionController;
import org.hoshta.model.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class UserSessionService {

    private final UserSessionController userSessionController;

    @Autowired
    public UserSessionService(UserSessionController userSessionController) {
        this.userSessionController = userSessionController;
    }

    public UserSession getSession(Message message) {
        return userSessionController.getUserSessionOrSaveDefault(message);
    }

    public UserSession saveSession(UserSession session) {
        return userSessionController.saveUserSession(session);
    }
}
