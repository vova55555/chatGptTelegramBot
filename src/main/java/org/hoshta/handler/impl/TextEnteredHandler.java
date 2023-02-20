package org.hoshta.handler.impl;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.service.OpenAiService;
import lombok.AllArgsConstructor;
import org.hoshta.enums.ConversationState;
import org.hoshta.handler.UserRequestHandler;
import org.hoshta.helper.KeyboardHelper;
import org.hoshta.model.UserRequest;
import org.hoshta.model.UserSession;
import org.hoshta.service.TelegramService;
import org.hoshta.service.UserSessionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.time.Duration;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.hoshta.constant.Constants.BUNDLE_NAME;

@Component
public class TextEnteredHandler extends UserRequestHandler {

    @Value("${open-ai.token}")
    private String openAiToken;

    @Value("${open-ai.timeout-in-seconds}")
    private long timeOut;

    @Value("${open-ai.davinchi-model}")
    private String daVinchiModel;

    @Value("${open-ai.max-tokens}")
    private int maxTokens;

    private final TelegramService telegramService;
    private final KeyboardHelper keyboardHelper;
    private final UserSessionService userSessionService;

    public TextEnteredHandler(TelegramService telegramService, KeyboardHelper keyboardHelper, UserSessionService userSessionService) {
        this.telegramService = telegramService;
        this.keyboardHelper = keyboardHelper;
        this.userSessionService = userSessionService;
    }

    @Override
    public boolean isApplicable(UserRequest userRequest) {
        return isTextMessage(userRequest.getUpdate())
                && ConversationState.WAITING_FOR_TEXT.equals(userRequest.getUserSession().getState());
    }

    @Override
    public void handle(UserRequest userRequest) {
        UserSession session = userRequest.getUserSession();
        Locale locale = session.getLocale();
        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardHelper.buildMenuWithBack(locale);
        String text = userRequest.getUpdate().getMessage().getText();
        String answer = requestOpenAi(text, locale);
        telegramService.sendMessage(userRequest.getChatId(), answer, replyKeyboardMarkup);

        session.setText(text);
        userSessionService.saveSession(userRequest.getChatId(), session);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }

    public String requestOpenAi(String question, Locale locale) {
        try {
            OpenAiService service = new OpenAiService(openAiToken, Duration.ofSeconds(timeOut));
            CompletionRequest request = CompletionRequest.builder()
                    .prompt(question)
                    .model(daVinchiModel)
                    .maxTokens(maxTokens)
                    .build();
            CompletionResult response = service.createCompletion(request);
            return response.getChoices().get(0).getText();
        } catch (Exception e) {
            e.printStackTrace();
            return   ResourceBundle.getBundle(BUNDLE_NAME, locale).getString("errorMessageChat");
        }
    }

}
