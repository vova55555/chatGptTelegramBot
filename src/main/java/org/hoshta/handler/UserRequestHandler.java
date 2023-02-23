package org.hoshta.handler;

import org.hoshta.enums.ConversationState;
import org.hoshta.helper.KeyboardHelper;
import org.hoshta.model.UserRequest;
import org.hoshta.model.UserSession;
import org.hoshta.service.TelegramService;
import org.hoshta.service.UserSessionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.hoshta.constant.Constants.BUNDLE_NAME;

public abstract class UserRequestHandler {

    @Value("${open-ai.token}")
    protected String openAiToken;

    @Value("${open-ai.timeout-in-seconds}")
    protected long timeOut;

    @Value("${open-ai.davinchi-model}")
    protected String daVinchiModel;

    @Value("${open-ai.max-tokens}")
    protected int maxTokens;

    protected final UserSessionService userSessionService;
    protected final KeyboardHelper keyboardHelper;
    protected final TelegramService telegramService;

    protected UserRequestHandler(UserSessionService userSessionService,
                                 TelegramService telegramService, KeyboardHelper keyboardHelper) {
        this.userSessionService = userSessionService;
        this.keyboardHelper = keyboardHelper;
        this.telegramService = telegramService;
    }

    public abstract boolean isApplicable(UserRequest request);

    public abstract void handle(UserRequest dispatchRequest);

    public abstract boolean isGlobal();

    protected boolean isTextMessage(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    protected void setUserSessionStateAndLocale(UserRequest userRequest, Locale selectedLocale, ConversationState conversationState) {
        UserSession userSession = getUserSession(userRequest);
        userSession.setState(conversationState);
        userSession.setLocale(selectedLocale);
        userSessionService.saveSession(userRequest.getChatId(), userSession);
    }

    protected void setUserSessionState(UserRequest userRequest, ConversationState conversationState) {
        UserSession userSession = getUserSession(userRequest);
        userSession.setState(conversationState);
        userSessionService.saveSession(userRequest.getChatId(), userSession);
    }

    protected UserSession getUserSession(UserRequest userRequest) {
        return userRequest.getUserSession();
    }

    protected String getTranslation(UserRequest request, String translationKey) {
        return ResourceBundle.getBundle(BUNDLE_NAME, getUserSession(request).getLocale())
                .getString(translationKey);
    }

    protected void sendErrorMessage(UserRequest request, String translationKey) {
        telegramService.sendMessage(request.getChatId(),
                getTranslation(request, translationKey));
    }

    protected void sendSelectYourPlansMessage(UserRequest request) {
        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardHelper.buildPlanSelectionMenuWithBack(getUserSession(request).getLocale());
        telegramService.sendMessage(request.getChatId(), getTranslation(request, "yourPlans"), replyKeyboardMarkup);
    }

}