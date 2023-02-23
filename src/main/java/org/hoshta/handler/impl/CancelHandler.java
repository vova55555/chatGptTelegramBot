package org.hoshta.handler.impl;

import org.hoshta.enums.ConversationState;
import org.hoshta.handler.UserRequestHandler;
import org.hoshta.helper.KeyboardHelper;
import org.hoshta.model.UserRequest;
import org.hoshta.model.UserSession;
import org.hoshta.service.TelegramService;
import org.hoshta.service.UserSessionService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.hoshta.enums.ConversationState.*;

@Component
public class CancelHandler extends UserRequestHandler {

    private final StartCommandHandler startCommandHandler;
    private final LanguageSelectionHandler languageSelectionHandler;
    private final PlansHandler plansHandler;

    public CancelHandler(UserSessionService userSessionService, TelegramService telegramService,
                         KeyboardHelper keyboardHelper, StartCommandHandler startCommandHandler,
                         LanguageSelectionHandler languageSelectionHandler, PlansHandler plansHandler) {
        super(userSessionService, telegramService, keyboardHelper);
        this.startCommandHandler = startCommandHandler;
        this.languageSelectionHandler = languageSelectionHandler;
        this.plansHandler = plansHandler;
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        if (request.getUserSession().getLocale() == null) {
            return false;
        }
        Update update = request.getUpdate();
        if (!isTextMessage(update)) {
            return false;
        }
        if (!update.getMessage().getText().equals(getTranslation(request, "backBtn"))) {
            return false;
        }
        ConversationState actualState = getUserSession(request).getState();
        if (actualState == null) {
            return false;
        }
        return actualState.equals(WAITING_FOR_QUESTION) || actualState.equals(WAITING_FOR_PLANS)
                || actualState.equals(WAITING_FOR_IMAGE_DESCRIPTION);
    }

    @Override
    public void handle(UserRequest request) {
        ConversationState actualState = getUserSession(request).getState();
        if (actualState.equals(WAITING_FOR_PLANS)) {
            startCommandHandler.handle(request);
        } else if (actualState.equals(WAITING_FOR_QUESTION) || actualState.equals(WAITING_FOR_IMAGE_DESCRIPTION)) {
            plansHandler.handle(request);
        }
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
