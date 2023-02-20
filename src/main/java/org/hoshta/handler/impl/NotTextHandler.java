package org.hoshta.handler.impl;

import lombok.AllArgsConstructor;
import org.hoshta.constant.Constants;
import org.hoshta.enums.ConversationState;
import org.hoshta.handler.UserRequestHandler;
import org.hoshta.model.UserRequest;
import org.hoshta.service.TelegramService;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@Component
@AllArgsConstructor
public class NotTextHandler extends UserRequestHandler {
    private final TelegramService telegramService;
    private final LanguageSelectionHandler languageSelectionHandler;
    private final TextEnteredHandler textEnteredHandler;

    @Override
    public boolean isApplicable(UserRequest request) {
        return !isTextMessage(request.getUpdate());
    }

    @Override
    public void handle(UserRequest request) {
        ConversationState state = request.getUserSession().getState();
        Long chatId = request.getChatId();
        ResourceBundle bundle = ResourceBundle.getBundle(Constants.BUNDLE_NAME, request.getUserSession().getLocale());
        if (state.equals(ConversationState.WAITING_FOR_LANGUAGE)) {
            telegramService.sendMessage(chatId, bundle.getString("notTextLanguageError"));
            languageSelectionHandler.handle(request);
        } else {
            telegramService.sendMessage(chatId, bundle.getString("notTextChatError"));
            telegramService.sendMessage(chatId, bundle.getString("conversationTooltip"));
        }
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
