package org.hoshta.service;

import lombok.extern.slf4j.Slf4j;
import org.hoshta.sender.OpenAIChatBot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

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
        execute(sendMessage);
    }

    public void sendPhoto(Long chatId, InputFile photo, ReplyKeyboard replyKeyboard) {
        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(String.valueOf(chatId))
                .photo(photo)
                .replyMarkup(replyKeyboard)
                .build();
        execute(sendPhoto);
    }

    private void execute(SendMessage sendMessage) {
        try {
            botSender.execute(sendMessage);
        } catch (Exception e) {
            log.error("Exception: ", e);
        }
    }

    private void execute(SendPhoto sendPhoto) {
        try {
            botSender.execute(sendPhoto);
        } catch (Exception e) {
            log.error("Exception: ", e);
        }
    }
}
