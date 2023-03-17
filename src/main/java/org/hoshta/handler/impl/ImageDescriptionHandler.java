package org.hoshta.handler.impl;

import org.hoshta.handler.UserRequestHandler;
import org.hoshta.helper.KeyboardHelper;
import org.hoshta.model.UserRequest;
import org.hoshta.model.UserSession;
import org.hoshta.service.OpenAiCustomService;
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
import java.util.Objects;

import static org.hoshta.enums.ConversationState.WAITING_FOR_IMAGE_DESCRIPTION;

@Component
public class ImageDescriptionHandler extends UserRequestHandler {
    private final OpenAiCustomService openAiCustomService;

    public ImageDescriptionHandler(UserSessionService userSessionService, TelegramService telegramService, KeyboardHelper keyboardHelper, OpenAiCustomService openAiCustomService) {
        super(userSessionService, telegramService, openAiCustomService, keyboardHelper);
        this.openAiCustomService = openAiCustomService;
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        return isTextMessage(request.getUpdate()) && Objects.equals(getState(request), WAITING_FOR_IMAGE_DESCRIPTION);
    }

    @Override
    public void handle(UserRequest request) {
        UserSession userSession = getUserSession(request);
        String messageText = request.getUpdate().getMessage().getText();
        Long chatId = request.getChatId();
        String prompt;
        if (!Objects.equals(messageText, getTranslation(request, "regenerateBtn"))) {
            userSession.setText(messageText);
            userSessionService.saveSession(userSession);
            prompt = messageText;
        } else {
            prompt = userSession.getText();
        }
        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardHelper.buildMenuWithBackAndRegenerateBtns(getLocale(request));
        try {
            InputFile inputFile = getImageInputFile(request, prompt);
            telegramService.sendPhoto(chatId, inputFile, replyKeyboardMarkup);
        } catch (Exception e) {
            telegramService.logErrorAdnInformUser(e, chatId);
        }
    }

    @Override
    public boolean isGlobal() {
        return false;
    }

    // Convert BufferedImage to byte array
    private static byte[] imageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        return baos.toByteArray();
    }

    private InputFile getImageInputFile(UserRequest request, String prompt) throws Exception {
        URL imageUrl = new URL(openAiCustomService.createImageRequestOpenAi(prompt));
        BufferedImage image = ImageIO.read(imageUrl);
        byte[] bytes = imageToByteArray(image);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        return new InputFile(inputStream, "image.jpg");
    }
}
