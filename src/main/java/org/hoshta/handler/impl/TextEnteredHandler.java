package org.hoshta.handler.impl;

import org.hoshta.handler.UserRequestHandler;
import org.hoshta.helper.KeyboardHelper;
import org.hoshta.model.UserRequest;
import org.hoshta.service.OpenAiCustomService;
import org.hoshta.service.TelegramService;
import org.hoshta.service.UserSessionService;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static org.hoshta.enums.ConversationState.WAITING_FOR_QUESTION;

@Component
public class TextEnteredHandler extends UserRequestHandler {
    private final OpenAiCustomService openAiCustomService;

    public TextEnteredHandler(UserSessionService userSessionService,
                              TelegramService telegramService, KeyboardHelper keyboardHelper,
                              OpenAiCustomService openAiCustomService) {
        super(userSessionService, telegramService, openAiCustomService, keyboardHelper);
        this.openAiCustomService = openAiCustomService;
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        return isTextMessage(request.getUpdate()) && Objects.equals(getState(request), WAITING_FOR_QUESTION);
    }

    @Override
    public void handle(UserRequest request) {
        Long chatId = request.getChatId();
        try {
            String answer = openAiCustomService.completionRequestOpenAi(request);
            telegramService.sendMessage(chatId, answer,
                    keyboardHelper.buildMenuWithBackBtnOnly(getLocale(request)));
        } catch (Exception e) {
            e.printStackTrace();
            telegramService.sendMessage(chatId, e.getMessage());
            telegramService.sendMessage(chatId,getTranslation(request, "errorMessageChat"));
        }
    }

    @Override
    public boolean isGlobal() {
        return false;
    }

}
