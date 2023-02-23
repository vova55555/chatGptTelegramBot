package org.hoshta.handler.impl;

import org.hoshta.enums.SupportedLanguages;
import org.hoshta.handler.UserRequestHandler;
import org.hoshta.helper.KeyboardHelper;
import org.hoshta.model.UserRequest;
import org.hoshta.service.TelegramService;
import org.hoshta.service.UserSessionService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import static org.hoshta.enums.ConversationState.WAITING_FOR_LANGUAGE;
import static org.hoshta.enums.ConversationState.WAITING_FOR_PLANS;

@Component
public class LanguageSelectionHandler extends UserRequestHandler {
    private final SupportedLanguages[] values = SupportedLanguages.values();

    public LanguageSelectionHandler(UserSessionService userSessionService, TelegramService telegramService,
                                    KeyboardHelper keyboardHelper) {
        super(userSessionService, telegramService, keyboardHelper);
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        return isTextMessage(request.getUpdate()) && Objects.equals(getState(request), WAITING_FOR_LANGUAGE);
    }

    @Override
    public void handle(UserRequest request) {
        String messageText = request.getUpdate().getMessage().getText();
        if (isMessageSupportedLanguage(messageText) || Objects.equals(messageText, getTranslation(request, "backBtn"))) {
            Locale selectedLocale = new Locale(getSelectedLanguageCode(messageText));
            setUserSessionStateAndLocale(request, selectedLocale, WAITING_FOR_PLANS);
            sendSelectYourPlansMessage(request);
        } else {
            sendErrorMessage(request, "errorMessageLanguageSelection");
        }
    }

    @Override
    public boolean isGlobal() {
        return true;
    }

    private String getSelectedLanguageCode(String messageText) {
        return Arrays.stream(values).filter(sl -> Objects.equals(sl.getButtonText(), messageText))
                .findFirst().get().name();
    }

    private boolean isMessageSupportedLanguage(String messageText) {
        return Arrays.stream(values).anyMatch(sl -> Objects.equals(sl.getButtonText(), messageText));
    }
}
