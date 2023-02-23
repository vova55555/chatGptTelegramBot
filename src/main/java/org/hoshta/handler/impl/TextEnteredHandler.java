package org.hoshta.handler.impl;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.service.OpenAiService;
import org.hoshta.handler.UserRequestHandler;
import org.hoshta.helper.KeyboardHelper;
import org.hoshta.model.UserRequest;
import org.hoshta.service.TelegramService;
import org.hoshta.service.UserSessionService;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static org.hoshta.enums.ConversationState.WAITING_FOR_QUESTION;

@Component
public class TextEnteredHandler extends UserRequestHandler {

    public TextEnteredHandler(UserSessionService userSessionService,
                              TelegramService telegramService, KeyboardHelper keyboardHelper) {
        super(userSessionService, telegramService, keyboardHelper);
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        return isTextMessage(request.getUpdate())
                && WAITING_FOR_QUESTION.equals(getUserSession(request).getState());
    }

    @Override
    public void handle(UserRequest request) {
        String answer = requestOpenAi(request);
        telegramService.sendMessage(request.getChatId(), answer,
                keyboardHelper.buildMenuWithBackBtnOnly(getUserSession(request).getLocale()));
    }

    @Override
    public boolean isGlobal() {
        return false;
    }

    public String requestOpenAi(UserRequest request) {
        try {
            OpenAiService service = new OpenAiService(openAiToken, Duration.ofSeconds(timeOut));
            CompletionRequest openAIRequest = CompletionRequest.builder()
                    .prompt(request.getUpdate().getMessage().getText())
                    .model(daVinchiModel)
                    .maxTokens(maxTokens)
                    .build();
            CompletionResult response = service.createCompletion(openAIRequest);
            return response.getChoices().get(0).getText();
        } catch (Exception e) {
            e.printStackTrace();
            return getTranslation(request, "errorMessageChat");
        }
    }

}
