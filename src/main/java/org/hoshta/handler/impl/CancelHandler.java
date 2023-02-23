package org.hoshta.handler.impl;

import org.hoshta.enums.ConversationState;
import org.hoshta.handler.UserRequestHandler;
import org.hoshta.helper.KeyboardHelper;
import org.hoshta.model.UserRequest;
import org.hoshta.service.TelegramService;
import org.hoshta.service.UserSessionService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;

import static org.hoshta.enums.ConversationState.*;

@Component
public class CancelHandler extends UserRequestHandler {

    private final StartCommandHandler startCommandHandler;
    private final PlansHandler plansHandler;

    public CancelHandler(UserSessionService userSessionService, TelegramService telegramService,
                         KeyboardHelper keyboardHelper, StartCommandHandler startCommandHandler, PlansHandler plansHandler) {
        super(userSessionService, telegramService, keyboardHelper);
        this.startCommandHandler = startCommandHandler;
        this.plansHandler = plansHandler;
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        if (Objects.isNull(getLocale(request))) {
            return false;
        }
        Update update = request.getUpdate();
        if (!isTextMessage(update)) {
            return false;
        }
        if (!Objects.equals(update.getMessage().getText(), getTranslation(request, "backBtn"))) {
            return false;
        }
        ConversationState actualState = getState(request);
        if (Objects.isNull(actualState)) {
            return false;
        }
        return Objects.equals(actualState, WAITING_FOR_QUESTION) || Objects.equals(actualState, WAITING_FOR_PLANS)
                || Objects.equals(actualState, WAITING_FOR_IMAGE_DESCRIPTION);
    }

    @Override
    public void handle(UserRequest request) {
        ConversationState actualState = getState(request);
        if (Objects.equals(actualState, WAITING_FOR_PLANS)) {
            startCommandHandler.handle(request);
        } else if (Objects.equals(actualState, WAITING_FOR_QUESTION)
                || Objects.equals(actualState, WAITING_FOR_IMAGE_DESCRIPTION)) {
            plansHandler.handle(request);
        }
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
