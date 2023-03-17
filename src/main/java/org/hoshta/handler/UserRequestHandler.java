package org.hoshta.handler;

import org.hoshta.enums.ConversationState;
import org.hoshta.helper.KeyboardHelper;
import org.hoshta.model.UserRequest;
import org.hoshta.model.UserSession;
import org.hoshta.service.OpenAiCustomService;
import org.hoshta.service.TelegramService;
import org.hoshta.service.UserSessionService;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.hoshta.constant.Constants.BUNDLE_NAME;

public abstract class UserRequestHandler {

    protected final UserSessionService userSessionService;
    protected final KeyboardHelper keyboardHelper;
    protected final TelegramService telegramService;
    protected final OpenAiCustomService openAiCustomService;

    protected UserRequestHandler(UserSessionService userSessionService, TelegramService telegramService,
                                 OpenAiCustomService openAiCustomService,KeyboardHelper keyboardHelper) {
        this.userSessionService = userSessionService;
        this.keyboardHelper = keyboardHelper;
        this.openAiCustomService = openAiCustomService;
        this.telegramService = telegramService;
    }

    public UserRequestHandler(UserSessionService userSessionService, TelegramService telegramService, KeyboardHelper keyboardHelper) {
        this.userSessionService = userSessionService;
        this.keyboardHelper = keyboardHelper;
        this.openAiCustomService = null;
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
        userSessionService.saveSession(userSession);
    }

    protected void setUserSessionState(UserRequest userRequest, ConversationState conversationState) {
        UserSession userSession = getUserSession(userRequest);
        userSession.setState(conversationState);
        userSessionService.saveSession(userSession);
    }

    protected static UserSession getUserSession(UserRequest userRequest) {
        return userRequest.getUserSession();
    }

    protected ConversationState getState(UserRequest userRequest) {
        return getUserSession(userRequest).getState();
    }

    protected static Locale getLocale(UserRequest userRequest) {
        return getUserSession(userRequest).getLocale();
    }

    public static String getTranslation(UserRequest request, String translationKey) {
        return ResourceBundle.getBundle(BUNDLE_NAME, getLocale(request))
                .getString(translationKey);
    }

    protected void sendErrorMessage(UserRequest request, String translationKey) {
        telegramService.sendMessage(request.getChatId(),
                getTranslation(request, translationKey));
    }

    protected void sendSelectYourPlansMessage(UserRequest request) {
        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardHelper.buildPlanSelectionMenuWithBack(getLocale(request));
        telegramService.sendMessage(request.getChatId(), getTranslation(request, "yourPlans"), replyKeyboardMarkup);
    }

}