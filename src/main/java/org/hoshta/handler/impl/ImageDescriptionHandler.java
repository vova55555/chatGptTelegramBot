package org.hoshta.handler.impl;

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.service.OpenAiService;
import org.hoshta.handler.UserRequestHandler;
import org.hoshta.helper.KeyboardHelper;
import org.hoshta.model.UserRequest;
import org.hoshta.model.UserSession;
import org.hoshta.service.TelegramService;
import org.hoshta.service.UserSessionService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Locale;

import static org.hoshta.enums.ConversationState.WAITING_FOR_IMAGE_DESCRIPTION;

@Component
public class ImageDescriptionHandler extends UserRequestHandler {
    protected ImageDescriptionHandler(UserSessionService userSessionService, TelegramService telegramService, KeyboardHelper keyboardHelper) {
        super(userSessionService, telegramService, keyboardHelper);
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        return isTextMessage(request.getUpdate())
                && WAITING_FOR_IMAGE_DESCRIPTION.equals(getUserSession(request).getState());
    }

    @Override
    public void handle(UserRequest request) {
        try {
            UserSession userSession = getUserSession(request);
            String messageText = request.getUpdate().getMessage().getText();
            Long chatId = request.getChatId();
            String prompt;
            if (!messageText.equals(getTranslation(request, "regenerateBtn"))) {
                userSession.setText(messageText);
                userSessionService.saveSession(chatId, userSession);
                prompt = messageText;
            } else {
                prompt = userSession.getText();
            }
            URL imageUrl = new URL(requestOpenAi(request, prompt));
            BufferedImage image = ImageIO.read(imageUrl);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageToByteArray(image));
            InputFile inputFile = new InputFile(inputStream, "image.jpg");

            Locale locale = userSession.getLocale();
            ReplyKeyboardMarkup replyKeyboardMarkup = keyboardHelper.buildMenuWithBackAndRegenerateBtns(locale);
            telegramService.sendPhoto(chatId, inputFile, replyKeyboardMarkup);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean isGlobal() {
        return false;
    }

    public String requestOpenAi(UserRequest request, String prompt) {
        try {
            OpenAiService service = new OpenAiService(openAiToken, Duration.ofSeconds(timeOut));
            CreateImageRequest openAIRequest = CreateImageRequest.builder()
                    .prompt(prompt)
                    .build();
            return service.createImage(openAIRequest).getData().get(0).getUrl();
        } catch (OpenAiHttpException e) {
            e.printStackTrace();
            telegramService.sendMessage(request.getChatId(), e.getMessage());
            return getTranslation(request, "errorMessageChat");
        }
    }

    // Convert BufferedImage to byte array
    private static byte[] imageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        return baos.toByteArray();
    }
}
