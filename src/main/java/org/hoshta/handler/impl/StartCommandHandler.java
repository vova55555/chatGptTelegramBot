package org.hoshta.handler.impl;

import lombok.AllArgsConstructor;
import org.hoshta.enums.ConversationState;
import org.hoshta.handler.UserRequestHandler;
import org.hoshta.helper.KeyboardHelper;
import org.hoshta.model.UserRequest;
import org.hoshta.model.UserSession;
import org.hoshta.service.TelegramService;
import org.hoshta.service.UserSessionService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.hoshta.constant.Constants.BUNDLE_NAME;

@Component
@AllArgsConstructor
public class StartCommandHandler extends UserRequestHandler {

    private static String command = "/start";

    private final UserSessionService userSessionService;
    private final KeyboardHelper keyboardHelper;
    private final TelegramService telegramService;


    @Override
    public boolean isApplicable(UserRequest userRequest) {
        return isCommand(userRequest.getUpdate(), command);
    }

    @Override
    public void handle(UserRequest request) {
        ReplyKeyboard replyKeyboard = keyboardHelper.buildSelectLanguageMenu();
        UserSession userSession = request.getUserSession();
        String userDefaultLanguage = request.getUpdate().getMessage().getFrom().getLanguageCode();
        Locale userLocale = userSession.getLocale() == null ? new Locale(userDefaultLanguage) : userSession.getLocale();
        telegramService.sendMessage(request.getChatId(),
                ResourceBundle.getBundle(BUNDLE_NAME, userLocale).getString("selectLanguage"),
                replyKeyboard);
        userSession.setState(ConversationState.WAITING_FOR_LANGUAGE);
        userSession.setLocale(userLocale);
        userSessionService.saveSession(request.getChatId(), userSession);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
