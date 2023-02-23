package org.hoshta.service;

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.hoshta.model.UserRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

@Slf4j
@Component
public class OpenAiCustomService {

    @Value("${open-ai.token}")
    private String openAiToken;

    @Value("${open-ai.timeout-in-seconds}")
    private long timeOut;

    @Value("${open-ai.davinchi-model}")
    private String daVinchiModel;

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
