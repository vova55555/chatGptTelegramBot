package org.hoshta.service;

import lombok.extern.slf4j.Slf4j;
import org.hoshta.sender.OpenAIChatBot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * This service allows to communicate with Telegram API
 */
@Slf4j
@Component
public class TelegramService {

    private final OpenAIChatBot botSender;

    public TelegramService(OpenAIChatBot botSender) {
        this.botSender = botSender;
    }

    public void sendMessage(Long chatId, String text) {
        sendMessage(chatId, text, null);
    }

    public void sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboard) {
        SendMessage sendMessage = SendMessage
                .builder()
                .text(text)
                .chatId(String.valueOf(chatId))
                .parseMode(ParseMode.HTML)
                .replyMarkup(replyKeyboard)
                .build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logErrorAdnInformUser(e, chatId);
        }
    }

    public void sendPhoto(Long chatId, InputFile photo, ReplyKeyboard replyKeyboard) {
        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(String.valueOf(chatId))
                .photo(photo)
                .replyMarkup(replyKeyboard)
                .build();
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            logErrorAdnInformUser(e, chatId);
        }
    }

    private void execute(SendMessage sendMessage) throws TelegramApiException {
        botSender.execute(sendMessage);
    }

    private void execute(SendPhoto sendPhoto) throws TelegramApiException {
        botSender.execute(sendPhoto);
    }

    public void logErrorAdnInformUser(Exception e, Long chatId) {
        String errorMessage = e.getMessage();
        log.error(errorMessage);
        e.printStackTrace();
        sendMessage(chatId, errorMessage);
    }
}
