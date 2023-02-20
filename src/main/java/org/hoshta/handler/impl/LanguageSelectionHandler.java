package org.hoshta.handler.impl;

import lombok.AllArgsConstructor;
import org.hoshta.enums.ConversationState;
import org.hoshta.enums.SupportedLanguages;
import org.hoshta.handler.UserRequestHandler;
import org.hoshta.helper.KeyboardHelper;
import org.hoshta.model.UserRequest;
import org.hoshta.model.UserSession;
import org.hoshta.service.TelegramService;
import org.hoshta.service.UserSessionService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.hoshta.constant.Constants.BUNDLE_NAME;

@Component
@AllArgsConstructor
public class LanguageSelectionHandler extends UserRequestHandler {


    private final TelegramService telegramService;
    private final KeyboardHelper keyboardHelper;
    private final UserSessionService userSessionService;

    @Override
    public boolean isApplicable(UserRequest userRequest) {
        return isTextMessage(userRequest.getUpdate()) &&
                ConversationState.WAITING_FOR_LANGUAGE.equals(userRequest.getUserSession().getState());
    }

    @Override
    public void handle(UserRequest userRequest) {
        SupportedLanguages[] values = SupportedLanguages.values();
        String messageText = userRequest.getUpdate().getMessage().getText();
        if (Arrays.stream(values).anyMatch(sl -> sl.getButtonText().equals(messageText))) {
            String selectedLanguageCode = Arrays.stream(values).filter(sl -> sl.getButtonText().equals(messageText))
                    .findFirst().get().name();
            Long chatId = userRequest.getChatId();
            Locale selectedLocale = new Locale(selectedLanguageCode);
            ReplyKeyboardMarkup replyKeyboardMarkup = keyboardHelper.buildMenuWithBack(selectedLocale);
            ResourceBundle translations = ResourceBundle.getBundle(BUNDLE_NAME, selectedLocale);
                    String greeting = translations.getString("greeting");
            String conversationTooltip =translations.getString("conversationTooltip");
            telegramService.sendMessage(chatId, greeting);
            telegramService.sendMessage(chatId, conversationTooltip, replyKeyboardMarkup);

            UserSession userSession = userRequest.getUserSession();
            userSession.setState(ConversationState.WAITING_FOR_TEXT);
            userSession.setLocale(selectedLocale);
            userSessionService.saveSession(chatId, userSession);
        } else {
            sendErrorMessage(userRequest);
        }
    }

    @Override
    public boolean isGlobal() {
        return true;
    }

    public void sendErrorMessage(UserRequest userRequest) {
        telegramService.sendMessage(userRequest.getChatId(),
                ResourceBundle.getBundle(BUNDLE_NAME, userRequest.getUserSession().getLocale()).getString("errorMessageLanguageSelection"));
    }
}
