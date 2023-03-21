package org.hoshta.service;

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.hoshta.model.UserRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class OpenAiCustomService {

    @Value("${CHAT_GPT_TOKEN}")
    private String openAiToken;

    @Value("${open-ai.timeout-in-seconds}")
    private long timeOut;

    @Value("${open-ai.davinchi-model}")
    private String daVinchiModel;

    @Value("${open-ai.chat-model}")
    private String chatModel;

    @Value("${open-ai.max-tokens}")
    private int maxTokens;
    private OpenAiService openAiService;

    public String completionRequestOpenAi(UserRequest request) throws Exception {
        CompletionRequest openAIRequest = CompletionRequest.builder()
                .prompt(request.getUpdate().getMessage().getText())
                .model(daVinchiModel)
                .maxTokens(maxTokens)
                .build();
        CompletionResult response = getOpenAiService().createCompletion(openAIRequest);
        return response.getChoices().get(0).getText();
    }

    public String chatCompletionRequestOpenAi(UserRequest request) {
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), request.getUpdate().getMessage().getText());
        ChatCompletionRequest openAIRequest = ChatCompletionRequest.builder()
                .messages(List.of(chatMessage))
                .model(chatModel)
                .maxTokens(maxTokens)
                .build();
        ChatCompletionResult response = getOpenAiService().createChatCompletion(openAIRequest);
        return response.getChoices().get(0).getMessage().getContent();
    }

    public String createImageRequestOpenAi(String prompt) throws OpenAiHttpException {
        CreateImageRequest openAIRequest = CreateImageRequest.builder()
                .prompt(prompt)
                .build();
        return getOpenAiService().createImage(openAIRequest).getData().get(0).getUrl();
    }

    private OpenAiService getOpenAiService(){
        if (Objects.isNull(openAiService)) {
            openAiService = new OpenAiService(openAiToken, Duration.ofSeconds(timeOut));
        }
        return openAiService;
    }
}
