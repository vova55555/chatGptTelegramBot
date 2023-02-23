package org.hoshta.handler.impl;

import org.hoshta.handler.UserRequestHandler;
import org.hoshta.helper.KeyboardHelper;
import org.hoshta.model.UserRequest;
import org.hoshta.service.TelegramService;
import org.hoshta.service.UserSessionService;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static org.hoshta.constant.Constants.IMAGE_UNICODE;
import static org.hoshta.constant.Constants.PEN_UNICODE;
import static org.hoshta.enums.ConversationState.*;

@Component
public class PlansHandler extends UserRequestHandler {

    public PlansHandler(UserSessionService userSessionService, TelegramService telegramService, KeyboardHelper keyboardHelper) {
        super(userSessionService, telegramService, keyboardHelper);
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        return isTextMessage(request.getUpdate())
                && Objects.equals(getState(request), WAITING_FOR_PLANS);
    }

    @Override
    public void handle(UserRequest request) {
        String messageText = request.getUpdate().getMessage().getText();
        String expectedImageBtnText = IMAGE_UNICODE + getTranslation(request, "imgGeneratedBtn");
        String expectedAnswerBtnText = PEN_UNICODE + getTranslation(request, "textAnswerBtn");
        String expectedBackBtnText = getTranslation(request, "backBtn");
        if (Objects.equals(messageText, expectedAnswerBtnText)) {
            sendEnterYourQuestionMessages(request);
        } else if (Objects.equals(messageText, expectedImageBtnText)) {
            sendEnterImageDescriptionMessage(request);
        } else if (Objects.equals(messageText, expectedBackBtnText)) {
            setUserSessionStateAndLocale(request, getLocale(request), WAITING_FOR_PLANS);
            sendSelectYourPlansMessage(request);
        }
        else {
            sendErrorMessage(request, "errorMessagePlanSelection");
        }
    }

    @Override
    public boolean isGlobal() {
        return false;
    }

    private void sendEnterImageDescriptionMessage(UserRequest request) {
        telegramService.sendMessage(request.getChatId(), getTranslation(request, "imageDescriptionTooltip"),
                keyboardHelper.buildMenuWithBackBtnOnly(getLocale(request)));
        setUserSessionState(request, WAITING_FOR_IMAGE_DESCRIPTION);
    }

    private void sendEnterYourQuestionMessages(UserRequest request) {
        Long chatId = request.getChatId();
        telegramService.sendMessage(chatId, getTranslation(request, "greeting"));
        telegramService.sendMessage(chatId, getTranslation(request, "conversationTooltip"),
                keyboardHelper.buildMenuWithBackBtnOnly(getLocale(request)));
        setUserSessionState(request, WAITING_FOR_QUESTION);
    }
}
