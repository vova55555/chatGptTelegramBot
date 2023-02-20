package org.hoshta.handler.impl;

import lombok.AllArgsConstructor;
import org.hoshta.enums.ConversationState;
import org.hoshta.handler.UserRequestHandler;
import org.hoshta.model.UserRequest;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.hoshta.constant.Constants.BUNDLE_NAME;

@Component
@AllArgsConstructor
public class CancelHandler extends UserRequestHandler {

    private final StartCommandHandler startCommandHandler;

    @Override
    public boolean isApplicable(UserRequest userRequest) {
        Update update = userRequest.getUpdate();
        Locale locale = userRequest.getUserSession().getLocale();
        if (locale != null) {
            String backBtn = ResourceBundle.getBundle(BUNDLE_NAME, locale).getString("backBtn");
            return isTextMessage(update) && update.getMessage().getText().equals(backBtn)
                    && ConversationState.WAITING_FOR_TEXT.equals(userRequest.getUserSession().getState());
        }
        return false;
    }

    @Override
    public void handle(UserRequest userRequest) {
        startCommandHandler.handle(userRequest);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
